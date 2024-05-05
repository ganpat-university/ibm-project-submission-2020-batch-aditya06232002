package com.example.hushtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class chatWin extends AppCompatActivity {

    String receiverName, receiverUid, senderUid;
    TextView receiverNName;
    EditText textmsg;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    String senderRoom, receiverRoom;
    RecyclerView messageAdapter;
    ArrayList<msgModelclass> messagesArrayList;
    MessageAdapter madapter;
    String currenttime;
    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    private static final byte[] AES_SECRET_KEY = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        receiverName = getIntent().getStringExtra("namee");
        receiverUid = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        textmsg = findViewById(R.id.textmsg);
        receiverNName = findViewById(R.id.receivername);

        messageAdapter = findViewById(R.id.msgadapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        madapter = new MessageAdapter(chatWin.this, messagesArrayList);
        messageAdapter.setAdapter(madapter);

        receiverNName.setText(receiverName);

        senderUid = firebaseAuth.getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        msgModelclass message = dataSnapshot.getValue(msgModelclass.class);
                        if (message != null) {
                            messagesArrayList.add(message);
                        }
                    }
                }
                madapter.notifyDataSetChanged();
                scrollToBottom();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findViewById(R.id.sendbtnn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textmsg.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(chatWin.this, "Please type a message", Toast.LENGTH_SHORT).show();
                } else {
                    Date date = new Date();
                    currenttime = simpleDateFormat.format(calendar.getTime());
                    try {
                        String encryptedMessage = encryptMessage(message);
                        msgModelclass newMessage = new msgModelclass(encryptedMessage, firebaseAuth.getUid(), date.getTime(), currenttime);

                        database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(newMessage)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(newMessage)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                textmsg.setText(""); // Clear input field after sending message
                                                                scrollToBottom();
                                                            } else {
                                                                Toast.makeText(chatWin.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(chatWin.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(chatWin.this, "Encryption failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Method to scroll the RecyclerView to its bottom
    private void scrollToBottom() {
        if (!messagesArrayList.isEmpty()) {
            messageAdapter.post(() -> messageAdapter.smoothScrollToPosition(messagesArrayList.size() - 1));
        }
    }

    // Method to encrypt a message using AES
    private String encryptMessage(String message) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(AES_SECRET_KEY, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return new String(Base64.getEncoder().encode(encryptedBytes));
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
            Intent intent = new Intent(chatWin.this, chat.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.set) {
            Intent intent = new Intent(chatWin.this, settings.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.log) {
            Intent intent = new Intent(chatWin.this, Login.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
