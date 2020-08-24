package com.roncoder.bookstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.models.ShippingAddress;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class BillAdapter extends BaseAdapter {

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
    public Object getItem(int position) {
        return bills.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bill bill = bills.get(position);
        ShippingAddress shippingAddress = bill.getShippingAddress();

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_bill,
                    parent, false);
        }

        TextView client_name = convertView.findViewById(R.id.client_name);
        TextView client_phone = convertView.findViewById(R.id.client_phone);
        TextView client_location = convertView.findViewById(R.id.client_location);
        TextView date_cmd = convertView.findViewById(R.id.date_cmd);
        TextView book_number = convertView.findViewById(R.id.book_number);
        ImageButton action_btn = convertView.findViewById(R.id.action_state_btn);

        client_name.setText(shippingAddress.getReceiver_name());
        client_phone.setText(shippingAddress.getPhone_number());
        client_location.setText(shippingAddress.getStreet());
        date_cmd.setText(Utils.formatDate(bill.getShipping_date()));
        String total_books = getBookTotal(bill) + " " + parent.getContext().getString(R.string.books_in_total);
        book_number.setText(total_books);

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
        convertView.setOnClickListener(v -> listener.onClickListener(position));

        return convertView;
    }

    public void setOnItemActionListener (OnItemActionsListener listener) {
        this.listener = listener;
    }

    private int getBookTotal (Bill bill) {
        int t = 0;
        for (Commend c : bill.getCommends())
            t += c.getTotal_prise();
        return t;
    }
}
