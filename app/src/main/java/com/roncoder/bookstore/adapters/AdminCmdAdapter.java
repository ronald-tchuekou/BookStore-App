package com.roncoder.bookstore.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.Facture;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class AdminCmdAdapter extends BaseAdapter {

    private List<Facture> factures;
    private OnItemActionsListener listener;

    public interface OnItemActionsListener {
        void onClickListener (int position);
        void onValidListener (int position, View view);
    }

    public AdminCmdAdapter (List<Facture> factures) {
        this.factures = factures;
    }
    @Override
    public int getCount() {
        return factures.size();
    }

    @Override
    public Object getItem(int position) {
        return factures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Facture facture = factures.get(position);
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_cmd,
                    parent, false);
        }

        TextView client_name = convertView.findViewById(R.id.client_name);
        TextView client_phone = convertView.findViewById(R.id.client_phone);
        TextView client_location = convertView.findViewById(R.id.client_location);
        TextView date_cmd = convertView.findViewById(R.id.date_cmd);
        TextView book_number = convertView.findViewById(R.id.book_number);
        ImageButton action_btn = convertView.findViewById(R.id.action_btn);

        if (facture.getState().equals(Utils.FactureState.SHIPPING))
        action_btn.setImageDrawable(parent.getContext().getResources().getDrawable(R.drawable.ic_valid));
        else if (facture.getState().equals(Utils.FactureState.OBSOLETED))
            action_btn.setImageDrawable(parent.getContext().getResources().getDrawable(R.drawable.ic_obsolete));

        action_btn.setOnClickListener(v -> listener.onValidListener(position, v));
        convertView.setOnClickListener(v -> listener.onClickListener(position));

        // TODO set the view values.

        return convertView;
    }

    public void setOnItemActionListener (OnItemActionsListener listener) {
        this.listener = listener;
    }
}
