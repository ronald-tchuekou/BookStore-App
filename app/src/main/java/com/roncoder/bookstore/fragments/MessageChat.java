package com.roncoder.bookstore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.Chat;
import com.roncoder.bookstore.adapters.ContactAdapter;
import com.roncoder.bookstore.models.ContactMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageChat extends Fragment {
    public static final String CONTACT_MESSAGE_EXTRA = "contact_message";
    private List<ContactMessage> contactMessages;
    private ContactAdapter adapter;

    public MessageChat() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_message, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.message_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        contactMessages = new ArrayList<>();
        adapter = new ContactAdapter(contactMessages);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::setToChatActivity);
        setCommendList();
        return root;
    }

    private void setToChatActivity(int position) {
        Intent chatIntent = new Intent(requireContext(), Chat.class);
        chatIntent.putExtra(CONTACT_MESSAGE_EXTRA, contactMessages.get(position));
        startActivity(chatIntent);
    }

    private void setCommendList () {
        // TODO implement this to get the list of contact message.
        contactMessages.add(new ContactMessage("1", "Ronald Tchuekou", "Commerce", "Comment est-ce vous procédez pour la livraison ?",
                Calendar.getInstance().getTime(), 5));
        contactMessages.add(new ContactMessage("2", "Benois Gilbert", "Commerce", "A-ton la possibilité de payer à la livraison ?",
                Calendar.getInstance().getTime(), 2));
        contactMessages.add(new ContactMessage("3", "Loren Mafouo Goldine", "Commerce", "Quel sont les modalités de livraison ?",
                Calendar.getInstance().getTime(), 0));
        adapter.notifyDataSetChanged();
    }
}