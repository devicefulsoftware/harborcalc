package com.devicefulsoftware.harborcalc_free;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bsh.Interpreter;

public class calculatorScreen extends AppCompatActivity {
    //Instantiate Global Variables
    boolean wasAnswered = false;
    String currentString = "0";
    int numLeftParen = 0;
    int numRightParen = 0;

    // The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private MovementDetector mDetector;

    public static final String HARBORCALC_PREFERENCES = "HarborCalc_Preferences";
    private  String HARBORCALC_THEME="HarborCalcDark";
    private boolean HARBORCALC_FIRSTRUN=true;
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mDetector);
        clearScreen();
        super.onPause();
    }
    @Override
    protected void onResume(){
        super.onPause();
        mSensorManager.registerListener(mDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check settings before displaying anything
        SharedPreferences themePreference = getSharedPreferences(HARBORCALC_PREFERENCES, 0);
        if (themePreference.getString("Theme","HarborCalcDark").equals("HarborCalcDark")){
            setTheme(R.style.Theme_HarborCalcDark);
        }else{
            setTheme(R.style.Theme_HarborCalcLight);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_calculator_screen);
        if (this.hasAccel()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            detectShake();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        SharedPreferences themePreference = getSharedPreferences(HARBORCALC_PREFERENCES, 0);
        MenuInflater inflater = getMenuInflater();
        if (themePreference.getString("Theme","HarborCalcDark").equals("HarborCalcDark")){
            inflater.inflate(R.menu.mainmenu_dark, menu);
        }else{
            inflater.inflate(R.menu.mainmenu_light, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        SharedPreferences themePreference = getSharedPreferences(HARBORCALC_PREFERENCES, 0);
        SharedPreferences.Editor editor = themePreference.edit();
        switch (item.getItemId()){

            case R.id.about:
                startActivity(new Intent(calculatorScreen.this, aboutScreen.class));
                return super.onOptionsItemSelected(item);

            case R.id.light_theme:
                editor.putString("Theme","HarborCalcLight");
                editor.apply();
                finish();
                startActivity(new Intent(this, this.getClass()));
                return super.onOptionsItemSelected(item);

            case R.id.dark_theme:
                editor.putString("Theme","HarborCalcDark");
                editor.apply();
                finish();
                startActivity(new Intent(this, this.getClass()));
                return super.onOptionsItemSelected(item);

            case R.id.update:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devicefulsoftware.com/download/harborcalc-free/"));
                startActivity(browserIntent);
                return super.onOptionsItemSelected(item);

            case R.id.exit:
                this.exitApplication();
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);

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
    public void holdLogo(View view){
        ImageView imageView = (ImageView)findViewById(R.id.imageLogo);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //On long click
                TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
                if (calcScreen.getText().toString().equals("11301982")){
                    startActivity(new Intent(calculatorScreen.this, hiddenMenu.class));
                    calcScreen.setText("0");
                }
                return true;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Regular click
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devicefulsoftware.com"));
                startActivity(browserIntent);
            }
        });
    }
    public void exitApplication(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_NEGATIVE:
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        finish();
                        System.exit(0);
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you wish to exit?").setNegativeButton("Yes", dialogClickListener).setPositiveButton("No", dialogClickListener).show();
    }
    public void setWasAnswered(View view){
        if (this.wasAnswered){
            this.buttonAc_click(view);
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
                TextView calcScreen = (TextView)findViewById(R.id.textView_calc);
                clearScreen();
                mSensorManager.registerListener(mDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            }
        });
    }
    public String doMath(String expression){
        //Ensure number gets handled as a double so the answer gives remainders
        String newExpression = "1.0*"+expression;

        //Ensure there is an operator before/after each parenthesis
        for (int i = newExpression.length() - 1; i >= 0;i--){
            if (String.valueOf(newExpression.charAt(i)).equals("(")){
                if ( i > 1){
                    if (!isOperator(String.valueOf(newExpression.charAt(i-1)))){
                        newExpression = newExpression.substring(0,i) + "*" + newExpression.substring(i, newExpression.length());
                    }
                }
            }else{
                if (String.valueOf(newExpression.charAt(i)).equals(")")){
                    if ( i < newExpression.length()-1){
                        if (!isOperator(String.valueOf(newExpression.charAt(i+1)))){
                            newExpression = newExpression.substring(0,i+1) + "*" + newExpression.substring(i+1, newExpression.length());
                        }
                    }
                }
            }
        }
        //Interpret the answer
        Interpreter bsh = new Interpreter();
        try{
            String answer = bsh.eval(newExpression).toString();
            //Round the answer to 5 decimal places
            double factor = 1e5;
            double doubleAnswer = Math.round(Double.valueOf(answer) * factor ) / factor;
            answer=Double.toString(doubleAnswer);
            if (String.valueOf(answer.charAt(answer.length() - 1)).equals("0")){
                if (String.valueOf(answer.charAt(answer.length() - 2)).equals(".")){
                    answer = answer.substring(0, answer.length() - 2);
                }
            }
            if (answer.equals("9.223372036854777E13")){
                playSnicker();
                return "Cant divide by zero!";
            }else{
                return answer;
            }
        }catch (Exception e){
            playSnicker();
            return "Does not compute!";
        }
    }
    public void playSnicker(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.snicker);
        mediaPlayer.start();
    }
    public void clearScreen(){
        TextView calcScreen = (TextView)findViewById(R.id.textView_calc);
        calcScreen.setText("0");
        calcScreen.setTextSize(48);
        calcScreen.setText("0");
        this.currentString = "0";
        this.numLeftParen = 0;
        this.numRightParen = 0;
        this.wasAnswered = false;
    }
    public boolean containsOperator(String text){
        boolean foundOperator = false;
        for (int i = 0; i < text.length() - 1;i++){
            if (this.isOperator(String.valueOf(text.charAt(i)))){
                foundOperator = true;
            }
        }
        return foundOperator;
    }
    public boolean isOperator(String text){
        String lastChar = text.substring(text.length() - 1);
        boolean operator = false;
        switch (lastChar) {
            case "*":
                operator = true;
                break;
            case "/":
                operator = true;
                break;
            case "+":
                operator = true;
                break;
            case "-":
                operator = true;
                break;
        }
        return operator;
    }
    public void button0_click(View view){
        //What happens when button 0 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( !calcScreen.getText().toString().equals("0") ){
            calcScreen.setText(currentString + "0");
        }
        currentString = calcScreen.getText().toString();
    }
    public void button1_click(View view){
        //What happens when button 1 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ){
            calcScreen.setText("1");
        }else{
            calcScreen.setText(currentString + "1");
        }
        currentString = calcScreen.getText().toString();
    }
    public void button2_click(View view){
        //What happens when button 2 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ){
            calcScreen.setText("2");
        }else{
            calcScreen.setText(currentString + "2");
        }
        currentString = calcScreen.getText().toString();
    }
    public void button3_click(View view){
        //What happens when button 3 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ){
            calcScreen.setText("3");
        }else{
            calcScreen.setText(currentString + "3");
        }
        currentString = calcScreen.getText().toString();
    }
    public void button4_click(View view){
        //What happens when button 4 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ){
            calcScreen.setText("4");
        }else{
            calcScreen.setText(currentString + "4");
        }
        currentString = calcScreen.getText().toString();
    }
    public void button5_click(View view){
        //What happens when button 5 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ){
            calcScreen.setText("5");
        }else{
            calcScreen.setText(currentString + "5");
        }
        currentString = calcScreen.getText().toString();
    }
    public void button6_click(View view){
        //What happens when button 6 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ){
            calcScreen.setText("6");
        }else{
            calcScreen.setText(currentString + "6");
        }
        currentString = calcScreen.getText().toString();
    }
    public void button7_click(View view){
        //What happens when button 7 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ){
            calcScreen.setText("7");
        }else{
            calcScreen.setText(currentString + "7");
        }
        currentString = calcScreen.getText().toString();
    }
    public void button8_click(View view){
        //What happens when button 8 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ){
            calcScreen.setText("8");
        }else{
            calcScreen.setText(currentString + "8");
        }
        currentString = calcScreen.getText().toString();
    }
    public void button9_click(View view){
        //What happens when button 9 is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ){
            calcScreen.setText("9");
        }else{
            calcScreen.setText(currentString + "9");
        }
        currentString = calcScreen.getText().toString();
    }
    public void buttonDecimal_click(View view){
        //What happens when decimal button is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        String lastChar = calcScreen.getText().toString().substring(calcScreen.getText().toString().length() - 1);
        if (!lastChar.equals(".")){
            calcScreen.setText(currentString + ".");
        }
        currentString = calcScreen.getText().toString();
    }
    public void buttonEquals_click(View view){
        //What happens when equals button is pressed
        String answer = "";
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if (this.containsOperator(calcScreen.getText().toString())){
            this.setWasAnswered(view);
            answer = this.doMath(calcScreen.getText().toString());
            calcScreen.setText(answer);
            currentString = calcScreen.getText().toString();
            this.wasAnswered = true;
        }
    }
    public void buttonPlus_click(View view){
        //What happens when plus button is pressed
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if (this.isOperator(currentString)) {
            calcScreen.setText(calcScreen.getText().toString().substring(0, calcScreen.getText().toString().length() - 1));
        }
        calcScreen.setText(calcScreen.getText().toString() + "+");
        this.wasAnswered = false;
        currentString = calcScreen.getText().toString();
    }
    public void buttonMinus_click(View view){
        //What happens when minus button is pressed
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if (this.isOperator(currentString)){
            calcScreen.setText(calcScreen.getText().toString().substring(0,calcScreen.getText().toString().length() - 1));
        }else{
            if (calcScreen.getText().toString().equals("0")){
                calcScreen.setText("");
            }
        }
        calcScreen.setText(calcScreen.getText().toString() + "-");
        this.wasAnswered = false;
        currentString = calcScreen.getText().toString();
    }
    public void buttonMultiply_click(View view){
        //What happens when multiply button is pressed
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if (this.isOperator(currentString)) {
            calcScreen.setText(calcScreen.getText().toString().substring(0, calcScreen.getText().toString().length() - 1));
        }
        calcScreen.setText(calcScreen.getText().toString() + "*");
        this.wasAnswered = false;
        currentString = calcScreen.getText().toString();
    }
    public void buttonDivide_click(View view){
        //What happens when divide button is pressed
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if (this.isOperator(currentString)) {
            calcScreen.setText(calcScreen.getText().toString().substring(0, calcScreen.getText().toString().length() - 1));
        }
        calcScreen.setText(calcScreen.getText().toString() + "/");
        this.wasAnswered = false;
        currentString = calcScreen.getText().toString();
    }
    public void buttonAc_click(View view){
        //What happens when ac button is pressed
        clearScreen();
    }
    public void buttonBackspace_click(View view){
        //What happens when percent button is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if (calcScreen.getText().toString().length() > 1) {
            if (String.valueOf(calcScreen.getText().charAt(calcScreen.getText().toString().length()-1)).equals(")")){
                this.numRightParen--;
            }else{
                if (String.valueOf(calcScreen.getText().charAt(calcScreen.getText().toString().length()-1)).equals("(")){
                    this.numLeftParen--;
                }
            }
            calcScreen.setText(calcScreen.getText().toString().substring(0, calcScreen.getText().toString().length() - 1));
        }else{
            if (calcScreen.getText().toString().length() == 1){
                this.buttonAc_click(view);
            }
        }
        currentString = calcScreen.getText().toString();
    }
    public void buttonrightParen_click(View view){
        //What happens when button ) is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( !calcScreen.getText().toString().equals("0") ) {
            if ( this.numLeftParen > this.numRightParen){
                calcScreen.setText(currentString + ")");
                this.numRightParen += 1;
            }
        }
        currentString = calcScreen.getText().toString();
    }
    public void buttonleftParen_click(View view){
        //What happens when button ( is pressed
        this.setWasAnswered(view);
        Intent intent = new Intent(this, calculatorScreen.class);
        TextView calcScreen = (TextView) findViewById(R.id.textView_calc);
        if ( calcScreen.getText().toString().equals("0") ) {
            calcScreen.setText("(");
        }else{
            calcScreen.setText(currentString + "(");
        }
        this.numLeftParen += 1;
        currentString = calcScreen.getText().toString();
    }
}
