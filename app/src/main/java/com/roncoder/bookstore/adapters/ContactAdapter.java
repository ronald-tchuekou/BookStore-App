package com.roncoder.bookstore.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.ContactMessage;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {
    private static final String TAG = "ContactAdapter";
    private List<ContactMessage> contactMessages;
    private OnItemClickListener listener;
    private Context context;
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
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        ContactMessage contactMessage = contactMessages.get(position);
        holder.name.setText(contactMessage.getSender());
        UserHelper.getUserById(contactMessage.getSender()).addOnCompleteListener(com-> {
            if (!com.isSuccessful()) {
                Log.e(TAG, "onBindViewHolder: ", com.getException());
                return;
            }
            if (com.getResult() != null) {
                User user = com.getResult().toObject(User.class);
                assert user != null;
                String username = user.getName() + " " + user.getSurname();
                holder.name.setText(username);
                Glide.with(context)
                        .load(user.getProfile())
                        .error(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_account, null))
                        .placeholder(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_account, null))
                        .into(holder.image_contact);
            }
        });
        holder.last_message.setText(contactMessage.getLast_message().getText());
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
