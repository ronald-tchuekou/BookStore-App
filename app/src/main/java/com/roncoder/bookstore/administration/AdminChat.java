package com.roncoder.bookstore.administration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.Chat;
import com.roncoder.bookstore.adapters.ContactAdapter;
import com.roncoder.bookstore.models.ContactMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.roncoder.bookstore.fragments.MessageChat.CONTACT_MESSAGE_EXTRA;

public class AdminChat extends AppCompatActivity {

    private List<ContactMessage> contactMessages;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);
        managedActionBar();
        RecyclerView recyclerView = findViewById(R.id.message_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        contactMessages = new ArrayList<>();
        adapter = new ContactAdapter(contactMessages);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::setToChatActivity);
        setCommendList();
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

    private void setCommendList () {
        // TODO implement this to get the list of contact message.
        contactMessages.add(new ContactMessage("1", "Ronald Tchuekou", "Commerce", "Comment est-ce vous procédez pour la livraison ?",
                Calendar.getInstance().getTime(), 5));
        contactMessages.add(new ContactMessage("2", "Benois Gilbert", "Commerce", "A-ton la possibilité de payer à la livraison ?",
                Calendar.getInstance().getTime(), 2));
        adapter.notifyDataSetChanged();
    }
}