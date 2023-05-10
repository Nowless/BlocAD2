package com.example.blocad.ApartamentPages;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blocad.MainPages.MainPageActivity;
import com.example.blocad.ProfilePages.CompleteProfileActivtiy;
import com.example.blocad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AssignAppartamentActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore fStore;
    EditText enterKey, ownerFirstName, ownerSurname;
    Spinner selectHouseholds, selectWaterMetersCount;
    Button assignButton;

    public static boolean validateLastName( String lastName ) {
        if(lastName.matches("[a-zA-Z]+"))
            return true;
        return false;
    }

    public static boolean validateFirstName( String firstName ) {
        if(firstName.matches("(?i)(^[a-z])((?![ .,'-]$)[a-z .,'-]){0,24}$"))
            return true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_apartament_activity);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        enterKey = findViewById(R.id.editTextAssign);
        ownerFirstName = findViewById(R.id.ownerFirstNameEditText);
        ownerSurname = findViewById(R.id.ownerSurnameEditText);
        selectHouseholds = findViewById(R.id.householdsCountSpinner);
        //selectWaterMetersCount = findViewById(R.id.waterMetersCountSpinner);

        assignButton = findViewById(R.id.assignButton);

        Integer[] households = new Integer[]{1,2,3,4,5,6,7,8,9};
        Integer[] waterMetersCount = new Integer[]{1,2,3,4,5};


        CollectionReference appCollection = fStore.collection("Apartaments");

        ArrayAdapter<Integer> householdsAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, households);
        householdsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectHouseholds.setAdapter(householdsAdapter);

//        ArrayAdapter<Integer> waterMetersAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, waterMetersCount);
////        householdsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////        selectWaterMetersCount.setAdapter(waterMetersAdapter);



        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String key, firstName, surname;

                key = enterKey.getText().toString().trim();
                firstName = ownerFirstName.getText().toString().trim();
                surname = ownerSurname.getText().toString().trim();

                if(key.isEmpty())
                {
                    Toast.makeText(AssignAppartamentActivity.this, "Introdu o cheie!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                fStore.collection("Apartaments")
                        .whereEqualTo("securityKey", key)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                        if(!document.get("ownerUID").toString().equals("-1"))
                                        {
                                            Toast.makeText(AssignAppartamentActivity.this, "Acest apartament apartine unui alt cont!",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }


                                        if(firstName.isEmpty())
                                        {
                                            Toast.makeText(AssignAppartamentActivity.this, "Introdu un nume!",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        if(!validateFirstName(firstName))
                                        {
                                            Toast.makeText(AssignAppartamentActivity.this, "Introdu un nume format doar din litere!",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        if(surname.isEmpty())
                                        {
                                            Toast.makeText(AssignAppartamentActivity.this, "Introdu un prenume!",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        if(!validateLastName(surname))
                                        {
                                            Toast.makeText(AssignAppartamentActivity.this, "Introdu un prenume format doar din litere!",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Map<String,Object> appInfo = new HashMap<>(); // string-u de bagat in baza de date

                                        appInfo.put("ownerFirstname",firstName);
                                        appInfo.put("ownerSurname",surname);
                                        appInfo.put("householdsCount",selectHouseholds.getSelectedItem());
                                      //  appInfo.put("waterMetersCount",selectWaterMetersCount.getSelectedItem());

                                        appInfo.put("adminUID", document.get("adminUID"));
                                        appInfo.put("bloc",document.get("bloc"));
                                        appInfo.put("number",document.get("number"));
                                        appInfo.put("scara",document.get("scara"));
                                        appInfo.put("securityKey",document.get("securityKey"));
                                        appInfo.put("cod_postal",document.get("cod_postal"));
                                        appInfo.put("oras",document.get("oras"));
                                        appInfo.put("strada",document.get("strada"));
                                        appInfo.put("judet",document.get("judet"));
                                        appInfo.put("utilities",document.get("utilities"));
                                        appInfo.put("waterCount",document.get("waterCount"));
                                        appInfo.put("waterCountMonth",document.get("waterCountMonth"));
                                        appInfo.put("water_price",document.get("water_price"));



                                        DocumentReference appCollections = fStore.collection("Apartaments").document(document.getId());

                                        appCollections.set(appInfo);
                                        appCollections.update("ownerUID",mUser.getUid());

                                        DocumentReference userColections = fStore.collection("Users").document(mUser.getUid());

                                        userColections.update("appCount", FieldValue.increment(1));
                                        userColections.update("apps", FieldValue.arrayUnion(document.getId()));

                                        Intent startMainPage = new Intent(getApplicationContext(), MainPageActivity.class);
                                        startActivity(startMainPage);
                                        finish();

                                    }
                                } else {

                                    Toast.makeText(AssignAppartamentActivity.this, "Cheie apartament invalida. Verifica daca ai introdus corect cheia. Daca problema persista, te rog contacteaza administratorul.",
                                            Toast.LENGTH_LONG).show();

                                    Log.d(TAG, "Error getting documents: ", task.getException());

                                }
                            }
                        });

            }
        });


    }

}