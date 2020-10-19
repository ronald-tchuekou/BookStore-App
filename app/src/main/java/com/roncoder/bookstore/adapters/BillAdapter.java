package com.roncoder.bookstore.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.ShippingAddressHelper;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.models.ShippingAddress;
import com.roncoder.bookstore.util.Utils;

import java.util.List;
import java.util.Objects;

public class BillAdapter extends BaseAdapter {

    private static final String TAG = "BillAdapter";
    private List<Bill> bills;
    private OnItemActionsListener listener;

    public interface OnItemActionsListener {
        void onClickListener (int position);
        void onValidListener (int position, View view);
    }

    public BillAdapter(List<Bill> bills) {
        this.bills = bills;
    }
    @Override
    public int getCount() {
        return bills.size();
    }

    @Override
    public Bill getItem(int position) {
        return bills.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bill bill = bills.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_bill,
                    parent, false);
        }
        Log.w(TAG, "getView: " + bill);
        TextView client_name = convertView.findViewById(R.id.client_name);
        TextView client_phone = convertView.findViewById(R.id.client_phone);
        TextView client_location = convertView.findViewById(R.id.client_location);
        TextView date_cmd = convertView.findViewById(R.id.date_cmd);
        ImageButton action_btn = convertView.findViewById(R.id.action_state_btn);

        // Get the correspond shipping address.
        ShippingAddressHelper.getShippingAddressByRef(bill.getShipping_ref()).addOnCompleteListener(com -> {
            if (!com.isSuccessful()) {
                Exception e = com.getException();
                Log.e(TAG, "getView: ", e);
                return;
            }

            ShippingAddress shippingAddress = Objects.requireNonNull(com.getResult()).toObject(ShippingAddress.class);
            assert shippingAddress != null;
            client_name.setText(shippingAddress.getReceiver_name());
            client_phone.setText(shippingAddress.getPhone_number());
            client_location.setText(shippingAddress.getStreet());
            Log.w(TAG, "getView: Shipping address " + shippingAddress);
        });

        client_name.setText("");
        client_phone.setText("");
        client_location.setText("");
        date_cmd.setText(Utils.formatDate(bill.getShipping_date()));

        switch (bill.getState()) {
            case Utils.BILL_DELIVER:
                action_btn.setImageDrawable(ResourcesCompat.getDrawable(parent.getContext().getResources(),
                        R.drawable.ic_valid, null));
                break;
            case Utils.BILL_OBSOLETE:
                action_btn.setImageDrawable(ResourcesCompat.getDrawable(parent.getContext().getResources(),
                        R.drawable.ic_obsolete, null));
                break;
        }

        action_btn.setOnClickListener(v -> listener.onValidListener(position, v));
        convertView.findViewById(R.id.content_layout).setOnClickListener(v -> listener.onClickListener(position));

        return convertView;
    }

    public void setOnItemActionListener (OnItemActionsListener listener) {
        this.listener = listener;
    }
}
