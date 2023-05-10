package com.example.blocad.MainPages;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blocad.ApartamentPages.CreateAppsActivity;
import com.example.blocad.AuthPages.AuthActivity;
import com.example.blocad.ApartamentPages.AssignAppartamentActivity;
import com.example.blocad.ProfilePages.CompleteProfileActivtiy;
import com.example.blocad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.provider.FirebaseInitProvider;

import java.util.HashMap;
import java.util.Map;

public class MainPageActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button logout;
    Button toAssign;
    Button sendWater;
    Button toCreate;
    EditText waterCountMonth;
    TextView helloText,adressText,ownerText,waterText,householdsText,waterTotalText,utilitiesTotalText,waterThisMonth;
    ImageView appIcon;
    FirebaseUser user;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page_activity);

        appIcon = findViewById(R.id.imageView2);

        waterCountMonth = findViewById(R.id.waterCountMonthEditText);

        toAssign = findViewById(R.id.toAssignAppMain);
        logout = findViewById(R.id.logoutBtn);
        sendWater = findViewById(R.id.sendButtonMain);
        toCreate = findViewById(R.id.toCreateButton);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        adressText=findViewById(R.id.appAdressMain);
        ownerText=findViewById(R.id.appOwnerNameMain);
        waterText=findViewById(R.id.waterPayTotalMain);
        householdsText=findViewById(R.id.householdsCountMain);
        waterTotalText=findViewById(R.id.appWaterCountMain);
        utilitiesTotalText=findViewById(R.id.utilitesPayMain);
        helloText = findViewById(R.id.helloUserMain);
        waterThisMonth = findViewById(R.id.waterCountThisMonthMain);

        DocumentReference userReference = fStore.collection("Users").document(user.getUid()); // legatura cu baza de date si colectia



        if(user==null) {
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(intent);
            finish();
        }

        else{
            userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                String firstName, lastName;

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            if(!Boolean.parseBoolean(document.get("isAdmin").toString()))
                            {
                                toCreate.setVisibility(View.GONE);
                            }

                            if (document.get("completeProfile").toString().equals("F")) {
                                Intent toComplete = new Intent(getApplicationContext(), CompleteProfileActivtiy.class);
                                startActivity(toComplete);
                                finish();
                            }


                            else
                            {

                            firstName = document.get("firstName").toString();
                            lastName = document.get("name").toString();

                            if (Integer.parseInt(document.get("appCount").toString()) >= 1) {
                                toAssign.setVisibility(View.GONE);
                                helloText.setText("Salut, " + firstName + " " + lastName + ". Mai jos poti vedea detalii despre apartamentul tau.");

                               fStore.collection("Apartaments")
                                        .whereEqualTo("ownerUID", user.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot appDocument : task.getResult()) {
                                                        Log.d(TAG, appDocument.getId() + " => " + appDocument.getData());

                                                        adressText.setText("Judet " + appDocument.get("judet").toString() + ", loc. " + appDocument.get("oras") + ", str. " + appDocument.get("strada") + ", nr. " + appDocument.get("number") + ", bl. " + appDocument.get("bloc") + ", sc. " + appDocument.get("scara") + ", Cod Postal " + appDocument.get("cod_postal"));
                                                        ownerText.setText(appDocument.get("ownerFirstname") + " " + appDocument.get("ownerSurname"));
                                                        householdsText.setText("Nr. loc: " + appDocument.get("householdsCount").toString());
                                                        waterTotalText.setText("Consum total: " + appDocument.get("waterCount").toString());
                                                        waterText.setText("De platit apa pt luna in curs: " + Float.parseFloat(appDocument.get("waterCountMonth").toString()) * Float.parseFloat(appDocument.get("water_price").toString()) + " RON");
                                                        utilitiesTotalText.setText("De plata utilitati pt luna in curs: " + Float.parseFloat(appDocument.get("utilities").toString())* Float.parseFloat(appDocument.get("householdsCount").toString()) + " RON");

                                                        DocumentReference appReference = fStore.collection("Apartaments").document(appDocument.getId());

                                                        if (Float.parseFloat(appDocument.get("waterCountMonth").toString()) > 0) {
                                                            waterThisMonth.setVisibility(View.GONE);
                                                            sendWater.setVisibility(View.GONE);
                                                            waterCountMonth.setVisibility(View.GONE);
                                                        }


                                                        sendWater.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                if (waterCountMonth.getText().toString().isEmpty()) {
                                                                    Toast.makeText(getApplicationContext(), "Introdu consumul de pe luna aceasta in casuta de mai sus!",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }

                                                                appReference.update("waterCountMonth", Float.parseFloat(waterCountMonth.getText().toString()));
                                                                appReference.update("waterCount", FieldValue.increment(Float.parseFloat(waterCountMonth.getText().toString())));
                                                                waterText.setText("De platit apa pt luna in curs: " + Float.parseFloat(waterCountMonth.getText().toString()) * Float.parseFloat(appDocument.get("water_price").toString()) + " RON");
                                                                Toast.makeText(getApplicationContext(), "Consumul pe luna aceasta transmis cu succes!",
                                                                        Toast.LENGTH_SHORT).show();


                                                                waterTotalText.setText("Consum total: " + (Float.parseFloat(appDocument.get("waterCount").toString()) + Float.parseFloat(waterCountMonth.getText().toString())));

                                                                    waterThisMonth.setVisibility(View.GONE);
                                                                    sendWater.setVisibility(View.GONE);
                                                                    waterCountMonth.setVisibility(View.GONE);

                                                            }
                                                        });


                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());

                                                }
                                            }
                                        });
                            } else {
                                helloText.setText("Salut, " + firstName + " " + lastName + ". Apasă pe butonul 'Adauga un apartament' pentru a-ti putea însuși locuința.");
                                appIcon.setVisibility(View.GONE);
                                adressText.setVisibility(View.GONE);
                                ownerText.setVisibility(View.GONE);
                                householdsText.setVisibility(View.GONE);
                                waterText.setVisibility(View.GONE);
                                waterTotalText.setVisibility(View.GONE);
                                utilitiesTotalText.setVisibility(View.GONE);
                                waterThisMonth.setVisibility(View.GONE);
                                sendWater.setVisibility(View.GONE);
                                waterCountMonth.setVisibility(View.GONE);
                            }

                        }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });

        toAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAssign = new Intent(getApplicationContext(), AssignAppartamentActivity.class);
                startActivity(toAssign);
                finish();

            }
        });

        toCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCreate = new Intent(getApplicationContext(), CreateAppsActivity.class);
                startActivity(toCreate);
                finish();
            }
        });


    }



}