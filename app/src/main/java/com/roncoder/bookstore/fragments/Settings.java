package com.roncoder.bookstore.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.utils.Utils;

public class Settings extends PreferenceFragmentCompat {

    private static final CharSequence KEY = "allow_notification";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        SwitchPreference notificationsPref = findPreference(KEY);
        if (notificationsPref != null) {
            notificationsPref.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        Utils.setToastMessage(requireContext(), newValue.toString());
                        return true;
                    });
        }
    }
}
