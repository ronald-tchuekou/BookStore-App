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
import com.roncoder.bookstore.adapters.ContactAdapter;
import com.roncoder.bookstore.dbHelpers.MessageHelper;
import com.roncoder.bookstore.models.ContactMessage;

import java.util.ArrayList;
import java.util.List;

import static com.roncoder.bookstore.fragments.MessageChat.CONTACT_MESSAGE_EXTRA;

public class AdminChat extends AppCompatActivity {

    private static final String TAG = "AdminChat";
    private List<ContactMessage> contactMessages;
    private ContactAdapter adapter;
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
        contactMessages = new ArrayList<>();
        adapter = new ContactAdapter(contactMessages);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::setToChatActivity);
        setContactMessages();
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
        Intent chatIntent = new Intent(this, Chat.class);
        chatIntent.putExtra(CONTACT_MESSAGE_EXTRA, contactMessages.get(position));
        startActivity(chatIntent);
    }

    private void setContactMessages() {
        MessageHelper.getContactMessages(auth.getUid()).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "setContactMessageList: ", error);
                return;
            }
            if (value != null) {
                contactMessages.clear();
                contactMessages.addAll(value.toObjects(ContactMessage.class));
                adapter.notifyDataSetChanged();

                if (contactMessages.isEmpty()){
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