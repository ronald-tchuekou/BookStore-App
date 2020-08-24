package com.roncoder.bookstore.administration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.models.ShippingAddress;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

import static com.roncoder.bookstore.administration.FragBill.EXTRA_BILL;

public class AdminBillDetail extends AppCompatActivity {

    private ListView listView;
    CommendAdapter adapter;
    List<Commend> listCommends;
    private MenuItem menuItem;
    Bill bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cmd_detail);

        Intent extraIntent = getIntent();
        if (extraIntent != null && extraIntent.hasExtra(EXTRA_BILL)){
            bill = extraIntent.getParcelableExtra(EXTRA_BILL);
        }else
            bill = new Bill();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_cmd_detail);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        listView = findViewById(R.id.list_cmd);
        listCommends = bill.getCommends();
        adapter = new CommendAdapter(listCommends);
        setListHeader();
        listView.setAdapter(adapter);
    }

    /**
     * Function to managed the header and footer of the list.
     */
    private void setListHeader() {
        View listHeader = LayoutInflater.from(this).inflate(R.layout.header_admin_bill, listView, false),
                listFooter = LayoutInflater.from(this).inflate(R.layout.copyright, listView, false);
        TextView client_name = listHeader.findViewById(R.id.client_name);
        TextView client_phone = listHeader.findViewById(R.id.client_phone);
        TextView client_location = listHeader.findViewById(R.id.client_location);
        TextView address_des = listHeader.findViewById(R.id.address_des);
        TextView total_prise = listHeader.findViewById(R.id.total_prise);

        ShippingAddress shippingAddress = bill.getShippingAddress();

        client_location.setText(shippingAddress.getStreet());
        client_name.setText(shippingAddress.getReceiver_name());
        client_phone.setText(shippingAddress.getPhone_number());
        address_des.setText(shippingAddress.getMore_description());
        total_prise.setText(Utils.formatPrise(getTotalPayment(listCommends)));

        listView.addHeaderView(listHeader);
        listView.addFooterView(listFooter);

    }

    /**
     * Function that return the total prise of this bill.
     * @param commends List of commends.
     * @return Result.
     */
    private float getTotalPayment(List<Commend> commends) {
        float tp = 0;
        for (Commend c : commends)
            tp += c.getTotal_prise();
        return tp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cmd_state_option, menu);
        menuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cmd_state_option) {
            changCommendState();
        }else
            onBackPressed();
        return true;
    }

    private void changCommendState() {
        menuItem.setIcon(R.drawable.ic_valid);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private static class CommendAdapter extends BaseAdapter {

        private List<Commend> commends;

        CommendAdapter (List<Commend> commends) {
            this.commends = commends;
        }

        @Override
        public int getCount() {
            return commends.size();
        }

        @Override
        public Object getItem(int position) {
            return commends.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Commend commend = commends.get(position);
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .item_admin_cmd_list, parent, false);

            ImageView book_image = convertView.findViewById(R.id.book_image);
            TextView book_title = convertView.findViewById(R.id.book_title),
                    book_edition = convertView.findViewById(R.id.book_edition),
                    book_count = convertView.findViewById(R.id.book_prise),
                    total_prise = convertView.findViewById(R.id.total_prise);

            Book book = commend.getBook();
            book_title.setText(book.getTitle());
            book_edition.setText(book.getEditor());
            book_count.setText(commend.getQuantity());
            total_prise.setText(String.valueOf(commend.getTotal_prise()));

            Glide.with(parent.getContext())
                    .load(commend.getBook().getImage1_front())
                    .placeholder(ResourcesCompat.getDrawable(parent.getContext().getResources(),
                            R.drawable.excellence_en_svteehb_3e, null))
                    .into(book_image);
            return convertView;
        }
    }
}