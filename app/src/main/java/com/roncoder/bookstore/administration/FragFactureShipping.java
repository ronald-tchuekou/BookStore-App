package com.roncoder.bookstore.administration;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.AdminCmdAdapter;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.models.Facture;
import com.roncoder.bookstore.models.ShippingAddress;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.roncoder.bookstore.utils.Utils.EXTRA_COMMEND;

public class FragFactureShipping extends Fragment {
    List<Facture> factures;
    private ListView listView;
    private AdminCmdAdapter adapter;
    private View root;
    public FragFactureShipping() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_frag_cmd_shipping, container, false);

        listView = root.findViewById(R.id.list_cmd);
        View footer = LayoutInflater.from(requireContext()).inflate(R.layout.list_footer, listView, false);
        listView.addFooterView(footer);
        setCommendList();
        adapter = new AdminCmdAdapter(factures);
        listView.setAdapter(adapter);
        adapter.setOnItemActionListener(new AdminCmdAdapter.OnItemActionsListener() {
            @Override
            public void onClickListener(int position) {
                Intent detailIntent = new Intent(requireActivity(), AdminFactureDetail.class);
                detailIntent.putExtra(EXTRA_COMMEND, factures.get(position));
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
        factures = new ArrayList<>();
        List<Commend> commends = new ArrayList<>();
        commends.add(new Commend(1, new Book(), 4, Calendar.getInstance().getTime()));
        commends.add(new Commend(1, new Book(), 2, Calendar.getInstance().getTime()));
        factures.add(new Facture(1, commends, new User(), new ShippingAddress(), Calendar.getInstance().getTime(), Utils.FactureState.SHIPPING));
        factures.add(new Facture(1, commends, new User(), new ShippingAddress(), Calendar.getInstance().getTime(), Utils.FactureState.SHIPPING));
        factures.add(new Facture(1, commends, new User(), new ShippingAddress(), Calendar.getInstance().getTime(), Utils.FactureState.SHIPPING));
        factures.add(new Facture(1, commends, new User(), new ShippingAddress(), Calendar.getInstance().getTime(), Utils.FactureState.SHIPPING));
        factures.add(new Facture(1, commends, new User(), new ShippingAddress(), Calendar.getInstance().getTime(), Utils.FactureState.SHIPPING));
        factures.add(new Facture(1, commends, new User(), new ShippingAddress(), Calendar.getInstance().getTime(), Utils.FactureState.SHIPPING));
        factures.add(new Facture(1, commends, new User(), new ShippingAddress(), Calendar.getInstance().getTime(), Utils.FactureState.SHIPPING));
        factures.add(new Facture(1, commends, new User(), new ShippingAddress(), Calendar.getInstance().getTime(), Utils.FactureState.SHIPPING));
    }
}