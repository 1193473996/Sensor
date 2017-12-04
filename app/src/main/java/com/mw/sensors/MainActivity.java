package com.mw.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private TextView showData;
    private Vibrator vibrator; //震动器
    private AudioTrack audioTrack;
    private AudioTrack aT[] = new AudioTrack[10];
    private byte wave[];
    private int Hz;
    private int waveLen;
    private int length;
    private int count = 0;
    private int errors = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showData = (TextView) findViewById(R.id.showData);

        // 获取传感器管理对象
        mSensorManager = (SensorManager) getSystemService(MainActivity.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(AcclerListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        /*for(int j=0;j<30;j++) {
            for (int i = 0; i < 200000000; i++) {

            }
            start(100*j);
        }*/
    }




    public void stopPlaying() {
        audioTrack.flush();
        audioTrack.stop();
        audioTrack.release();
    }

    private SensorEventListener AcclerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // 加速度可能会是负值，所以要取它们的绝对值
            float xValue = Math.abs(event.values[0]);
            float yValue = Math.abs(event.values[1]);
            float zValue = Math.abs(event.values[2]);
            String datas = new String();
            datas = "X:=" + String.valueOf(xValue) + "\n" +
                    "Y:=" + String.valueOf(yValue) + "\n" +
                    "Z:=" + String.valueOf(zValue) + "\n" +
                    "Count:" + String.valueOf(count) + '\n' +
                    "Errors:" + String.valueOf(errors) + "\n" +
                    String.valueOf(100*(int)xValue + 200*(int)yValue + 150*(int)zValue);
            showData.setText(datas);
            //start(100*(int)xValue + 200*(int)yValue + 150*(int)zValue);
            start(100*((int)xValue + (int)yValue + (int)zValue));
            //start(200);



        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    /**
     * 设置频率
     *
     * @param rate
     */
    public void start(int rate) {
        if (rate > 0) {
            Hz = rate;
            waveLen = 44100 / Hz;
            length = waveLen * Hz;

            /*aT[count % 10] = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                    AudioFormat.CHANNEL_CONFIGURATION_STEREO, // CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    length,
                    AudioTrack.MODE_STREAM);
            wave = new byte[length];
            wave = SinWave.sin(wave, waveLen, length);
            aT[count % 10].write(wave, 0 ,length);
            if (aT[count % 10] != null) {
                try {
                    aT[count % 10].play();
                    count++;
                } catch (IllegalStateException e) {
                    errors++;
                    e.printStackTrace();
                }
            }*/
            audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                length,
                AudioTrack.MODE_STREAM);
                //生成正弦波
                wave = new byte[length];
                wave = SinWave.sin(wave, waveLen, length);
                audioTrack.write(wave, 0 ,length);
                if(audioTrack!=null){
                    try{
                        audioTrack.play();
                        for(int i=0;i<10000000;i++){

                        }
                        stopPlaying();
                        count++;
                    }catch (IllegalStateException e){
                        System.gc();
                        errors++;
                        e.printStackTrace();
                    }
                }else{
                    return;
                }
            }
        }
    }
