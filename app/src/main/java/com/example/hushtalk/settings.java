package com.example.hushtalk;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class settings extends AppCompatActivity {

    private TextView nameTextView;
    private Button changeEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        nameTextView = findViewById(R.id.receivername);
        changeEmailButton = findViewById(R.id.ecbutton);

        // Get current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get user's username (you might need to adjust this based on how you store the username)
            String username = currentUser.getDisplayName();
            if (username != null && !username.isEmpty()) {
                // Set user's username as text for the TextView
                nameTextView.setText(username);
            }
        }

        // Set click listener for change email button
        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeEmailDialog();
            }
        });
    }

    private void showChangeEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Email");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newEmail = input.getText().toString().trim();
                if (!newEmail.isEmpty()) {
                    updateEmail(newEmail);
                } else {
                    Toast.makeText(settings.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateEmail(final String newEmail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.verifyBeforeUpdateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Email updated successfully
                    // Re-authenticate user with new email
                    reauthenticateWithNewEmail(newEmail);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Email update failed
                    Toast.makeText(settings.this, "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void reauthenticateWithNewEmail(final String newEmail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.verifyBeforeUpdateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Email updated successfully
                    Toast.makeText(settings.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                    // Now you can navigate back to the login activity or any other activity
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Email update failed
                    Toast.makeText(settings.this, "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
