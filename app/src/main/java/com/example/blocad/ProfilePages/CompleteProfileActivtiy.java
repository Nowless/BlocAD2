package com.example.blocad.ProfilePages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blocad.MainPages.MainPageActivity;
import com.example.blocad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CompleteProfileActivtiy extends AppCompatActivity implements AdapterView.OnItemSelectedListener   {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView yourEmail;
    Spinner selectCity;
    Button completeButton;
    EditText profileLastName, profileFirstName, profilePhoneNumber;
    FirebaseFirestore fStore;

    String apps[];



        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_profile_actitvity);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        yourEmail = findViewById(R.id.yourEmailTextViewComplete);

        selectCity = findViewById(R.id.citySpinnerComplete);
        profileFirstName = findViewById(R.id.firstNameEditTextComplete);
        profileLastName = findViewById(R.id.nameEditTextComplete);
        profilePhoneNumber = findViewById(R.id.phoneEditTextComplete);

        completeButton = findViewById(R.id.completeProfileButton);



        DocumentReference df = fStore.collection("Users").document(mUser.getUid()); // legatura cu baza de date si colectia


            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCity.setAdapter(adapter);
        selectCity.setOnItemSelectedListener(this);   //toate astea pt spinner


        String plm = "Bine ai venit, " + mUser.getEmail().toString();

        yourEmail.setText(plm);

            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //verificam daca casuta numelui e goala
                    if(profileLastName.getText().toString().isEmpty())
                    {
                        Toast.makeText(CompleteProfileActivtiy.this, "Introdu numele!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //verificam daca numele e valid
                    if(!lastName(profileLastName.getText().toString().trim()))
                    {
                        Toast.makeText(CompleteProfileActivtiy.this, "Introdu un nume valid, format doar din litere!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //verificam daca casuta prenumelui e goala
                    if(profileFirstName.getText().toString().isEmpty())
                    {
                        Toast.makeText(CompleteProfileActivtiy.this, "Introdu prenumele!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //verificam daca prenumele e valid
                    if(!firstName(profileFirstName.getText().toString().trim()))
                    {
                        Toast.makeText(CompleteProfileActivtiy.this, "Introdu un prenume valid, format doar din litere!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //verificam daca casuta numarului e goala
                    if(profilePhoneNumber.getText().toString().isEmpty())
                    {
                        Toast.makeText(CompleteProfileActivtiy.this, "Introdu numarul de telefon!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //verificam daca nr e valid
                    if(!phone(profilePhoneNumber.getText().toString()))
                    {
                        Toast.makeText(CompleteProfileActivtiy.this, "Introdu un numar valid, format din 10 cifre!",Toast.LENGTH_SHORT).show();
                        return;
                    }


                    Map<String,Object> userInfo = new HashMap<>(); // string-u de bagat in baza de date

                    userInfo.put("userEmail", mUser.getEmail().toString());
                    userInfo.put("city",selectCity.getSelectedItem().toString().trim());
                    userInfo.put("isAdmin", false);
                    userInfo.put("phoneNumber", profilePhoneNumber.getText().toString());
                    userInfo.put("name", profileLastName.getText().toString().trim());
                    userInfo.put("firstName", profileFirstName.getText().toString().trim());
                    userInfo.put("appCount", 0);
                    userInfo.put("apps","-");
                    userInfo.put("completeProfile", "A");

                    df.set(userInfo);

                    Intent goToMain = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(goToMain);
                    finish();

                }
            });


    }

    public static boolean phone( String z ) {
        if (z.matches("[0-9]+") && z.length() == 10)
            return true;
        return false;
    }

    public static boolean lastName( String lastName ) {
        if(lastName.matches("[a-zA-Z]+"))
            return true;
        return false;
    }

    public static boolean firstName( String firstName ) {
       if(firstName.matches("(?i)(^[a-z])((?![ .,'-]$)[a-z .,'-]){0,24}$"))
           return true;
       return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}