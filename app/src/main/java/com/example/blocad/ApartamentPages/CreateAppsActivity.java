package com.example.blocad.ApartamentPages;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blocad.MainPages.MainPageActivity;
import com.example.blocad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import kotlinx.coroutines.MainCoroutineDispatcher;

public class CreateAppsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner selectCity;

    FirebaseUser user;
    FirebaseFirestore fStore;
    FirebaseAuth auth;

    Button createApp, backToMain;

    EditText adress, postalCode, bloc, scara, number;

    EditText utilities, waterCost, waterCount, securityKey;

    boolean keyIsSafe = true;
    String s = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_apps_activity);

        selectCity = findViewById(R.id.citySpinnerCreate);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        createApp = findViewById(R.id.createButton);
        backToMain = findViewById(R.id.backToMain);

        adress = findViewById(R.id.adress);
        postalCode = findViewById(R.id.postalCodeEdit);
        bloc = findViewById(R.id.blocNumber);
        scara = findViewById(R.id.scara);
        number = findViewById(R.id.appNumber);

        utilities = findViewById(R.id.monthlyUtilitiesEdit);
        waterCost = findViewById(R.id.waterCostEdit);
        waterCount = findViewById(R.id.counterValue);
        securityKey = findViewById(R.id.securityKeyEdit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCity.setAdapter(adapter);
        selectCity.setOnItemSelectedListener(this);

        createApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adress.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Introdu o adresa!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(postalCode.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Introdu un cod postal!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(postalCode.getText().toString().trim().length()!=6)
                {
                    Toast.makeText(getApplicationContext(), "Un cod postal e format doar din 6 cifre!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(bloc.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Introdu un bloc!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(scara.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Introdu o scara!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(number.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Introdu un numar de apartament!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(utilities.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Introdu un cost al utitlitatilor!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(waterCost.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Introdu un cost al apei!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(waterCount.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Introdu o valoare a contorului!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(securityKey.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Introdu o cheie de securitate!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                fStore.collection("Apartaments")
                        .whereEqualTo("securityKey", securityKey.getText().toString().trim())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        keyIsSafe = false;
                                        s = document.getId().toString();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    keyIsSafe = true;
                                }
                            }
                        });

                if(!keyIsSafe)
                {
                    Toast.makeText(getApplicationContext(), "Cheia este deja folosita de apartamentul cu ID-ul: " + s,
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                Map<String,Object> appInfo = new HashMap<>();

                appInfo.put("judet", "Cluj");
                appInfo.put("adminUID", "-1");
                appInfo.put("ownerUID", "-1");
                appInfo.put("waterCountMonth", 0);
                appInfo.put("householdsCount", 0);

                appInfo.put("strada", adress.getText().toString().trim());
                appInfo.put("cod_postal", postalCode.getText().toString().trim());
                appInfo.put("oras", selectCity.getSelectedItem().toString());
                appInfo.put("number", number.getText().toString().trim());
                appInfo.put("scara", scara.getText().toString().trim());
                appInfo.put("bloc", bloc.getText().toString().trim());

                appInfo.put("waterCount", Float.parseFloat(waterCount.getText().toString()));
                appInfo.put("water_price", Float.parseFloat(waterCost.getText().toString()));
                appInfo.put("utilities", Float.parseFloat(utilities.getText().toString()));
                appInfo.put("securityKey", securityKey.getText().toString().trim());


                fStore.collection("Apartaments").add(appInfo)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                adress.setText("");
                                postalCode.setText("");
                                scara.setText("");
                                number.setText("");
                                bloc.setText("");
                                waterCost.setText("");
                                utilities.setText("");
                                securityKey.setText("");
                                waterCount.setText("");

                                Toast.makeText(getApplicationContext(), "Apartament creat cu succes!",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(toMain);
                finish();
            }
        });




    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}