package com.roncoder.bookstore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.Chat;
import com.roncoder.bookstore.adapters.CartAdapter;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.models.ContactMessage;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.roncoder.bookstore.fragments.MessageChat.CONTACT_MESSAGE_EXTRA;
import static com.roncoder.bookstore.utils.Utils.EXTRA_COMMEND;

public class Cart extends Fragment {
    private View root;
    private List<Commend> commends;
    private CartAdapter adapter;

    public Cart() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cart, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_cart);
        root.findViewById(R.id.btn_buy_all).setOnClickListener(v -> Utils.buyCommend(requireActivity(), commends));
        root.findViewById(R.id.btn_chat_all).setOnClickListener(v -> setToMessage(commends));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        commends = new ArrayList<>();
        adapter = new CartAdapter(commends);
        recyclerView.setAdapter(adapter);
        setCommendList();
        adapter.setOnCartListener(new CartAdapter.OnCartActionListener() {
            @Override
            public void onMessageListener(int position) {
                List<Commend> commendList = new ArrayList<>();
                commendList.add(commends.get(position));
                setToMessage(commendList);
            }

            @Override
            public void onRemoveListener(int position) {
                new MaterialAlertDialogBuilder(requireActivity(),
                        R.style.Theme_MaterialComponents_Light_BottomSheetDialog)
                        .setTitle(R.string.comfim)
                        .setMessage(R.string.confir_removed_to_cart)
                        .setPositiveButton(R.string.yes, (dialog1, which) -> {
                            commends.remove(position);
                            adapter.notifyItemRemoved(position);
                            Toast.makeText(requireActivity(), getString(R.string.remove_success),
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }

            @Override
            public void onBuyListener(int position) {
                List<Commend> buyCommends = new ArrayList<>();
                buyCommends.add(commends.get(position));
                Utils.buyCommend(requireActivity(), buyCommends);
            }

            @Override
            public void onMoreInfoListener(int position, View view) {
                Utils.bookDetail(requireActivity(), commends.get(position).getBook(), view, Home.TRANSITION_IMAGE_NAME);
            }
        });
        updateTotalCommendPrise();
        return root;
    }
    private void updateTotalCommendPrise () {
        double total = 0;
        for (Commend c : commends) {
            total += (c.getBook().getUnit_prise() * c.getQuantity());
        }

        TextView total_cmd_prise = root.findViewById(R.id.total_cmd_prise);
        total_cmd_prise.setText(Utils.formatPrise(total));
    }
    private void setToMessage(List<Commend> commends) {
        // TODO
        Intent chatIntent = new Intent(requireActivity(), Chat.class);
        chatIntent.putExtra(CONTACT_MESSAGE_EXTRA, new ContactMessage("id", "Ronald Tchuekou (Commande)",
                "Boutique", "Commande commande commande commande commande", Calendar.getInstance().getTime(),
                0));
        chatIntent.putExtra(EXTRA_COMMEND, (ArrayList<? extends Parcelable>) commends);
        /* TO GET THIS ELEMENT, we make this method.
           ArrayList<Parcelable> commend = getIntent().getParcelableArrayListExtra(EXTRA_COMMEND);
         */
        startActivity(chatIntent);
    }

    private void setCommendList () {
        Commend commend = new Commend(0, new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10), 3, Calendar.getInstance().getTime());
        commends.add(commend);
        commends.add(commend);
        adapter.notifyDataSetChanged();
    }
}
