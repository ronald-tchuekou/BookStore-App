package com.roncoder.bookstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.ContactMessage;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {
    private List<ContactMessage> contactMessages;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick (int position);
    }
    public void setOnItemClickListener (OnItemClickListener listener) {
        this.listener = listener;
    }
    public ContactAdapter (List<ContactMessage> contactMessages) {
        this.contactMessages = contactMessages;
    }
    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        ContactMessage contactMessage = contactMessages.get(position);
        holder.name.setText(contactMessage.getSender());
        holder.last_message.setText(contactMessage.getLast_message());
        holder.date.setText(Utils.formatDate(contactMessage.getDate()));
        if (contactMessage.getNot_read_count() == 0){
            holder.not_read_count.setVisibility(View.GONE);
        }else{
            holder.not_read_count.setVisibility(View.VISIBLE);
            holder.not_read_count.setText(String.valueOf(contactMessage.getNot_read_count()));
        }
    }

    @Override
    public int getItemCount() {
        return contactMessages.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image_contact;
        TextView name, date, last_message, not_read_count;
        public ContactHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            image_contact = itemView.findViewById(R.id.image_contact);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            last_message = itemView.findViewById(R.id.last_message);
            not_read_count = itemView.findViewById(R.id.not_read_count);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onItemClick(position);
            });
        }
    }
}
