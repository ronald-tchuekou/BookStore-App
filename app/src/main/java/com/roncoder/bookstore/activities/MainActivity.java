package com.roncoder.bookstore.activities;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.transition.MaterialFadeThrough;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.NavAdapter;
import com.roncoder.bookstore.fragments.Account;
import com.roncoder.bookstore.fragments.Cart;
import com.roncoder.bookstore.fragments.Home;
import com.roncoder.bookstore.fragments.MessageChat;
import com.roncoder.bookstore.fragments.Search;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final SparseArray<Fragment> LAYOUT_RES_MAP = new SparseArray<>();
    public static final int REQUEST_CODE_CYCLE_CLASS = 0;
    private DrawerLayout drawerLayout;
    private NavAdapter adapter;
    private ExpandableListView listView;
    private List<String> groups;
    private Map<String, List<String>> child;
    @IdRes int currentItem;

    static {
        LAYOUT_RES_MAP.append(R.id.action_home, new Home());
        LAYOUT_RES_MAP.append(R.id.action_search, Search.getInstance());
        LAYOUT_RES_MAP.append(R.id.action_kilt, new Cart());
        LAYOUT_RES_MAP.append(R.id.action_message, new MessageChat());
        LAYOUT_RES_MAP.append(R.id.action_account, new Account());
    }
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLists();
        adapter = new NavAdapter(groups, child);
        listView = findViewById(R.id.nav_list);
        initExpandableView();

        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottomnavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            replaceFragment(item.getItemId());
            return true;
        });
        addCartBadge(R.id.action_kilt); // Add the cart badge.
        addCartBadge(R.id.action_message); // Add the message badge.
        replaceFragment(R.id.action_home);
        Search searchFragment = (Search) LAYOUT_RES_MAP.get(R.id.action_search);
        searchFragment.setOnSearchFocus(hasFocus -> bottomNavigationView.setVisibility(hasFocus ? View.GONE : View.VISIBLE));
    }

    /**
     * Function to init the expandable view.
     */
    private void initExpandableView() {
        View listHeader = LayoutInflater.from(this).inflate(R.layout.drawer_header, drawerLayout, false);
        listView.addHeaderView(listHeader);
        listView.setAdapter(adapter);
        listView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String classe = Objects.requireNonNull(child.get(groups.get(groupPosition))).get(childPosition);
            Toast.makeText(MainActivity.this, classe, Toast.LENGTH_SHORT).show();

            // TODO implement this method to set the correspond calss books.
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setLists() {
        groups = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.class_groups)));

        child = new TreeMap<>();
        child.put(groups.get(0), Arrays.asList(getResources().getStringArray(R.array.nursery_anglophone)));
        child.put(groups.get(1),  Arrays.asList(getResources().getStringArray(R.array.maternelle_francophone)));
        child.put(groups.get(2),  Arrays.asList(getResources().getStringArray(R.array.primary_anglophone)));
        child.put(groups.get(3),  Arrays.asList(getResources().getStringArray(R.array.primaire_francophone)));
        child.put(groups.get(4),  Arrays.asList(getResources().getStringArray(R.array.secondary_anglophone)));
        child.put(groups.get(5),  Arrays.asList(getResources().getStringArray(R.array.secondaire_francophone)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CYCLE_CLASS && resultCode == RESULT_OK) {
            if (data == null) return;
            setFragment(data);
        }
    }

    private void setFragment(Intent data) {
        if (data.hasExtra(Utils.EXTRA_FRAG_TYPE)){
            int fragType = data.getIntExtra(Utils.EXTRA_FRAG_TYPE, 0);
            if (fragType == Utils.FRAG_HOME){
                replaceFragment(R.id.action_home);
                bottomNavigationView.setSelectedItemId(R.id.action_home);
            }
            else if (fragType == Utils.FRAG_SEARCH){
                replaceFragment(R.id.action_search);
                bottomNavigationView.setSelectedItemId(R.id.action_search);
            }
            else if (fragType == Utils.FRAG_KILT){
                replaceFragment(R.id.action_kilt);
                bottomNavigationView.setSelectedItemId(R.id.action_kilt);
            }
            else if (fragType == Utils.FRAG_MESSAGE){
                replaceFragment(R.id.action_message);
                bottomNavigationView.setSelectedItemId(R.id.action_message);
            }
            else if (fragType == Utils.FRAG_ACCOUNT){
                replaceFragment(R.id.action_account);
                bottomNavigationView.setSelectedItemId(R.id.action_account);
            }
        }
    }

    private static Fragment getLayoutForItemId(@IdRes int itemId) {
        return LAYOUT_RES_MAP.get(itemId);
    }
    @SuppressLint("NewApi")
    private void replaceFragment(@IdRes int itemId) {
        currentItem = itemId;
        Fragment fragment = getLayoutForItemId(itemId);
        fragment.setEnterTransition(createTransition());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    private MaterialFadeThrough createTransition(){
        MaterialFadeThrough fadeThrough = new MaterialFadeThrough();
        fadeThrough.addTarget(R.id.home_fragment);
        fadeThrough.addTarget(R.id.search_fragment);
        fadeThrough.addTarget(R.id.kilt_fragment);
        fadeThrough.addTarget(R.id.message_fragment);
        fadeThrough.addTarget(R.id.account_fragment);
        Log.i("MainAcitivity", "createTransition: " + fadeThrough);
        return fadeThrough;
    }

    /**
     * Function to add badge to bottom navigation view.
     * @param optionId Res Id.
     */
    private void addCartBadge (@IdRes int optionId) {
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(optionId);
        badge.setVisible(true);
    }
    /**
     * Function to add badge to bottom navigation view.
     * @param optionId Res Id.
     */
    private void updateNumberCartBadge (@IdRes int optionId, int value) {
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(optionId);
        if (value == 0) {
            bottomNavigationView.removeBadge(optionId);
            return;
        }
        badge.setNumber(value);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Search.getInstance().setOnDrawerClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        updateNumberCartBadge(R.id.action_message, 3);
        updateNumberCartBadge(R.id.action_kilt, 2);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (currentItem != R.id.action_home){
            replaceFragment(R.id.action_home);
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        } else
            finish();
    }
}
