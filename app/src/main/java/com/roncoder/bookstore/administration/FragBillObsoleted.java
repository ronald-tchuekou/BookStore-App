package com.roncoder.bookstore.administration;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.BillAdapter;
import com.roncoder.bookstore.dbHelpers.BillHelper;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.utils.Utils;
import java.util.ArrayList;
import java.util.List;

import static com.roncoder.bookstore.administration.FragBill.EXTRA_BILL;
import static com.roncoder.bookstore.utils.Utils.EXTRA_COMMEND;

public class FragBillObsoleted extends Fragment {
    public static final String TAG = "FragBillObsoleted";
    List<Bill> bills;
    private BillAdapter adapter;
    private View root, progress;
    public FragBillObsoleted() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_frag_cmd_obseleted, container, false);
        progress = root.findViewById(R.id.progress);
        ListView listView = root.findViewById(R.id.list_cmd);
        View footer = LayoutInflater.from(requireContext()).inflate(R.layout.list_footer, listView, false);
        listView.addFooterView(footer);
        bills = new ArrayList<>();
        adapter = new BillAdapter(bills);
        listView.setAdapter(adapter);
        adapter.setOnItemActionListener(new BillAdapter.OnItemActionsListener() {
            @Override
            public void onClickListener(int position) {
                Intent detailIntent = new Intent(requireActivity(), AdminBillDetail.class);
                detailIntent.putExtra(EXTRA_BILL, bills.get(position));
                startActivity(detailIntent);
            }

            @Override
            public void onValidListener(int position, View view) {
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setCommendList();
    }

    /**
     * Function to set the list of the commends.
     */
    private void setCommendList() {
        progress.setVisibility(View.VISIBLE);
        BillHelper.getBillByState(Utils.BILL_OBSOLETE)
                .addSnapshotListener((value, error) -> {
                    progress.setVisibility(View.GONE);
                    if (error != null)  {
                        Log.e(TAG, "Error when get the Obsoleted commend list : ", error);
                        return;
                    }

                    if (value != null) {
                        bills.clear();
                        bills.addAll(value.toObjects(Bill.class));
                        Log.w(TAG, "setCommendList => " + bills);
                        if (bills.isEmpty()) {
                            root.findViewById(R.id.empty).setVisibility(View.VISIBLE);
                            ((TextView)root.findViewById(R.id.text_empty)).setText(R.string.empty_bill);
                        }
                        else
                            root.findViewById(R.id.empty).setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}