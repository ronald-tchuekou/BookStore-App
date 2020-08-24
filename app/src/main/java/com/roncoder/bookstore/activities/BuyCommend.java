package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.models.ShippingAddress;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.roncoder.bookstore.utils.Utils.EXTRA_COMMEND;

public class BuyCommend extends AppCompatActivity {
    private Button valid;
    private EditText contact_name, phone_number, district_view, street, more_desc;
    private RadioButton standard_shipping, instant_shipping, express_shipping;
    private AutoCompleteTextView instant_shipping_address;
    private SwitchMaterial as_default_address;
    private List<Commend> commends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_commend);

        initViews();

        Intent extraIntent = getIntent();
        if (extraIntent != null && extraIntent.hasExtra(EXTRA_COMMEND)){
            ArrayList<Parcelable> commends = extraIntent.getParcelableArrayListExtra(EXTRA_COMMEND);
            if (commends != null)
                for (Parcelable p: commends)
                    this.commends.add((Commend) p);
        }

        /* Check if this commends are billed. */
        if (commends.get(0).getBill_ref().equals("") || commends.get(0).getBill_ref() == null)
            contact_name.setText(Utils.getCurrentUser().getName());
        else
            getBillsByRef ();

        // init view.
        initListener();
        manageToolbar();
    }

    /**
     * Function that get the bill by ref.
     */
    private void getBillsByRef() {
        //TODO implement this to get the bill and send the views values.
        setViewValues();
    }

    /**
     * Function to set a values to the views.
     */
    private void setViewValues() {
        Bill bill = new Bill();
        ShippingAddress shippingAddress = bill.getShippingAddress();

        contact_name.setText(shippingAddress.getReceiver_name());
        phone_number.setText(shippingAddress.getPhone_number());
        street.setText(shippingAddress.getStreet());
        more_desc.setText(shippingAddress.getMore_description());
        String shipping_type = bill.getShipping_type();
        if (shipping_type.equals(Utils.SHIPPING_INSTANT))
            instant_shipping.setChecked(true);
        else if (shipping_type.equals(Utils.SHIPPING_STANDARD))
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
        district_view.setEnabled(false);

        contact_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().equals(""))
                    contact_name.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().equals(""))
                    phone_number.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // by default, we can disabled the autocompletion.
        street.setEnabled(false);
        instant_shipping.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                instant_shipping_address.setEnabled(true);
                street.setEnabled(false);
            }
            else {
                instant_shipping_address.setEnabled(false);
                street.setEnabled(true);

            }
        });

        valid.setOnClickListener(v -> {

            /* TODO Managed this to save the commend address.
            *   1. Save the shipping address : receiver_name; phone;  district; street; more_desc;user_id
            *   2. Save the bill : user_id; shipping_add_ref; shipping_type; total_prise
            *   3. Update commend : cmd_id; bill_ref
            */

            String receiver_name = contact_name.getText().toString().trim();
            String phone = phone_number.getText().toString().trim();
            String district = district_view.getText().toString().trim();
            String street = this.street.getText().toString().trim();
            String more_desc = this.more_desc.getText().toString().trim();
            String user_id = String.valueOf(Utils.getCurrentUser().getId());

            String shipping_type = getShippingType();
            if (receiver_name.equals(""))
                contact_name.setError(getString(R.string.set_the_receiver_of_this_cmd));
            if (phone.equals(""))
                phone_number.setError(getString(R.string.set_the_phone));



            Utils.setToastMessage(this, "Manage to set the commend");
        });
    }

    /**
     * Function that return the shipping type.
     * @return Shipping type.
     */
    private String getShippingType() {
        if (instant_shipping.isChecked())
            return Utils.SHIPPING_INSTANT;
        if (standard_shipping.isChecked())
            return Utils.SHIPPING_STANDARD;
        if (express_shipping.isChecked())
            return Utils.SHIPPING_EXPRESS;
        return "";
    }

    /**
     * Function to initialize the views.
     */
    private void initViews() {
        valid = findViewById(R.id.valid);
        contact_name = findViewById(R.id.contact_name);
        phone_number = findViewById(R.id.phone_number);
        district_view = findViewById(R.id.district);
        district_view.setText(R.string.yaounde);
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