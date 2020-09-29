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
import com.google.firebase.auth.FirebaseAuth;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.Conversation;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {
    private static final String TAG = "ContactAdapter";
    private List<Conversation> conversations;
    private OnItemClickListener listener;
    private Context context;
    private FirebaseAuth auth;
    public interface OnItemClickListener {
        void onItemClick (int position);
    }
    public void setOnItemClickListener (OnItemClickListener listener) {
        this.listener = listener;
    }
    public ContactAdapter (List<Conversation> conversations) {
        this.conversations = conversations;
        auth = FirebaseAuth.getInstance();
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
        Conversation conversation = conversations.get(position);
        String[] members = conversation.getMembers();
        String receiver = members[0].equals(auth.getUid()) ? members[1] : members[0];
        holder.name.setText(receiver);
        UserHelper.getUserById(receiver).addOnCompleteListener(com-> {
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
        holder.last_message.setText(conversation.getLast_message().getText());
        holder.date.setText(Utils.formatDate(conversation.getDate()));
        if (conversation.getNot_read_count1() == 0){
            holder.not_read_count.setVisibility(View.GONE);
        }else{
            holder.not_read_count.setVisibility(View.VISIBLE);
            holder.not_read_count.setText(String.valueOf(conversation.getNot_read_count1()));
        }
    }

    @Override
    public int getItemCount() {
        return conversations.size();
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
