package com.roncoder.bookstore.administration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.Chat;
import com.roncoder.bookstore.adapters.ConversationAdapter;
import com.roncoder.bookstore.dbHelpers.MessageHelper;
import com.roncoder.bookstore.models.Conversation;

import java.util.ArrayList;
import java.util.List;

import static com.roncoder.bookstore.fragments.MessageChat.CHAT_CON_ID_EXTRA;
import static com.roncoder.bookstore.fragments.MessageChat.CHAT_DESTINATION_EXTRA;

public class AdminChat extends AppCompatActivity {

    private static final String TAG = "AdminChat";
    private List<Conversation> conversations;
    private ConversationAdapter adapter;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private View empty;
    private TextView text_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_admin_chat);
        managedActionBar();
        empty = findViewById(R.id.empty);
        text_empty = findViewById(R.id.text_empty);
        recyclerView = findViewById(R.id.message_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        conversations = new ArrayList<>();
        adapter = new ConversationAdapter(conversations);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::setToChatActivity);
        setConversationList();
    }

    private void managedActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(getString(R.string.message_about_cmd));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setToChatActivity(int position) {
        Conversation conversation = conversations.get(position);
        List<String> members = conversation.getMembers();
        String receiver = members.get(0).equals(auth.getUid()) ? members.get(1) : members.get(0);
        Intent chatIntent = new Intent(this, Chat.class);
        chatIntent.putExtra(CHAT_DESTINATION_EXTRA, receiver);
        chatIntent.putExtra(CHAT_CON_ID_EXTRA, conversation.getId());
        startActivity(chatIntent);
    }

    private void setConversationList() {
        MessageHelper.getConversations(auth.getUid()).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "setContactMessageList: ", error);
                return;
            }
            if (value != null) {
                conversations.clear();
                conversations.addAll(value.toObjects(Conversation.class));
                if (conversations.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    text_empty.setText(R.string.not_massages);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}