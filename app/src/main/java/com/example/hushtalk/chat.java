package com.example.hushtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class chat extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView recyclerView;
    UserAdapter Adapter;
    FirebaseDatabase database;
    ArrayList<users> usersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar();

       database = FirebaseDatabase.getInstance();
       auth = FirebaseAuth.getInstance();

        DatabaseReference reference = database.getReference().child("users");

        usersArrayList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    users users = dataSnapshot.getValue(users.class);

                    if(!users.getEmaill().equals(auth.getCurrentUser().getEmail())){
                        usersArrayList.add(users);
                    }

                }

                Adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Adapter = new UserAdapter(chat.this,usersArrayList);
        recyclerView.setAdapter(Adapter);

        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(chat.this,Login.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.chat){
            Intent intent = new Intent(chat.this, chat.class);
            startActivity(intent);
            finish();
        }

        if(id == R.id.set){
            Intent intent = new Intent(chat.this, settings.class);
            startActivity(intent);
            finish();
        }

        else if(id == R.id.log){
            Intent intent = new Intent(chat.this, Login.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}