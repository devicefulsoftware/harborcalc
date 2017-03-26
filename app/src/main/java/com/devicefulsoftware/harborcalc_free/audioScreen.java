package com.devicefulsoftware.harborcalc_free;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by cletus on 2/12/17.
 */

public class audioScreen extends AppCompatActivity{
    //Custom Variables
    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private MovementDetector mDetector;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            startActivity(new Intent(audioScreen.this, hiddenMenu.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check settings before displaying anything
        SharedPreferences themePreference = getSharedPreferences(calculatorScreen.HARBORCALC_PREFERENCES, 0);
        if (themePreference.getString("Theme","HarborCalcDark").equals("HarborCalcDark")){
            setTheme(R.style.Theme_HarborCalcDark);
        }else{
            setTheme(R.style.Theme_HarborCalcLight);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_audio_screen);
        if (this.hasAccel()){
            this.detectShake();
        }
        /*File dir = new File("/");
        File[] filelist = dir.listFiles();
        ListView list = (ListView)findViewById(R.id.listAudio);
        ArrayList<String> arrayList = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);
        for(int i = 0; i < filelist.length;i++){
            arrayList.add(filelist[i].getName());
            adapter.notifyDataSetChanged();
        }*/
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mDetector);
        finish();
    }

    @Override
    protected void onResume(){
        mSensorManager.registerListener(mDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        super.onPause();
    }
    private void setLogo(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
    private boolean hasAccel(){
        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor != null)
        {
            return true;
        }else{
            return false;
        }
    }
    private void detectShake(){
        // mDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mDetector = new MovementDetector();
        mDetector.setMovementDetector(new MovementDetector.mDetector() {
            @Override
            public void onShake(int count) {
                onPause();
            }
        });
    }
}

