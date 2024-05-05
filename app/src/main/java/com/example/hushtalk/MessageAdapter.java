package com.example.hushtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int ITEM_SEND = 1;
    int ITEM_RECEIVED = 2;

    private Context context;
    private ArrayList<msgModelclass> mAdapterArrayList;
    private final byte[] AES_SECRET_KEY = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6};

    public MessageAdapter(Context context, ArrayList<msgModelclass> mAdapterArrayList) {
        this.context = context;
        this.mAdapterArrayList = mAdapterArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_SEND) {
            view = LayoutInflater.from(context).inflate(R.layout.item_container_sent_layout, parent, false);
            return new SenderViewHolder(view);
        } else if (viewType == ITEM_RECEIVED) {
            view = LayoutInflater.from(context).inflate(R.layout.item_container_received_message, parent, false);
            return new ReceiverViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelclass message = mAdapterArrayList.get(position);

        // For the sender's view holder
        if (holder.getItemViewType() == ITEM_SEND) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.txtMessage1.setText(decryptMessage(message.getMessage())); // Decrypt here
            viewHolder.timeofmessage.setText(message.getCurrenttime());
        }
        // For the receiver's view holder
        else if (holder.getItemViewType() == ITEM_RECEIVED) {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.txtMessage.setText(decryptMessage(message.getMessage())); // Decrypt here
            viewHolder.timeofmessage.setText(message.getCurrenttime());
        }
    }

    @Override
    public int getItemCount() {
        return mAdapterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        msgModelclass message = mAdapterArrayList.get(position);
        // Checking if the message is sent by the current user
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVED;
        }
    }

    // View holder class for messages sent by the current user
    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage1;
        TextView timeofmessage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage1 = itemView.findViewById(R.id.sendermessage);
            timeofmessage = itemView.findViewById(R.id.timeofmessage);
        }
    }

    // View holder class for messages received by the current user
    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        TextView timeofmessage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.receivermessage);
            timeofmessage = itemView.findViewById(R.id.timeofmessage);
        }
    }

    // Method to decrypt a message using AES
    private String decryptMessage(String encryptedMessage) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
            SecretKeySpec secretKeySpec = new SecretKeySpec(AES_SECRET_KEY, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Decryption failed";
        }
    }
}
