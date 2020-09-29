package com.roncoder.bookstore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.Chat;
import com.roncoder.bookstore.adapters.ContactAdapter;
import com.roncoder.bookstore.dbHelpers.MessageHelper;
import com.roncoder.bookstore.models.ContactMessage;

import java.util.ArrayList;
import java.util.List;

public class MessageChat extends Fragment {

    public static final String TAG = "MessageChat";
    public static final String CONTACT_MESSAGE_EXTRA = "contact_message";
    private static MessageChat instance = null;
    private List<ContactMessage> contactMessages;
    private ContactAdapter adapter;
    private FirebaseAuth auth;
    private TextView text_empty;
    private View empty;
    private RecyclerView recyclerView;

    public MessageChat() { }

    public static MessageChat getInstance() {
        if (instance == null)
            instance = new MessageChat();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_message, container, false);
        empty = root.findViewById(R.id.empty);
        text_empty = root.findViewById(R.id.text_empty);
        recyclerView = root.findViewById(R.id.message_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        contactMessages = new ArrayList<>();
        adapter = new ContactAdapter(contactMessages);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::setToChatActivity);
        setContactMessageList();
        return root;
    }

    private void setToChatActivity(int position) {
        Intent chatIntent = new Intent(requireContext(), Chat.class);
        chatIntent.putExtra(CONTACT_MESSAGE_EXTRA, contactMessages.get(position));
        startActivity(chatIntent);
    }

    private void setContactMessageList() {
        MessageHelper.getContactMessages(auth.getUid()).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "setContactMessageList: ", error);
                return;
            }
            if (value != null) {
                contactMessages.clear();
                contactMessages.addAll(value.toObjects(ContactMessage.class));
                adapter.notifyDataSetChanged();

                if (contactMessages.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    text_empty.setText(R.string.not_massages);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }

            }
        });
    }
}