package com.roncoder.bookstore.administration;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.BillAdapter;
import com.roncoder.bookstore.models.Bill;

import java.util.ArrayList;
import java.util.List;

import static com.roncoder.bookstore.utils.Utils.EXTRA_COMMEND;

public class FragBillExpress extends Fragment {

    List<Bill> bills;
    private ListView listView;
    private BillAdapter adapter;
    private View root;

    public FragBillExpress() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_frag_cmd_express, container, false);

        listView = root.findViewById(R.id.list_cmd);
        View footer = LayoutInflater.from(requireContext()).inflate(R.layout.list_footer, listView, false);
        listView.addFooterView(footer);
        bills = new ArrayList<>();
        setCommendList();
        adapter = new BillAdapter(bills);
        listView.setAdapter(adapter);
        adapter.setOnItemActionListener(new BillAdapter.OnItemActionsListener() {
            @Override
            public void onClickListener(int position) {
                Intent detailIntent = new Intent(requireActivity(), AdminBillDetail.class);
                detailIntent.putExtra(EXTRA_COMMEND, bills.get(position));
                startActivity(detailIntent);
            }

            @Override
            public void onValidListener(int position, View view) {
                // TODO Validate the commend.
                ImageButton button = (ImageButton) view;
                button.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_valid, null));
//                adapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    /**
     * Function to set the list of the commends.
     */
    private void setCommendList() {
        bills.add(new Bill());
        bills.add(new Bill());
        bills.add(new Bill());
        bills.add(new Bill());
        bills.add(new Bill());
        bills.add(new Bill());
        bills.add(new Bill());
        bills.add(new Bill());
    }

}