package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.MessageAdapter;
import com.roncoder.bookstore.fragments.MessageChat;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.models.ContactMessage;
import com.roncoder.bookstore.models.Message;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.roncoder.bookstore.utils.Utils.EXTRA_COMMEND;

public class Chat extends AppCompatActivity {

    private static final String TAG = "Chat";
    private ArrayList<Parcelable> parcelablesCommends;
    private ContactMessage contactMessage;
    private RecyclerView chat_recycler;
    private MessageAdapter adapter;
    private List<Message> messages;
    private FloatingActionButton send_btn;
    private EditText text_message;
    private View load_indicator;

    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        contactMessage = getIntent().getParcelableExtra(MessageChat.CONTACT_MESSAGE_EXTRA);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        chat_recycler = findViewById(R.id.chat_recycler);
        text_message = findViewById(R.id.text_message);
        send_btn = findViewById(R.id.btn_send);
        load_indicator = findViewById(R.id.load_indicator);
        parcelablesCommends = getIntent().getParcelableArrayListExtra(EXTRA_COMMEND);
        if (parcelablesCommends != null) setCommends();
        adaptRecycler();
        setSupportActionBar(toolbar);
        managedActionBar();
        setMessageList();
        managedMessage();
    }

    /**
     * Function to get all the commend set by the intent.
     */
    private void setCommends () {
        for(Parcelable parcel : parcelablesCommends) {
            Commend commend = (Commend) parcel;
            Log.i(TAG, "setCommends: Commends : " + commend);
        }
    }

    private void managedMessage() {
        send_btn.setEnabled(false);
        send_btn.setClickable(false);
        text_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                message = s.toString().trim();
                if (message.equals("")){
                    send_btn.setEnabled(false);
                    send_btn.setClickable(false);
                }else {
                    send_btn.setEnabled(true);
                    send_btn.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        send_btn.setOnClickListener(v -> {
            // TODO implement the send of message.
            messages.add(new Message(1, "send", Utils.now(), message));
            chat_recycler.scrollToPosition(messages.size()-1);
            adapter.notifyItemInserted(messages.size()-1);
            text_message.getText().clear();
            send_btn.setClickable(false);
            send_btn.setEnabled(false);
        });
    }

    private void adaptRecycler() {
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);
        chat_recycler.setLayoutManager(new LinearLayoutManager(this));
        chat_recycler.setHasFixedSize(true);
        chat_recycler.scrollToPosition(messages.size() - 1);
        chat_recycler.setAdapter(adapter);
    }

    private void managedActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(contactMessage.getSender());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void setMessageList() {
        load_indicator.setVisibility(View.VISIBLE);
        messages.add(new Message(2, "received", Utils.now(), getString(R.string.loerm)));
        messages.add(new Message(1, "send", Utils.now(), getString(R.string.loerm)));
        messages.add(new Message(1, "send", Utils.now(), getString(R.string.loerm)));
        messages.add(new Message(2, "received", Utils.now(), getString(R.string.loerm)));
        messages.add(new Message(1, "send", Utils.now(), getString(R.string.loerm)));
        messages.add(new Message(2, "received", Utils.now(), getString(R.string.loerm)));
        messages.add(new Message(2, "received", Utils.now(), getString(R.string.loerm)));
        messages.add(new Message(2, "received", Utils.now(), getString(R.string.loerm)));
        adapter.notifyDataSetChanged();
        chat_recycler.scrollToPosition(messages.size() - contactMessage.getNot_read_count());
        load_indicator.setVisibility(View.GONE);
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
}