package com.example.blocad.AuthPages;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blocad.MainActivity;
import com.example.blocad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText editTextPassword;
    EditText editTextEmail;
    Button loginBtn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailTextBoxLogin);
        editTextPassword = findViewById(R.id.passwordTextBoxLogin);
        loginBtn = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBarLogin);
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password;
                email = String.valueOf(editTextEmail.getText()).trim();
                password = String.valueOf(editTextPassword.getText());

                progressBar.setVisibility(View.VISIBLE);


                //verificam daca casuta de email este goala
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Introdu email-ul",Toast.LENGTH_SHORT).show();
                    return;
                }

                //verificam daca casuta de parola este goala
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Introdu o parola", Toast.LENGTH_SHORT).show();
                    return;
                }

                //verificam daca formatul mail-ului este valid
                if(!email.matches(emailPattern))
                {
                    Toast.makeText(getApplicationContext(),"Introdu o adresa de email valida!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(LoginActivity.this, "Autentificare reusita!",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, "Autentificare esuata! Contul introdus nu exista. Verifica din nou datele!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        toAuth(); // trimite utilizatorul inapoi la meniul de autentificare
    }

    // modulul de trimitere inapoi la meniul de autentificare
    private void toAuth() {
        Button backToAuthButton = (Button) findViewById(R.id.backToAuthLogin);
        backToAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }
}
