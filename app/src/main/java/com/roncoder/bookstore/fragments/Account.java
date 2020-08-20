package com.roncoder.bookstore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.Administration;
import com.roncoder.bookstore.activities.ContactUs;
import com.roncoder.bookstore.activities.Help;
import com.roncoder.bookstore.activities.LoginWithSocial;
import com.roncoder.bookstore.activities.MyInformation;
import com.roncoder.bookstore.activities.Settings;

public class Account extends Fragment implements View.OnClickListener {
    public Account() { }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        root.findViewById(R.id.button_account_sign).setOnClickListener(this);
        root.findViewById(R.id.my_information).setOnClickListener(this);
        root.findViewById(R.id.administration).setOnClickListener(this);
        root.findViewById(R.id.settings).setOnClickListener(this);
        root.findViewById(R.id.help).setOnClickListener(this);
        root.findViewById(R.id.note_app).setOnClickListener(this);
        root.findViewById(R.id.contact_us).setOnClickListener(this);
        root.findViewById(R.id.disconnect).setOnClickListener(this);
        return root;
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
                startActivity(new Intent(requireContext(), Help.class));
                break;
            case R.id.note_app:
                Toast.makeText(requireContext(), "Faire ceci apres avoir mis l'application sur PlayStore !!",
                        Toast.LENGTH_SHORT).show();
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
                            // TODO disconnected the user.
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                break;
        }
    }
}
