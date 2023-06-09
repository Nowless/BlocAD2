package com.example.blocad.AuthPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blocad.ProfilePages.CompleteProfileActivtiy;
import com.example.blocad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextPassword,editTextEmail,ediTextConfirmPass;
    Button buttonReg;
    CheckBox termsCheck;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseUser mUser;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.emailTextBoxRegister);
        editTextPassword = findViewById(R.id.passwordTextBoxRegister);
        ediTextConfirmPass = findViewById(R.id.confirmPassTextBox);

        buttonReg = findViewById(R.id.createAccButton);

        progressBar = findViewById(R.id.progressBarRegister);

        termsCheck = findViewById(R.id.termsCheckBoxRegister);


        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // vom folosi acest string pentru a verifica veridicitatea emailul
        final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"; // vom folosi acest string pentru a verifica daca parola respecta un format sigur




        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password, confirmPass;
                email = String.valueOf(editTextEmail.getText()).trim();
                password = String.valueOf(editTextPassword.getText());
                confirmPass = String.valueOf(ediTextConfirmPass.getText());

                progressBar.setVisibility(View.VISIBLE);

                //verificam daca casuta de email este goala
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Introdu email-ul",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //verificam daca casuta de parola este goala
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Introdu o parola", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //verificam daca casuta de confirmare parola este goala
                if(TextUtils.isEmpty(confirmPass)){
                    Toast.makeText(RegisterActivity.this, "Confirma parola!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //verificam daca parola se potriveste cu confirmarea ei
                if(!password.matches(confirmPass))
                {
                    Toast.makeText(getApplicationContext(), "Parolele nu se potrivesc", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //verificam daca formatul mail-ului este valid
                if(!email.matches(emailPattern))
                {
                    Toast.makeText(getApplicationContext(),"Adresa de email invalida", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //verificam validitatea formatului parolei
                if(!password.matches(passwordPattern))
                {
                    Toast.makeText(getApplicationContext(),"Parola invalida. Aceasta trebuie sa contina litere mici, cel putin o litera mare, un numeral, si sa aiba cel putin 6 caractere", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //verificam termenii si conditiile
                if(!termsCheck.isChecked())
                {
                        Toast.makeText(getApplicationContext(),"Pentru a creea un cont, trebuie sa fi de-acord cu termenii si condtiile", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    mUser = mAuth.getCurrentUser();

                                    Toast.makeText(RegisterActivity.this, "Cont creat cu succes!",
                                            Toast.LENGTH_SHORT).show();

                                    editTextEmail.getText().clear();
                                    ediTextConfirmPass.getText().clear();
                                    editTextPassword.getText().clear();

                                    DocumentReference df = fStore.collection("Users").document(mUser.getUid()); // legatura cu baza de date si colectia

                                    Map<String,Object> userInfo = new HashMap<>(); // string-u de bagat in baza de date

                                    userInfo.put("completeProfile", "F");

                                    df.set(userInfo);

                                    Intent startCompleteProfile = new Intent(getApplicationContext(), CompleteProfileActivtiy.class);
                                    startActivity(startCompleteProfile);
                                    finish();  //schimbam meniul


                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Înreistrare eșuată. Poate emailul este deja in uz!",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

        toAuth(); // trimite utilizatorul inapoi la meniul de autentificare
    }


    // metoda de trimitere inapoi la meniul de autentificare
    private void toAuth() {
        Button backToAuthButton = (Button) findViewById(R.id.backToAuthRegister);
        backToAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}