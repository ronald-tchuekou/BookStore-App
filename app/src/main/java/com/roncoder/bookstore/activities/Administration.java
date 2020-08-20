package com.roncoder.bookstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.PagerAdapter;
import com.roncoder.bookstore.administration.AdminChat;

public class Administration extends AppCompatActivity {
    private  TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView message_badge;
    int commend_count = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);
        viewPager = findViewById(R.id.view_pager);
        setTab();
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        setActionBar();
    }

    /**
     * Function to set the actionbar.
     */
    private void setActionBar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Function to set the tab.
     */
    private void setTab() {
        tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.item_tab_customize));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.item_tab_customize));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.item_tab_customize));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.item_tab_customize));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.item_tab_customize));
        setTabTitle(0, R.string.cmd_cmd);
        setTabTitle(1, R.string.cmd_standard);
        setTabTitle(2, R.string.cmd_express);
        setTabTitle(3, R.string.cmd_shipping);
        setTabTitle(4, R.string.cmd_obselet);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setTabBadge(0, 10);
        setTabBadge(1, 11);
        setTabBadge(2, 12);
        setTabBadge(3, 10);
        setTabBadge(4, 0);
    }

    /**
     * Function to set the tab title.
     * @param position Tab position.
     * @param label Label.
     */
    private void setTabTitle(int position, int label) {

        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null){
            View root = tab.getCustomView();
            assert root != null;
            TextView title = root.findViewById(R.id.title);
            title.setText(label);

        }
    }

    /**
     * Function set the value of the badge.
     * @param position tab position.
     * @param number Number.
     */
    private void setTabBadge (int position, int number) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null){
            View root = tab.getCustomView();
            assert root != null;
            TextView badge = root.findViewById(R.id.badge);
            if (number <= 0)
                badge.setVisibility(View.GONE);
            else{
                badge.setVisibility(View.VISIBLE);
                badge.setText(String.valueOf(number));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_option, menu);

        final MenuItem menuItem = menu.findItem(R.id.message_option);

        View actionView = menuItem.getActionView();
        message_badge = actionView.findViewById(R.id.message_badge);

        setupBadge();

        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, AdminChat.class));
        return true;
    }

    /**
     * Function set the value off the badge.
     */
    private void setupBadge() {
        if (message_badge != null) {
            if (commend_count == 0) {
                if (message_badge.getVisibility() != View.GONE) {
                    message_badge.setVisibility(View.GONE);
                }
            } else {
                message_badge.setText(commend_count >= 100 ? 99 + "+" : commend_count + "");
                if (message_badge.getVisibility() != View.VISIBLE) {
                    message_badge.setVisibility(View.VISIBLE);
                }
            }
        }
    }


}