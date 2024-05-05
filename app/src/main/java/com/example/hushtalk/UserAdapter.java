package com.example.hushtalk;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {
    chat chat;
    ArrayList<users> usersArrayList;
    public UserAdapter(chat chat, ArrayList<users> usersArrayList) {
        this.chat = chat;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(chat).inflate(R.layout.chat_header,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {
        users users = usersArrayList.get(position);
        holder.username.setText(users.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chat, chatWin.class);
                intent.putExtra("namee",users.getName());
                intent.putExtra("uid",users.getUserId());
                chat.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {

        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView username;
        //ImageView profileimg;
        public viewholder(@NonNull View itemView) {

            super(itemView);
            username = itemView.findViewById(R.id.username);
        }
    }
}
