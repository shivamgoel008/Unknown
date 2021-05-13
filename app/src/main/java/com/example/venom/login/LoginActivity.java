package com.example.venom.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.venom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity<cardviewSignup> extends AppCompatActivity {


    private TextInputEditText userPassword,userEmail;
    private CardView cardviewSignup;
    private String email,password;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail=(TextInputEditText)findViewById(R.id.person_email);
        userPassword =(TextInputEditText)findViewById(R.id.person_password);
        cardviewSignup=(CardView)findViewById(R.id.cardView_signup);
        loginButton=(Button)findViewById(R.id.login_button);


        loginButton.setOnClickListener(v -> {

            Toast.makeText(LoginActivity.this, "button clicked", Toast.LENGTH_SHORT).show();
            email=userEmail.getText().toString().trim();
            password=userPassword.getText().toString().trim();

            if(email.equals("")){
                userEmail.setError("Enter Email");
            }
            else if(userPassword.equals("")){
                userPassword.setError("Enter correct Password");
            }

            else{
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                        }

                        else{
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        cardviewSignup.setOnClickListener(v->{
            startActivity(new Intent(this,SignupActivity.class));
        });
    }

}