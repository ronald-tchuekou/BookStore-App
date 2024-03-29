package com.roncoder.bookstore.administration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.roncoder.bookstore.dbHelpers.BillHelper;
import com.roncoder.bookstore.dbHelpers.BookHelper;
import com.roncoder.bookstore.dbHelpers.CommendHelper;
import com.roncoder.bookstore.dbHelpers.ShippingAddressHelper;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.models.ShippingAddress;
import com.roncoder.bookstore.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.roncoder.bookstore.administration.FragBill.EXTRA_BILL;

public class AdminBillDetail extends AppCompatActivity {

    private static final String TAG = "AdminBillDetail";
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
        bill = extraIntent.getParcelableExtra(EXTRA_BILL);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_cmd_detail);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        listView = findViewById(R.id.list_cmd);
        listCommends = getFlashCommendList();
        adapter = new CommendAdapter(listCommends);
        setListHeader();
        listView.setAdapter(adapter);
    }

    private List<Commend> getFlashCommendList() {
        List<Commend> result = new ArrayList<>();
        for (String cmd_id : bill.getCommend_ids())
        {
            Commend cmd = new Commend();
            cmd.setId(cmd_id);
            result.add(cmd);
        }
        return result;
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

        ShippingAddressHelper.getShippingAddressByRef(bill.getShipping_ref()).addOnCompleteListener(com -> {
           if (com.isSuccessful()){
               ShippingAddress shippingAddress = Objects.requireNonNull(com.getResult()).toObject(ShippingAddress.class);
               assert shippingAddress != null;
               client_location.setText(shippingAddress.getStreet());
               client_name.setText(shippingAddress.getReceiver_name());
               client_phone.setText(shippingAddress.getPhone_number());
               address_des.setText(shippingAddress.getMore_description());
           }
        });

        total_prise.setText(Utils.formatPrise(bill.getTotal_prise()));

        listView.addHeaderView(listHeader);
        listView.addFooterView(listFooter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cmd_state_option, menu);
        menuItem = menu.getItem(0);

        if (bill.getState().equals(Utils.BILL_DELIVER))
            menuItem.setIcon(R.drawable.ic_valid);
        if (bill.getState().equals(Utils.BILL_IN_COURSE))
            menuItem.setIcon(R.drawable.ic_not_valid);
        if (bill.getState().equals(Utils.BILL_OBSOLETE))
            menuItem.setIcon(R.drawable.ic_obsolete);
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
        BillHelper.getCollectionRef().document(bill.getRef())
                .update("state", Utils.BILL_DELIVER).addOnCompleteListener(command -> {
                   if (!command.isSuccessful()) {
                       Log.e(TAG, "changCommendState: ", command.getException());
                       return;
                   }
                    menuItem.setIcon(R.drawable.ic_valid);
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private static class CommendAdapter extends BaseAdapter {

        private static final String TAG = "Admin";
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
            TextView book_title = convertView.findViewById(R.id.book_title);
            TextView book_edition = convertView.findViewById(R.id.book_edition);
            TextView book_count = convertView.findViewById(R.id.book_count);
            TextView total_prise = convertView.findViewById(R.id.total_prise);
            TextView date_cmd = convertView.findViewById(R.id.date_cmd);
            ImageView cmd_state = convertView.findViewById(R.id.cmd_state);

            if (commend.getBook_id().equals(""))
                // GET THE COMMEND INFO
                CommendHelper.getCollectionRef().document(commend.getId()).get().addOnCompleteListener(command -> {
                    if (!command.isSuccessful()) {
                        Log.e(TAG, "getView: Commend information => ", command.getException());
                        return;
                    }
                    Log.i(TAG, "getView: Commend => " + Objects.requireNonNull(command.getResult()).toObject(Commend.class));
                    if (command.getResult() != null) {
                        Commend cmd = command.getResult().toObject(Commend.class);
                        assert cmd != null;
                        book_count.setText(String.valueOf(cmd.getQuantity()));
                        total_prise.setText(Utils.formatPrise(cmd.getTotal_prise()));
                        date_cmd.setText(Utils.formatDate(cmd.getDate_cmd()));
                        if (cmd.isIs_validate())
                            cmd_state.setVisibility(View.VISIBLE);
                        // GET THE BOOK INFO
                        BookHelper.getCollectionRef().document(cmd.getBook_id()).get().addOnCompleteListener(com -> {
                            if (!com.isSuccessful()) {
                                Log.e(TAG, "getView: Commend information => ", com.getException());
                                return;
                            }

                            Log.i(TAG, "getView: Book => " + Objects.requireNonNull(com.getResult()).toObject(Book.class));
                            Book book = Objects.requireNonNull(com.getResult()).toObject(Book.class);
                            assert book != null;
                            book_title.setText(book.getTitle() + " + " + book.getAuthor());
                            book_edition.setText(book.getEditor());
                            Glide.with(parent.getContext())
                                    .load(book.getImage1_front())
                                    .placeholder(ResourcesCompat.getDrawable(parent.getContext().getResources(),
                                            R.drawable.bg_image, null))
                                    .into(book_image);
                        });
                    }
                });
            return convertView;
        }
    }
}