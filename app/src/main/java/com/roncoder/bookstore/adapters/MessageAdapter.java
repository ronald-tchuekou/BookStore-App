package com.roncoder.bookstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.Message;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_MESSAGE_RECEIVED = 0;
    public static final int ITEM_MESSAGE_SEND = 1;
    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_MESSAGE_RECEIVED) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_recieved, parent, false);
            return new MessageHolder(v);
        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_send, parent, false);
            return new MessageHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        MessageHolder sendHolder = (MessageHolder) holder;
        sendHolder.date.setText(Utils.getDate(message.getDate()));
        sendHolder.time.setText(Utils.getTime(message.getDate()));
        sendHolder.text.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getType().equals("received"))
            return ITEM_MESSAGE_RECEIVED;
        else
            return ITEM_MESSAGE_SEND;
    }

    static class MessageHolder extends RecyclerView.ViewHolder {
        TextView date,time, text;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_message);
            text = itemView.findViewById(R.id.text_message);
            time = itemView.findViewById(R.id.time_message);
        }
    }
//
//    static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
//        TextView date,time, text;
//        public ReceivedMessageHolder(@NonNull View itemView) {
//            super(itemView);
//            date = itemView.findViewById(R.id.date_message);
//            text = itemView.findViewById(R.id.text_message);
//            time = itemView.findViewById(R.id.time_message);
//        }
//    }
}
