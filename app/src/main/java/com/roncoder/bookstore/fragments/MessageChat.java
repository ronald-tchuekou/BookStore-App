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
import com.roncoder.bookstore.adapters.ConversationAdapter;
import com.roncoder.bookstore.dbHelpers.MessageHelper;
import com.roncoder.bookstore.models.Conversation;

import java.util.ArrayList;
import java.util.List;

public class MessageChat extends Fragment {

    public static final String TAG = "MessageChat";
    public static final String CHAT_DESTINATION_EXTRA = "receiver";
    public static final String CHAT_CON_ID_EXTRA = "conversation_id";
    private static MessageChat instance = null;
    private List<Conversation> conversations;
    private ConversationAdapter adapter;
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
        conversations = new ArrayList<>();
        adapter = new ConversationAdapter(conversations);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::setToChatActivity);
        setConversationList();
        return root;
    }

    private void setToChatActivity(int position) {
        Conversation conversation = conversations.get(position);
        List<String> members = conversation.getMembers();
        String receiver = members.get(0).equals(auth.getUid()) ? members.get(1) : members.get(0);
        Intent chatIntent = new Intent(requireContext(), Chat.class);
        chatIntent.putExtra(CHAT_DESTINATION_EXTRA, receiver);
        chatIntent.putExtra(CHAT_CON_ID_EXTRA, conversation.getId());
        startActivity(chatIntent);
    }

    private void setConversationList() {
        MessageHelper.getConversations(auth.getUid() == null ? "not_user" : auth.getUid()).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "setContactMessageList: ", error);
                return;
            }
            if (value != null) {
                conversations.clear();
                conversations.addAll(value.toObjects(Conversation.class));
                adapter.notifyDataSetChanged();

                if (conversations.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    text_empty.setText(R.string.not_massages);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }

                Log.i(TAG, "setConversationList: " + conversations);

            }
        });
    }
}