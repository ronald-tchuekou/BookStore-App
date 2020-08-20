package com.roncoder.bookstore.administration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.drm.DrmStore;
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
import com.roncoder.bookstore.models.Commend;

import java.util.ArrayList;
import java.util.List;

public class AdminFactureDetail extends AppCompatActivity {

    private ListView listView;
    CommendAdapter adapter;
    List<Commend> listCommends;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cmd_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_cmd_detail);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        listView = findViewById(R.id.list_cmd);
        setCommendList();
        adapter = new CommendAdapter(listCommends);
        setListHeader();
        listView.setAdapter(adapter);
    }

    private void setCommendList() {
        listCommends = new ArrayList<>();
        listCommends.add(new Commend());
        listCommends.add(new Commend());
        listCommends.add(new Commend());
    }

    /**
     * Function to managed the header and footer of the list.
     */
    private void setListHeader() {
        View listHeader = LayoutInflater.from(this).inflate(R.layout.header_admin_cmd, listView, false),
                listFooter = LayoutInflater.from(this).inflate(R.layout.copyright, listView, false);
        TextView client_name = listHeader.findViewById(R.id.client_name);
        TextView client_phone = listHeader.findViewById(R.id.client_phone);
        TextView client_location = listHeader.findViewById(R.id.client_location);
        TextView address_des = listHeader.findViewById(R.id.address_des);
        TextView total_prise = listHeader.findViewById(R.id.total_prise);

        listView.addHeaderView(listHeader);
        listView.addFooterView(listFooter);

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
                    book_count = convertView.findViewById(R.id.book_count),
                    total_prise = convertView.findViewById(R.id.total_prise);

            Glide.with(parent.getContext())
                    .load(commend.getBook().getImage1_front())
                    .placeholder(parent.getContext().getResources().getDrawable(R.drawable.excellence_en_svteehb_3e))
                    .into(book_image);

            // TODO

            return convertView;
        }
    }
}