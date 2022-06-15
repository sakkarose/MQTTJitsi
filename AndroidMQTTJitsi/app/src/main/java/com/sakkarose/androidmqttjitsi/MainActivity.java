package com.sakkarose.androidmqttjitsi;

//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.os.Build;
import android.os.Bundle;
//import android.telephony.TelephonyManager;
//import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.NotificationCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    String dataReceived;
    final String room_code = "9000";
    final String topic = "alert";
    final String exit_code = "2";
    EditText editText;
    String text;
//    TelephonyManager tManager;
//    String UUID;
//    String clientId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.conferenceName);
        text = editText.getText().toString();

//        tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        UUID = tManager.getDeviceId();

        try {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//      dataReceived = (TextView) findViewById(R.id.dataReceived);
        startMqtt();
    }

    private void startMqtt(){

        mqttHelper = new MQTTHelper(getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Timber.tag("Debug").w("Connected");
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Timber.tag("Debug").w(mqttMessage.toString());
                dataReceived = mqttMessage.toString();
                Check4VideocallReq();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void onButtonClick(View v) {
        editText = findViewById(R.id.conferenceName);
        text = editText.getText().toString();
        if (text.length() > 0) {
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(text)
                    .build();
            JitsiMeetActivity.launch(this, options);
        }
    }

    public void OpenTheDoor(View v){
        try {
            mqttHelper.mqttAndroidClient.publish(topic, exit_code.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void Check4VideocallReq(){
        if (dataReceived.equals("1"))
        {
            editText.setText(room_code);
//            SendNoti1();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            mqttHelper.mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}