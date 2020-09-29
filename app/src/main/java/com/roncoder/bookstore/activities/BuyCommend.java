package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.BillHelper;
import com.roncoder.bookstore.dbHelpers.ShippingAddressHelper;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.models.ShippingAddress;
import com.roncoder.bookstore.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.roncoder.bookstore.administration.FragBill.EXTRA_BILL;

public class BuyCommend extends AppCompatActivity {
    private static final String TAG = "BuyCommend";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Button valid;
    private EditText contact_name, phone_number, district_view, street, more_desc;
    private RadioButton standard_shipping, instant_shipping, express_shipping;
    private AutoCompleteTextView instant_shipping_address;
    private SwitchMaterial as_default_address;
    private Bill bill;
    private boolean is_registered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_commend);

        // Verify if this use are auth.
        if (user == null) {
           Utils.setDialogMessage(this, null);
        }
        // init views.
        initViews();
        // get bill in extra intent.
        Intent extraIntent = getIntent();
        if (extraIntent != null && extraIntent.hasExtra(EXTRA_BILL))
            bill = extraIntent.getParcelableExtra(EXTRA_BILL);
        Log.w(TAG, "Bill : " + bill);
        // Check if this bill is registered or not.
        is_registered = bill != null && !bill.getRef().equals("");
        // init view listeners.
        initListener();
        // managed the action bar.
        manageToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!is_registered) {
            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setReceiver_name(user.getDisplayName());
            shippingAddress.setPhone_number(user.getPhoneNumber() == null || user.getPhoneNumber().equals("") ? user.getPhoneNumber() : "");
            setViewValues(shippingAddress);
        }else {
            Utils.setProgressDialog(this, getString(R.string.loading));
            ShippingAddressHelper.getShippingAddressByRef(bill.getShipping_ref())
                    .addOnCompleteListener(com -> {
                        Utils.dismissDialog();
                        if (!com.isSuccessful()) {
                            Exception e = com.getException();
                            if (e instanceof FirebaseNetworkException)
                                Utils.setDialogMessage(this, R.string.network_not_allowed);
                            Log.e(TAG, "On resume =>", e);
                            return;
                        }
                        ShippingAddress shippingAddress = Objects.requireNonNull(com.getResult()).toObject(ShippingAddress.class);
                        assert shippingAddress != null;
                        setViewValues(shippingAddress);
                    });
        }
    }

    /**
     * Function to set a values to the views.
     */
    private void setViewValues(ShippingAddress shippingAddress) {
        contact_name.setText(shippingAddress.getReceiver_name());
        phone_number.setText(shippingAddress.getPhone_number());
        street.setText(shippingAddress.getStreet());
        more_desc.setText(shippingAddress.getMore_description());
        String shipping_type = bill.getShipping_type();
        if (shipping_type.equals(Utils.SHIPPING_INSTANT)) {
            instant_shipping.setChecked(true);
            instant_shipping_address.setText(shippingAddress.getStreet());
            instant_shipping_address.setVisibility(View.VISIBLE);
            street.setVisibility(View.GONE);
            findViewById(R.id.street_label).setVisibility(View.GONE);
        }
        else {
            instant_shipping_address.setEnabled(false);
            instant_shipping_address.setVisibility(View.GONE);
            street.setVisibility(View.VISIBLE);
            findViewById(R.id.street_label).setVisibility(View.VISIBLE);

            street.setEnabled(true);
            if (shipping_type.equals(Utils.SHIPPING_STANDARD))
                standard_shipping.setChecked(true);
            else
                express_shipping.setChecked(true);
        }

        // Set if this address are default or not.
        as_default_address.setChecked(shippingAddress.isIs_default());
    }

    /**
     * Function to initialize the listeners on the views.
     */
    private void initListener() {
        // by default, we can disabled the district.
        district_view.setEnabled(false);
        // by default, we can disabled the autocompletion.
        street.setEnabled(false);
        instant_shipping.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                instant_shipping_address.setEnabled(true);
                street.setEnabled(false);
                instant_shipping_address.setVisibility(View.VISIBLE);
                street.setVisibility(View.GONE);
                findViewById(R.id.street_label).setVisibility(View.GONE);
            }
            else {
                instant_shipping_address.setEnabled(false);
                street.setEnabled(true);
                instant_shipping_address.setVisibility(View.GONE);
                street.setVisibility(View.VISIBLE);
                findViewById(R.id.street_label).setVisibility(View.VISIBLE);

            }
        });

        valid.setOnClickListener(v -> submit());
    }

    private void submit() {
        boolean is_validate = true;
        String receiver_name = contact_name.getText().toString().trim();
        String phone = phone_number.getText().toString().trim();
        String district = district_view.getText().toString().trim();
        String street;
        if (instant_shipping.isChecked())
            street = instant_shipping_address.getText().toString();
        else
            street = this.street.getText().toString().trim();
        String more_desc = this.more_desc.getText().toString().trim();
        String user_id = user.getUid();

        // Validations
        if (receiver_name.equals("")) {
            contact_name.setError(getString(R.string.error_this_field_can_be_empty));
            is_validate = false;
        }
        if (phone.equals("")){
            phone_number.setError(getString(R.string.error_this_field_can_be_empty));
            is_validate = false;
        }
        if (street.equals("")){
            if (instant_shipping.isChecked())
                instant_shipping_address.setError(getString(R.string.error_this_field_can_be_empty));
            else
                this.street.setError(getString(R.string.error_this_field_can_be_empty));
            is_validate = false;
        }
        if (more_desc.equals("")){
            this.more_desc.setError(getString(R.string.error_this_field_can_be_empty));
            is_validate = false;
        }
        ShippingAddress shippingAd = new ShippingAddress("", user_id, receiver_name, phone, district,
                street, more_desc, true);
        if (is_validate)
            if (is_registered)
                updateShippingAddress(shippingAd);
            else
                saveShippingAddress (shippingAd);
    }

    private void updateShippingAddress(ShippingAddress shippingAd) {
        Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
        ShippingAddressHelper.update(shippingAd)
                .addOnCompleteListener(com -> {
                    Utils.dismissDialog();
                    if (!com.isSuccessful()) {
                        if (com.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        Log.e(TAG, "add shipping address error : ", com.getException());
                        return;
                    }
                    Utils.setDialogMessage(this, getString(R.string.update_successful));
                    onBackPressed();
                });
    }

    private void saveShippingAddress(ShippingAddress shippingAddress) {
        Utils.setProgressDialog(this, getString(R.string.saver_data));
        ShippingAddressHelper.saveShippingAddress(shippingAddress)
                .addOnCompleteListener(com -> {
                    if (com.isSuccessful()) {
                        String ref = Objects.requireNonNull(com.getResult()).getId();
                        ShippingAddressHelper.getCollectionRef().document(ref).update("ref", ref)
                                .addOnCompleteListener(complete -> {
                                   if (complete.isSuccessful()){
                                       saveBill(ref);
                                   } else {
                                       Utils.dismissDialog();
                                       if (com.getException() instanceof FirebaseNetworkException)
                                           Utils.setDialogMessage(this, R.string.network_not_allowed);
                                       Log.e(TAG, "add shipping address error : ", com.getException());
                                   }
                                });
                    } else {
                        Utils.dismissDialog();
                        if (com.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        Log.e(TAG, "add shipping address error : ", com.getException());
                    }
                });
    }

    private void saveBill(String shipping_ref) {
        if (user != null) {

            bill.setUser_id(user.getUid());
            bill.setShipping_ref(shipping_ref);
            bill.setShipping_type(getShippingType());
            bill.setShipping_date(getShippingDate());
            bill.setPayment_type(Utils.PAYMENT_AT_SHIPPING);
            bill.setShipping_cost(getShippingCost());

            Log.i(TAG, "saveBill: " + bill);

            BillHelper.saveBill(bill).addOnCompleteListener(com -> { // Add bill to the database.
                if (com.isSuccessful()) {
                    String ref = Objects.requireNonNull(com.getResult()).getId();
                    List<Task<Void>> taskList = BillHelper.billedCommend(bill.getCommend_ids(), ref);
                    taskList.get(taskList.size()-1).addOnCompleteListener(command -> { // Billed commend of this bill.
                        if (command.isSuccessful()) {
                            BillHelper.getCollectionRef().document(ref).update("ref", ref)
                                    .addOnCompleteListener(d-> { // Update ref of this bill.
                                        Utils.dismissDialog();
                                        if (!d.isSuccessful()){
                                            if (com.getException() instanceof FirebaseNetworkException)
                                                Utils.setDialogMessage(this, R.string.network_not_allowed);
                                            Log.e(TAG, "updated bill error : ", command.getException());
                                            return;
                                        }
                                        Utils.setDialogMessage(this, getString(R.string.add_successful));
                                        onBackPressed();
                                    });
                        } else {
                            Utils.dismissDialog();
                            if (com.getException() instanceof FirebaseNetworkException)
                                Utils.setDialogMessage(this, R.string.network_not_allowed);
                            Utils.setToastMessage(this, getString(R.string.not_save));
                            Log.e(TAG, "updated bill error : ", command.getException());
                        }
                    });
                } else {
                    Utils.dismissDialog();
                    if (com.getException() instanceof FirebaseNetworkException)
                        Utils.setDialogMessage(this, R.string.network_not_allowed);
                    Utils.setToastMessage(this, getString(R.string.not_save));
                    Log.e(TAG, "add bill error : ", com.getException());
                }
            });
        }
        else {
            Log.w(TAG, "saveBill: The user in null");
            Utils.setToastMessage(this, getString(R.string.not_save));
        }
    }

    /**
     * Function that return the shipping date of this bill.
     * @return Shipping date.
     */
    private Date getShippingDate() {
        Calendar  calendar = Calendar.getInstance();
        if (instant_shipping.isChecked())
            calendar.add(Calendar.HOUR_OF_DAY, 24);
        if (standard_shipping.isChecked())
            calendar.add(Calendar.DAY_OF_WEEK, 2);
        if (express_shipping.isChecked())
            calendar.add(Calendar.HOUR_OF_DAY, 3);
        return calendar.getTime();
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
     * Function that return the shipping cost.
     * @return Shipping cost.
     */
    private float getShippingCost() {
        if (instant_shipping.isChecked())
            return Utils.SHIPPING_COST_INSTANT;
        if (standard_shipping.isChecked())
            return Utils.SHIPPING_COST_STANDARD;
        if (express_shipping.isChecked())
            return Utils.SHIPPING_COST_EXPRESS;
        return 0;
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