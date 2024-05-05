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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextView signup;
    Button button;
    EditText email,password;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    /*@Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!= null){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logbutton);
        signup = findViewById(R.id.signbutton);
        email = findViewById(R.id.editTextLogEmailAddress);
        password = findViewById(R.id.editTextLogPassword);
        auth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, signup.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String Password = password.getText().toString();

                if((TextUtils.isEmpty(Email))){
                    Toast.makeText(Login.this, "Enter the email", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(Password)){
                    Toast.makeText(Login.this, "Enter the password", Toast.LENGTH_SHORT).show();
                }else if(!Email.matches(emailPattern)){
                    email.setError("Give proper Email Address");
                }else if(Password.length()<6){
                    password.setError("Password should be more than 6 characters.");
                    Toast.makeText(Login.this, "Password needs to be longer than 6 characters", Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                try {
                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, chat.class);
                                    startActivity(intent);
                                    finish();
                                }catch(Exception e){
                                    Toast.makeText(Login.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });



    }
    public void checkUser() {
        String useremaill = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUserDatabase = reference.orderByChild("emaill").equalTo(useremaill);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    email.setError(null);

                    String passwordFromDB = snapshot.child(useremaill).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userPassword)) {
                        email.setError(null);

                        String nameFromDB = snapshot.child(useremaill).child("name").getValue(String.class);
                        String emailFromDB = snapshot.child(useremaill).child("emaill").getValue(String.class);

                        Intent intent = new Intent(Login.this, signup.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("emaill", emailFromDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);
                    } else {
                        password.setError("Invalid Credentials");
                        password.requestFocus();
                    }
                } else {
                    email.setError("User does not exist");
                    email.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}