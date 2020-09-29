package com.roncoder.bookstore.administration;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.BillAdapter;
import com.roncoder.bookstore.dbHelpers.BillHelper;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.roncoder.bookstore.utils.Utils.EXTRA_COMMEND;

public class FragBillShipping extends Fragment {
    public static final String TAG = "FragBillShipping";
    List<Bill> bills;
    private BillAdapter adapter;
    private View root;
    public FragBillShipping() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_frag_cmd_shipping, container, false);

        ListView listView = root.findViewById(R.id.list_cmd);
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
            public void onValidListener(int position, View view) { }
        });
        return root;
    }
    /**
     * Function to set the list of the commends.
     */
    private void setCommendList() {
        BillHelper.getBillByShippingType(Utils.BILL_DELIVER)
                .addSnapshotListener((value, error) -> {
                    if (error != null)  {
                        Utils.exceptions(requireActivity(), error);
                        Log.e(TAG, "Error when get the delivered commend list : ", error);
                        return;
                    }

                    if (value != null) {
                        bills = value.toObjects(Bill.class);
                        if (bills.isEmpty())
                            root.findViewById(R.id.empty).setVisibility(View.VISIBLE);
                        else
                            root.findViewById(R.id.empty).setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}