package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.databases.CommendHelper;
import com.roncoder.bookstore.models.ShippingAddress;

    public class BuyCommend extends AppCompatActivity {
    private Button valid;
    private ShippingAddress shippingAddress = new ShippingAddress();
    private EditText contact_name, phone_number, district, street, more_desc;
    private RadioButton standard_shipping, instant_shipping, express_shipping;
    private AutoCompleteTextView instant_shipping_address;
    private SwitchMaterial as_default_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_commend);
        // init view.
        initViews();
        setViewValues();
        initListener();
        manageToolbar();
    }

    /**
     * Function to set a values to the views.
     */
    private void setViewValues() {
        // TODO checked if the information is set or not.
        contact_name.setText(shippingAddress.getReceiver_name());
        phone_number.setText(shippingAddress.getPhone_number());
        district.setText(R.string.yaounde);
        street.setText(shippingAddress.getStreet());
        more_desc.setText(shippingAddress.getMore_description());
        String shipping_type = shippingAddress.getShipping_type();
        if (shipping_type.equals(CommendHelper.INSTANT_SHIPPING_TYPE))
            instant_shipping.setChecked(true);
        else if (shipping_type.equals(CommendHelper.STANDARD_SHIPPING_TYPE))
            standard_shipping.setChecked(true);
        else
            express_shipping.setChecked(true);

        // Set if this address are default or not.
        as_default_address.setChecked(shippingAddress.isIs_default());
    }

    /**
     * Function to initialize the listeners on the views.
     */
    private void initListener() {
        // by default, we can disabled the district.
        district.setEnabled(false);
        district.setFocusable(false);

        // by default, we can disabled the autocompletion.
        street.setEnabled(false);
        street.setFocusable(false);
        instant_shipping.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                instant_shipping_address.setFocusable(true);
                instant_shipping_address.setEnabled(true);

                street.setEnabled(false);
                street.setFocusable(false);
            }
            else {
                instant_shipping_address.setFocusable(false);
                instant_shipping_address.setEnabled(false);

                street.setEnabled(true);
                street.setFocusable(true);
            }
        });

        valid.setOnClickListener(v -> {
            // TODO Managed this to save the commend address.
            Toast.makeText(this, "Manage to set the commend.",
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Function to initialize the views.
     */
    private void initViews() {
        valid = findViewById(R.id.valid);
        contact_name = findViewById(R.id.contact_name);
        phone_number = findViewById(R.id.phone_number);
        district = findViewById(R.id.district);
        street = findViewById(R.id.street);
        more_desc = findViewById(R.id.more_desc);
        standard_shipping = findViewById(R.id.standard_shipping);
        instant_shipping = findViewById(R.id.instant_shipping);
        express_shipping = findViewById(R.id.express_shipping);
        as_default_address = findViewById(R.id.as_default_address);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                R.layout.cat_exposed_dropdown_popup_item,
                getResources().getStringArray(R.array.instant_shipping_locations));

        instant_shipping_address = findViewById(R.id.filled_exposed_dropdown);
        instant_shipping_address.setAdapter(adapter);
    }

    /**
     * Function to managed the toolbar.
     */
    private void manageToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.waranty_or_shipping);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
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
}