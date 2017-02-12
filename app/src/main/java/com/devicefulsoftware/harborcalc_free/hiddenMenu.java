package com.devicefulsoftware.harborcalc_free;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by cletus on 1/11/17.
 */

public class hiddenMenu extends AppCompatActivity{
    private boolean hasOffspring_living = false;
    //Settings key for this particular activity
    private final static String HARBORCALC_HIDDEN_PREFERENCES = "HarborCalc_Hidden_Preferences";
    //Variables to be populated with saved settings
    private boolean HARBORCALC_ACCELEXISTS=false;
    private boolean HARBORCALC_FIRSTRUN=true;
    // The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private MovementDetector mDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePreference = getSharedPreferences(calculatorScreen.HARBORCALC_PREFERENCES, 0);
        if (themePreference.getString("Theme","HarborCalcDark").equals("HarborCalcDark")){
            setTheme(R.style.Theme_HarborCalcDark);
        }else{
            setTheme(R.style.Theme_HarborCalcLight);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_hidden_menu);
        this.loadSettings();
        if (this.hasAccel()){
            this.detectShake();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case R.id.about:
                startActivity(new Intent(hiddenMenu.this, aboutScreen.class));
                return super.onOptionsItemSelected(item);

            case R.id.exit:
                this.exitApplication();
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        SharedPreferences themePreference = getSharedPreferences(calculatorScreen.HARBORCALC_PREFERENCES, 0);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hiddenmenu, menu);
        return true;
    }
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mDetector);
        super.onPause();
        if (!this.hasOffspring_living){
            finish();
        }
    }
    @Override
    protected void onResume() {
        mSensorManager.registerListener(mDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        if (this.hasOffspring_living){
            finish();
            this.hasOffspring_living = false;
        }
        super.onPause();
    }
    private void detectShake(){
        // ShakeDetector initialization
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
    private void loadSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences(HARBORCALC_HIDDEN_PREFERENCES, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean accelExists= sharedPreferences.getBoolean("HARBORCALC_ACCELEXISTS", false);
        boolean firstRun= sharedPreferences.getBoolean("HARBORCALC_FIRSTRUN", true);

        if (firstRun){
            this.firstRun();
            editor.putBoolean("HARBORCALC_FIRSTRUN", false);
            editor.apply();
        }

        if (this.hasAccel()){
            this.HARBORCALC_ACCELEXISTS = true;
            editor.putBoolean("HARBORCALC_ACCELEXISTS", true);
        }

    }
    private void firstRun(){
        this.makeToast("Sneaky!");
        if(this.hasAccel()){
            this.makeToast("Tip: You can shake the screen to exit this menu!");
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
    private void makeToast(String message){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
    private void setLogo(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
    public void sendNotify(String message, String title) {
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_action_name).setContentTitle(title).setContentText(message);
        Intent intent = new Intent(this, calculatorScreen.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(calculatorScreen.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        SharedPreferences themePreference = getSharedPreferences(calculatorScreen.HARBORCALC_PREFERENCES, 0);
        if (themePreference.getString("Theme","HarborCalcDark").equals("HarborCalcDark")){
            mBuilder.setColor(getResources().getColor(R.color.harborcalc_green));
        }else{
            mBuilder.setColor(getResources().getColor(R.color.harborcalc_blue));
        }
        mBuilder.setSmallIcon(R.drawable.ic_stat_name);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }
    public void exitApplication(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                        System.exit(0);
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you wish to exit?").setNegativeButton("Yes", dialogClickListener).setPositiveButton("No", dialogClickListener).show();
    }
    public void contactsMenu(View view){
        mSensorManager.unregisterListener(mDetector);
        startActivity(new Intent(hiddenMenu.this, contactMenu.class));
        this.hasOffspring_living = true;
    }

}
