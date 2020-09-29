package com.roncoder.bookstore.administration;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.ClassHelper;
import com.roncoder.bookstore.models.Classes;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;
import java.util.Objects;

public class AddClass extends AppCompatActivity {

    private static final String TAG = "AddClass";
    EditText edit_name, edit_libel;
    AutoCompleteTextView edit_cycle;
    Button submit;

    @Override
    protected void onCreate (Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_add_class);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        initViews();

        submit.setOnClickListener(v -> submitForm());
    }

    private void submitForm() {

        boolean name_ok = true, cycle_ok = true;

        String name = Objects.requireNonNull(edit_name.getText()).toString().trim();
        String libel = Objects.requireNonNull(edit_libel.getText()).toString().trim();
        String cycle = Objects.requireNonNull(edit_cycle.getText()).toString().trim();

        Classes classes = new Classes("", name, libel, cycle);

        // Check validation of title
        if (name.equals("")) {
            edit_name.setError(getString(R.string.error_this_field_can_be_empty));
            name_ok = false;
        } else
            edit_name.setError(null);

        // Check validation of title
        if (cycle.equals("")) {
            edit_cycle.setError(getString(R.string.error_this_field_can_be_empty));
            cycle_ok = false;
        } else
            edit_cycle.setError(null);

        if (name_ok && cycle_ok)
            addClass(classes);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        finish();
        return true;
    }

    private void addClass(Classes classes) {
        Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
        ClassHelper.getClassByName (classes.getName()).addOnCompleteListener(com1 -> { // Check existance.
            if (!com1.isSuccessful()) {
                Utils.dismissDialog();
                if (com1.getException() instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(this, R.string.network_not_allowed);
                else
                    Log.e(TAG, "Error: ", com1.getException());
                return;
            }
            if (!is_uniqueTitle(classes.getName(), Objects.requireNonNull(com1.getResult()).toObjects(Classes.class))) {
                ClassHelper.addClass(classes).addOnCompleteListener(com -> { // Addition.
                    if (!com.isSuccessful()) {
                        Utils.dismissDialog();
                        if (com.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        else
                            Log.e(TAG, "Error: ", com.getException());
                        return;
                    }
                    String id = Objects.requireNonNull(com.getResult()).getId();
                    ClassHelper.getCollectionRef().document(id).update("id", id)
                            .addOnSuccessListener(s -> {
                                Utils.dismissDialog();
                                Utils.setToastMessage(this, getString(R.string.add_successful));
                            })
                            .addOnFailureListener(f -> {
                                Utils.dismissDialog();
                                if (f instanceof FirebaseNetworkException)
                                    Utils.setDialogMessage(this, R.string.network_not_allowed);
                                else
                                    Log.e(TAG, "Error: ", f);
                            });
                });
            } else{
                Utils.dismissDialog();
                Utils.setToastMessage(this, getString(R.string.class_allready_exist));
            }
        });
    }

    private boolean is_uniqueTitle(String name, List<Classes> classes) {
        for (Classes c : classes)
            if (c.getName().contains(name))
                return true;
        return false;
    }

    private void initViews() {

        edit_name = findViewById(R.id.edit_name);
        edit_libel = findViewById(R.id.edit_libel);
        edit_cycle = findViewById(R.id.edit_cycle);
        submit = findViewById(R.id.submit_btn);

        ArrayAdapter<CharSequence> adapter_cycle = new ArrayAdapter<>(this,
                R.layout.cat_exposed_dropdown_popup_item, getResources().getStringArray(R.array.cycles));
        edit_cycle.setAdapter(adapter_cycle);
    }
}
