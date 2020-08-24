package com.roncoder.bookstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class CmdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Commend> commends;
    private OnCartActionListener listener;

    public CmdAdapter(List<Commend> commends) {
        this.commends = commends;
    }

    /**
     * Interface to managed the listener items actions.
     */
    public interface OnCartActionListener {
        void onMessageListener(int position);
        void onRemoveListener (int position);
        void onBuyListener (int position);
        void onChangeQuantity (int position, int value);
        void onMoreInfoListener (int position, View view);
        void onValidateListener(int position, Button button);
    }

    public void setOnCartListener (OnCartActionListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_book, parent, false);

        return new CartHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Commend commend = commends.get(position);
        Book book = commend.getBook();
        if (holder instanceof CartHolder){
            CartHolder cartHolder = (CartHolder) holder;

            if (commend.isIs_validate())
                return;

            cartHolder.book_title.setText(book.getTitle());
            cartHolder.book_author.setText(book.getAuthor());
            cartHolder.book_edition.setText(book.getEditor());
            cartHolder.book_class.setText(book.getClasses());
            cartHolder.quantity.setText(String.valueOf(commend.getQuantity()));
            cartHolder.prise.setText(Utils.formatPrise(book.getUnit_prise()));
            cartHolder.total_prise.setText(Utils.formatPrise(commend.getTotal_prise()));
            if (commend.isIs_billed())
                cartHolder.cmd_billed_state.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return commends.size();
    }

    static class CartHolder extends RecyclerView.ViewHolder {
        Button btn_validate, btn_buy, btn_message;
        TextView more_info, book_title, book_author, book_edition, book_class, prise, total_prise, quantity;
        ImageView front_image;
        ImageButton remove_to_cart, add_quantity, remove_quantity, cmd_billed_state;
        public CartHolder(@NonNull View itemView, OnCartActionListener listener) {
            super(itemView);
            book_title = itemView.findViewById(R.id.book_title);
            book_author = itemView.findViewById(R.id.book_author);
            book_class = itemView.findViewById(R.id.book_prise);
            book_edition = itemView.findViewById(R.id.book_edition);
            prise = itemView.findViewById(R.id.prise);
            quantity = itemView.findViewById(R.id.quantity);
            total_prise = itemView.findViewById(R.id.total_prise);
            add_quantity = itemView.findViewById(R.id.add_quantity);
            remove_quantity = itemView.findViewById(R.id.remove_quantity);
            btn_validate = itemView.findViewById(R.id.btn_validate);
            btn_buy = itemView.findViewById(R.id.btn_buy);
            btn_message = itemView.findViewById(R.id.btn_message);
            more_info = itemView.findViewById(R.id.more_info);
            front_image = itemView.findViewById(R.id.front_image);
            remove_to_cart = itemView.findViewById(R.id.remove_to_cart);
            cmd_billed_state = itemView.findViewById(R.id.cmd_billed_state);

            setClickListener (listener);
        }

        private void setClickListener(OnCartActionListener listener) {
            add_quantity.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onChangeQuantity(position, 1);
            });
            remove_quantity.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onChangeQuantity(position, -1);
            });
            btn_buy.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onBuyListener(position);
            });
            btn_message.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onMessageListener(position);
            });
            btn_validate.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onValidateListener(position, btn_validate);
            });
            more_info.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onMoreInfoListener(position, front_image);
            });
            remove_to_cart.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onRemoveListener(position);
            });
        }
    }
}
