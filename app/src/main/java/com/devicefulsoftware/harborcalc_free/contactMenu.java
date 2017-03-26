package com.devicefulsoftware.harborcalc_free;

import android.annotation.SuppressLint;
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
/**
 * Created by cletus on 1/28/17.
 */

public class contactMenu extends AppCompatActivity{
    //Custom Variables
    // The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private MovementDetector mDetector;
    public final int PICK_CONTACT = 2017;
    private static final int RESULT_PICK_CONTACT = 85500;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            startActivity(new Intent(contactMenu.this, hiddenMenu.class));
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
        setContentView(R.layout.activity_contacts_menu);
        if (this.hasAccel()){
            this.detectShake();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mDetector);
        finish();
    }
    @SuppressLint("MissingSuperCall")
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

