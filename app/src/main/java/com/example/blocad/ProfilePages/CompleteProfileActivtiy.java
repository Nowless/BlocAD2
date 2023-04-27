package com.example.blocad.ProfilePages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.blocad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CompleteProfileActivtiy extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    TextView yourEmail;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_profile_actitvity);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        yourEmail = findViewById(R.id.yourEmailTextViewComplete);

        String plm = "Bine ai venit, " + user.getEmail().toString();

        yourEmail.setText(plm);


    }
}