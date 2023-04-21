package com.example.blocad.LoadPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.blocad.AuthPages.AuthActivity;
import com.example.blocad.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Thread thread = new Thread()
        {
            @Override
            public void run() {

                try{
                    sleep(3000);
                }catch(Exception e) {
                    e.printStackTrace();
                }finally {
                    startActivity(new Intent(SplashScreenActivity.this, AuthActivity.class));
                }
            }
        };
        thread.start();
    }
}