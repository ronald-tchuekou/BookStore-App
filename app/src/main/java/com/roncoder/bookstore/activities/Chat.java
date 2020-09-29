package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.MessageAdapter;
import com.roncoder.bookstore.dbHelpers.MessageHelper;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.Message;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.roncoder.bookstore.fragments.MessageChat.CHAT_CON_ID_EXTRA;
import static com.roncoder.bookstore.fragments.MessageChat.CHAT_DESTINATION_EXTRA;

public class Chat extends AppCompatActivity {

    private static final String TAG = "Chat";
    private FirebaseAuth auth;
    private String receiver, conversation_id;
    private RecyclerView chat_recycler;
    private MessageAdapter adapter;
    private List<Message> messages;
    private FloatingActionButton send_btn;
    private EditText text_message;
    private View load_indicator;
    private ImageButton back_home;
    private ShapeableImageView profile_image;
    private TextView sender_name;
    private User user;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();

        receiver = getIntent().getStringExtra(CHAT_DESTINATION_EXTRA);
        conversation_id = getIntent().getStringExtra(CHAT_CON_ID_EXTRA);
        initViews();

        Log.i(TAG, "onCreate: (Receiver , Conversation id)  => (" + receiver + ", " + conversation_id + ")");

        adaptRecycler();
        setMessageList();
        managedListeners();

        // Recycler scrolling listener.
        chat_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Utils.setToastMessage(Chat.this, "POSITION : " + newState);
            }
        });

        back_home.setOnClickListener(v-> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserHelper.getUserById(receiver).addOnCompleteListener(com-> {
            if (!com.isSuccessful()) {
                if (com.getException() instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(this, R.string.network_not_allowed);
                else
                    Utils.setDialogMessage(this, R.string.error_has_provide);
                Log.e(TAG, "managedMessage: ", com.getException());
                return;
            }
            DocumentSnapshot value = com.getResult();
            if (value != null) {
                user = value.toObject(User.class);
                // Set info.
                if (user == null) { finish(); return;}
                String username = user.getSurname() + " " + user.getName();
                sender_name.setText(username);
                Glide.with(this)
                        .load(user.getProfile())
                        .placeholder(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_account, null))
                        .placeholder(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_account, null))
                        .into(profile_image);
            }
        });
    }

    private void initViews() {
        chat_recycler = findViewById(R.id.chat_recycler);
        text_message = findViewById(R.id.text_message);
        send_btn = findViewById(R.id.btn_send);
        load_indicator = findViewById(R.id.load_indicator);
        back_home = findViewById(R.id.back_home);
        profile_image = findViewById(R.id.profile_image);
        sender_name = findViewById(R.id.user_name);
    }

    private void managedListeners() {
        send_btn.setEnabled(false);
        send_btn.setClickable(false);
        // Text changer.
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
        //  Send the message.
        send_btn.setOnClickListener(v -> {
            Message message = new Message(conversation_id, auth.getUid(), this.message, Utils.now());
            MessageHelper.sendMessage(message).addOnCompleteListener(com-> {
                if (!com.isSuccessful()) {
                    if (com.getException() instanceof FirebaseNetworkException)
                        Utils.setDialogMessage(this, R.string.network_not_allowed);
                    else
                        Utils.setToastMessage(this, getString(R.string.error_has_provide));
                    Log.e(TAG, "managedMessage: ", com.getException());
                    return;
                }
                if (com.getResult() != null) {
                    String id = com.getResult().getId();
                    // Update message id.
                    MessageHelper.getCollectionRef().document(id).update("id", id).addOnCompleteListener(com1-> {
                        if (!com1.isSuccessful()) {
                            Log.e(TAG, "managedMessage: ", com1.getException());
                            return;
                        }
                        message.setId(id);
                        // UPDATE LAST MESSAGE OF THIS CONVERSATION.
                        MessageHelper.getConCollectionRef().document(conversation_id).update("last_message", message)
                                .addOnCompleteListener(com12 -> {
                                    if (!com1.isSuccessful()) {
                                        Log.e(TAG, "managedMessage: ", com1.getException());
                                        return;
                                    }
                                    Log.i(TAG, "managedMessage: Message is sent.");
                                });
                    });
                }
            });
            messages.add(message);
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

    private void setMessageList() {
        if (conversation_id == null)
            return;

        load_indicator.setVisibility(View.VISIBLE);
        MessageHelper.getMessages(conversation_id).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "setMessageList: ", error);
                load_indicator.setVisibility(View.GONE);
                return;
            }
            if (value != null) {
                messages.clear();
                messages.addAll(value.toObjects(Message.class));
                adapter.notifyDataSetChanged();
                chat_recycler.scrollToPosition(messages.size()-1);
                load_indicator.setVisibility(View.GONE);
                Log.i(TAG, "setMessageList: "+ messages);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}