package com.example.venom.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.venom.R;
import com.example.venom.common.NodeName;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText signupName, signupEmail, signupPassword, signupConfirmPassword;
    private Button signupButton;
    private String name, email, password, confirmPassword;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference fileStorage;
    private Uri localFileUri, serverFileUri;
    private ImageView profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = (TextInputEditText) findViewById(R.id.signup_name);
        signupEmail = (TextInputEditText) findViewById(R.id.signup_email);
        signupPassword = (TextInputEditText) findViewById(R.id.signup_password);
        signupConfirmPassword = (TextInputEditText) findViewById(R.id.signup_confirm_password);
        signupButton = (Button) findViewById(R.id.signup_button);
        profile = (ImageView) findViewById(R.id.user_image);

        fileStorage = FirebaseStorage.getInstance().getReference();


        signupButton.setOnClickListener(v -> {
            name = signupName.getText().toString().trim();
            email = signupEmail.getText().toString().trim();
            password = signupPassword.getText().toString().trim();
            confirmPassword = signupConfirmPassword.getText().toString().trim();

            if (name.equals("")) {
                signupName.setError("Enter Valid Name");
            } else if (email.equals("")) {
                signupEmail.setError("Enter Valid Email");
            } else if (password.equals("")) {
                signupPassword.setError("Enter String Password");
            } else if (!(confirmPassword.equals(password))) {
                signupConfirmPassword.setError("Password Don't Match");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                signupEmail.setError("Enter Correct Email");
            } else {

                // validation by email and password
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();   // to get the current user

//                            if(localFileUri!=null)
//                                updateNameAndPhoto();
//                            else {
                                Toast.makeText(SignupActivity.this, "ppp yeah", Toast.LENGTH_SHORT).show();
                                updateOnlyName();
//                            }

                            Toast.makeText(SignupActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        profile.setOnClickListener(v -> {
            pickImage(v);
        });

    }


    public void pickImage(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//         to start ans activity to pick the picture
        startActivityForResult(intent, 101);
    }

    //    overriding the startActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
//             we are checking with RESULT_OK for the case if user opens gallery but dont choose any photo
            if (requestCode == RESULT_OK) {
                localFileUri = data.getData();
                profile.setImageURI(localFileUri);
            }
        }
    }

    private void updateNameAndPhoto() {
        String strFileName = firebaseUser.getUid() + ".jpg";
        final StorageReference fileRef = fileStorage.child("images/" + strFileName);

        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverFileUri = uri;
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(signupName.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();
                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String userID = firebaseUser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");


                                        // putting all the values in the hashmap
                                        HashMap<String, String> hashMap = new HashMap<>();

                                        hashMap.put(NodeName.NAME, signupName.getText().toString().trim());
                                        hashMap.put(NodeName.EMAIL, signupEmail.getText().toString().trim());
                                        hashMap.put(NodeName.ONLINE, "true");
                                        hashMap.put(NodeName.PHOTO, serverFileUri.getPath());

                                        // putting the values in the realtime databse
                                        databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(SignupActivity.this, "User Created Successful", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                            }
                                        });
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Failed To Update %1$s", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    private void updateOnlyName() {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(signupName.getText().toString().trim())
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("User");


                    // putting all the values in the hashmap
                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put(NodeName.NAME, signupName.getText().toString().trim());
                    hashMap.put(NodeName.EMAIL, signupEmail.getText().toString().trim());
                    hashMap.put(NodeName.ONLINE, "true");
//                    if(serverFileUri.getPath()!=null)
//                    hashMap.put(NodeName.PHOTO, serverFileUri.getPath());

                    // putting the values in the realtime databse
                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "User Created Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(SignupActivity.this, "Failed To Update %1$s", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}