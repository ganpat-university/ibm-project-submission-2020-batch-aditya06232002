package com.example.hushtalk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class signup extends AppCompatActivity {

    TextView loginbutton;
    EditText rg_username, rg_email, rg_password, rg_repassword;
    Button rgsignup;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    FirebaseStorage storage;
    DatabaseReference reference;

    /*@Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!= null){
            Intent intent = new Intent(signup.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        rgsignup = findViewById(R.id.signupbutton);
        loginbutton = findViewById(R.id.logbutton);
        rg_username = findViewById(R.id.rgusername);
        rg_email = findViewById(R.id.rgemail);
        rg_password = findViewById(R.id.rgpassword);
        rg_repassword = findViewById(R.id.rgrepassword);
        auth = FirebaseAuth.getInstance();

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        rgsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String username = rg_username.getText().toString();
                String emaill = rg_email.getText().toString();
                String password = rg_password.getText().toString();
                String cPassword = rg_repassword.getText().toString();
                String status = "Hey, I am using this application";




                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(emaill) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)) {
                    Toast.makeText(signup.this, "Please enter valid information", Toast.LENGTH_SHORT).show();
                } else if (!emaill.matches(emailPattern)) {
                    rg_email.setError("Type a valid email");
                } else if (password.length() < 6) {
                    rg_password.setError("Password must be 6 character or more");
                } else if (!password.equals(cPassword)) {
                    rg_password.setError("The passwords don't match");
                } else {
                    auth.createUserWithEmailAndPassword(emaill, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("users").child(uid);


                                users Users = new users(uid, username, emaill, password);
                                reference.setValue(Users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(signup.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(signup.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                // User created successfully, now log in the user
                            }
                        }
                    });
                }
            }
        });
    }
}
