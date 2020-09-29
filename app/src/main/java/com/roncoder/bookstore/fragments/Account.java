package com.roncoder.bookstore.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.Administration;
import com.roncoder.bookstore.activities.ContactUs;
import com.roncoder.bookstore.activities.Help;
import com.roncoder.bookstore.activities.LoginWithSocial;
import com.roncoder.bookstore.activities.MyInformation;
import com.roncoder.bookstore.activities.Settings;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

import java.util.Objects;

public class Account extends Fragment implements View.OnClickListener {
    private static final String TAG = "Account";
    @SuppressLint("StaticFieldLeak")
    private static Account instance = null;
    Button button_account_sign;
    TextView disconnect;
    private FirebaseUser firebaseUser;
    String uId = FirebaseAuth.getInstance().getUid() == null ? "not_user" : FirebaseAuth.getInstance().getUid();

    public Account() { }

    public static Account getInstance() {
        if (instance == null)
            instance = new Account();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        // Check if current user is admin.
        UserHelper.getUserById(uId) .addOnCompleteListener(com -> {
            if (!com.isSuccessful()) {
                if (com.getException() instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                Log.e(TAG, "on Create View : ", com.getException());
                return;
            }
            User user = Objects.requireNonNull(com.getResult()).toObject(User.class);
            if (user != null && user.isIs_admin())
                root.findViewById(R.id.administration).setVisibility(View.VISIBLE);
        });

        button_account_sign = root.findViewById(R.id.button_account_sign);
        button_account_sign.setOnClickListener(this);
        root.findViewById(R.id.my_information).setOnClickListener(this);
        root.findViewById(R.id.administration).setOnClickListener(this);
        root.findViewById(R.id.settings).setOnClickListener(this);
        root.findViewById(R.id.help).setOnClickListener(this);
        root.findViewById(R.id.note_app).setOnClickListener(this);
        root.findViewById(R.id.contact_us).setOnClickListener(this);
        disconnect = root.findViewById(R.id.disconnect);
        disconnect.setOnClickListener(this);
        connectionState();
        return root;
    }

    public void connectionState () {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean is_connected = firebaseUser != null;
        if (is_connected) {
            button_account_sign.setEnabled(false);
            disconnect.setEnabled(true);
        }else{
            button_account_sign.setEnabled(true);
            disconnect.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        connectionState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_account_sign:
                startActivity(new Intent(requireContext(), LoginWithSocial.class));
                break;
            case R.id.my_information:
                startActivity(new Intent(requireContext(), MyInformation.class));
                break;
            case R.id.administration:
                startActivity(new Intent(requireActivity(), Administration.class));
                break;
            case R.id.settings:
                startActivity(new Intent(requireContext(), Settings.class));
                break;
            case R.id.help:
                Intent intent = new Intent(requireContext(), Help.class);
                intent.putExtra(Help.EXTRA_TYPE, Help.HELP);
                startActivity(intent);
                break;
            case R.id.note_app:
                Utils.setToastMessage(requireActivity(), "Faire ceci apres avoir mis l'application sur PlayStore !!");
                // TODO set to the note of app.
                break;
            case R.id.contact_us:
                startActivity(new Intent(requireContext(), ContactUs.class));
                break;
            default:
                new MaterialAlertDialogBuilder(requireActivity(),
                        R.style.Theme_MaterialComponents_Light_BottomSheetDialog)
                        .setTitle(R.string.comfim)
                        .setMessage(R.string.confir_disconnect)
                        .setPositiveButton(R.string.yes, (dialog1, which) -> {
                            disconnectUser();
                            disconnect.setEnabled(false);
                            button_account_sign.setEnabled(true);
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                break;
        }
    }

    /**
     * Function to disconnect the current user.
     */
    private void disconnectUser() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       if (firebaseUser != null)
            FirebaseAuth.getInstance().signOut();
    }
}
