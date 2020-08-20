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

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Commend> commends;
    private OnCartActionListener listener;

    public CartAdapter(List<Commend> commends) {
        this.commends = commends;
    }

    /**
     * Interface to managed the listener items actions.
     */
    public interface OnCartActionListener {
        void onMessageListener(int position);
        void onRemoveListener (int position);
        void onBuyListener (int position);
        void onMoreInfoListener (int position, View view);
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
        // TODO implement this to get the class.
        Commend commend = commends.get(position);
        Book book = commend.getBook();
        if (holder instanceof CartHolder){
            CartHolder cartHolder = (CartHolder) holder;
            cartHolder.book_title.setText(book.getTitle());
            cartHolder.book_author.setText(book.getAuthor());
            cartHolder.book_edition.setText(book.getEditor());
            cartHolder.book_class.setText("Classe");
            cartHolder.quantity.setText(String.valueOf(commend.getQuantity()));
            cartHolder.prise.setText(Utils.formatPrise(book.getUnit_prise()));
            cartHolder.total_prise.setText(Utils.formatPrise(book.getUnit_prise() * commend.getQuantity()));
        }
    }

    @Override
    public int getItemCount() {
        return commends.size();
    }

    static class CartHolder extends RecyclerView.ViewHolder {
        Button btn_remove, btn_buy, btn_message;
        TextView more_info, book_title, book_author, book_edition, book_class, prise, total_prise, quantity;
        ImageView front_image;
        ImageButton remove_to_cart, add_quantity, remove_quanity;
        public CartHolder(@NonNull View itemView, OnCartActionListener listener) {
            super(itemView);
            book_title = itemView.findViewById(R.id.book_title);
            book_author = itemView.findViewById(R.id.book_author);
            book_class = itemView.findViewById(R.id.book_count);
            book_edition = itemView.findViewById(R.id.book_edition);
            prise = itemView.findViewById(R.id.prise);
            quantity = itemView.findViewById(R.id.quantity);
            total_prise = itemView.findViewById(R.id.total_prise);
            add_quantity = itemView.findViewById(R.id.add_quantity);
            remove_quanity = itemView.findViewById(R.id.remove_quantity);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            btn_buy = itemView.findViewById(R.id.btn_buy);
            btn_message = itemView.findViewById(R.id.btn_message);
            more_info = itemView.findViewById(R.id.more_info);
            front_image = itemView.findViewById(R.id.front_image);
            remove_to_cart = itemView.findViewById(R.id.remove_to_cart);

            setClickListener (listener);
        }

        private void setClickListener(OnCartActionListener listener) {
            add_quantity.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // TODO
                }
            });
            remove_quanity.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    //TODO
                }
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
            btn_remove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onRemoveListener(position);
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
