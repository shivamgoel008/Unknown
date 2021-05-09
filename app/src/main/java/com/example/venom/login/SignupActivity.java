package com.example.venom.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import com.example.venom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText signupName, signupEmail, signupPassword, signupConfirmPassword;
    private Button signupButton;
    private String name, email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = (TextInputEditText) findViewById(R.id.signup_name);
        signupEmail = (TextInputEditText) findViewById(R.id.signup_email);
        signupPassword = (TextInputEditText) findViewById(R.id.signup_password);
        signupConfirmPassword = (TextInputEditText) findViewById(R.id.signup_confirm_password);
        signupButton = (Button) findViewById(R.id.signup_button);


        signupButton.setOnClickListener(v -> {
            name = signupName.getText().toString().trim();
            email = signupEmail.getText().toString().trim();
            password = signupPassword.getText().toString().trim();
            confirmPassword = signupConfirmPassword.getText().toString().trim();

            if (name.equals("")) {
                signupName.setError("Enter Valid Name");
            }

            else if (email.equals("")) {
                signupEmail.setError("Enter Valid Email");
            }

            else if (password.equals("")) {
                signupPassword.setError("Enter String Password");
            }

            else if (!(confirmPassword.equals(password))) {
                signupConfirmPassword.setError("Password Don't Match");
            }

            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                signupEmail.setError("Enter Correct Email");
            }

            else {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                        }
                        else {
                            Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}