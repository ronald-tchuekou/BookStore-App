package com.roncoder.bookstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.Message;
import com.roncoder.bookstore.models.MessageCmd;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_MESSAGE_RECEIVED = 0;
    public static final int ITEM_MESSAGE_SEND = 1;
    public static final int ITEM_MESSAGE_CMD_RECEIVED = 2;
    public static final int ITEM_MESSAGE_CMD_SEND = 3;
    private List<Message> messages;
    private Context context;
    private FirebaseAuth auth;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == ITEM_MESSAGE_RECEIVED) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_recieved, parent, false);
            return new MessageHolder(v);
        } else if (viewType == ITEM_MESSAGE_CMD_RECEIVED) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_cmd_recieved, parent, false);
            return new MessageCmdHolder(v);
        }else if (viewType == ITEM_MESSAGE_CMD_SEND) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_cmd_send, parent, false);
            return new MessageCmdHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_send, parent, false);
            return new MessageHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder instanceof MessageHolder) {
            MessageHolder sendHolder = (MessageHolder) holder;
            sendHolder.date.setText(Utils.getDate(message.getDate()));
            sendHolder.time.setText(Utils.getTime(message.getDate()));
            sendHolder.text.setText(message.getText());
            if (message.getId() != null)
                sendHolder.sms_indicator.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.ic_sms_send, null));
        }
        else {
            MessageCmd messageCmd = message.getMessage_cmd();
            MessageCmdHolder cmdHolder = (MessageCmdHolder) holder;
            cmdHolder.date.setText(Utils.getDate(message.getDate()));
            cmdHolder.time.setText(Utils.getTime(message.getDate()));
            cmdHolder.text.setText(message.getText());
            String quantity = context.getString(R.string.book_quantity) + messageCmd.getQuantity() + " " + context.getString(R.string.book);
            String total_prise = context.getString(R.string.total_prise) + Utils.formatPrise(messageCmd.getTotal_prise());
            cmdHolder.quantity.setText(quantity);
            cmdHolder.book_title.setText(messageCmd.getTitle());
            cmdHolder.total_prise.setText(total_prise);
            Glide.with(context)
                    .load(messageCmd.getImage())
                    .placeholder(R.drawable.bg_image)
                    .error(R.drawable.bg_image)
                    .into(cmdHolder.image_book);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (!message.getSender().equals(auth.getUid())) {
            if (message.getMessage_cmd() != null)
                return ITEM_MESSAGE_CMD_RECEIVED;
            return ITEM_MESSAGE_RECEIVED;
        } else {
            if (message.getMessage_cmd() != null)
                return ITEM_MESSAGE_CMD_SEND;
            return ITEM_MESSAGE_SEND;
        }
    }

    static class MessageHolder extends RecyclerView.ViewHolder {
        TextView date,time, text;
        ImageView sms_indicator;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_message);
            text = itemView.findViewById(R.id.text_message);
            time = itemView.findViewById(R.id.time_message);
            sms_indicator = itemView.findViewById(R.id.sms_indicator);
        }
    }

    static class MessageCmdHolder extends RecyclerView.ViewHolder {
        TextView date,time, text, book_title, quantity, total_prise;
        ImageView image_book;
        public MessageCmdHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_message);
            text = itemView.findViewById(R.id.text_message);
            time = itemView.findViewById(R.id.time_message);
            book_title = itemView.findViewById(R.id.book_title);
            quantity = itemView.findViewById(R.id.quantity);
            total_prise = itemView.findViewById(R.id.total_prise);
            image_book = itemView.findViewById(R.id.image_book);
        }
    }
}
