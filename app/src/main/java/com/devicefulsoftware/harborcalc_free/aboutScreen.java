package com.devicefulsoftware.harborcalc_free;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by cletus on 1/26/17.
 */

public class aboutScreen extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePreference = getSharedPreferences(calculatorScreen.HARBORCALC_PREFERENCES, 0);
        if (themePreference.getString("Theme","HarborCalcDark").equals("HarborCalcDark")){
            setTheme(R.style.Theme_HarborCalcDark);
        }else{
            setTheme(R.style.Theme_HarborCalcLight);
        }
        setContentView(R.layout.activity_about_screen);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    public void clickLogo(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devicefulsoftware.com"));
        startActivity(browserIntent);
    }
}
