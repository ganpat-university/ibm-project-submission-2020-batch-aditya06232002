package com.example.hushtalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class settings extends AppCompatActivity {

    // Firebase authentication instance
    private FirebaseAuth mAuth;

    // Reference to Firebase Database
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Find references to buttons
        Button phoneButton = findViewById(R.id.phoneButton);
        Button aboutButton = findViewById(R.id.aboutButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set onClick listener for the "Add Phone Number" button
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement functionality to add phone number
                showPhoneNumberPrompt();
            }
        });

        // Set onClick listener for the "About Hushtalk" button
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display about information
                displayAboutInfo();
            }
        });

        // Set onClick listener for the "Logout" button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout
                logout();
            }
        });
    }

    // Method to show prompt for adding phone number
    private void showPhoneNumberPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Phone Number");
        builder.setMessage("Please enter your phone number:");

        // Add input field for phone number
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phoneNumber = input.getText().toString().trim();
                // Update database with the entered phone number
                updatePhoneNumber(phoneNumber);
                Toast.makeText(settings.this, "Phone number added: " + phoneNumber, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }

    // Method to update phone number in the database
    private void updatePhoneNumber(String phoneNumber) {
        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Get the user ID of the current user
            String userId = currentUser.getUid();
            // Assuming "users" is the node where user information is stored
            // Update the phone number for the current user
            databaseRef.child("users").child(userId).child("phoneNumber").setValue(phoneNumber);
        } else {
            // User is not logged in, handle accordingly
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to display about information
    private void displayAboutInfo() {
        // Show about information in a toast
        Toast.makeText(this, "Hushtalk is a secure text transfer app having end to end encryption functionality.", Toast.LENGTH_LONG).show();
    }

    // Method to logout the user
    private void logout() {
        // Logout the user
        mAuth.signOut();
        // Navigate to the login activity
        Intent intent = new Intent(settings.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.chat) {
            Intent intent = new Intent(settings.this, chat.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.set) {
            Intent intent = new Intent(settings.this, settings.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.log) {
            Intent intent = new Intent(settings.this, Login.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
