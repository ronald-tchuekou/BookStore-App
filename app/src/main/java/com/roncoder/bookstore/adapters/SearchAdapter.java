package com.roncoder.bookstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.fragments.Search;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    public static final int EMPTY_ELEMENT = 0;
    public static final int ITEM_ELEMENT = 1;
    private OnItemClickListener listener;
    private List<Book> books;
    public interface OnItemClickListener {
        void onItemClick(int position, View v);
        void onItemCmdClick(int position);
    }
    private Context context;
    public void setOnItemClickListener (OnItemClickListener listener) {
        this.listener = listener;
    }
    public SearchAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == EMPTY_ELEMENT){
            View v = layoutInflater.inflate(R.layout.item_empty_result, parent, false);
            return new TextHolder(v);
        }else{
         View v = layoutInflater.inflate(R.layout.item_search, parent, false);
         return new ItemHolder(v, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Book book = books.get(position);
        if (holder instanceof TextHolder){
            TextHolder textHolder = (TextHolder) holder;
            String text = context.getString(R.string.no_result_found) + " : " + book.getAuthor();
            textHolder.textView.setText(text);
        }else  {
            ItemHolder itemHolder = (ItemHolder) holder;
            Glide.with(context)
                    .load(book.getImage1_front())
                    .error(R.drawable.bg_image)
                    .into(itemHolder.book_image);

            itemHolder.book_title.setText(book.getTitle());
            itemHolder.book_author.setText(book.getAuthor());
            itemHolder.book_edition.setText(book.getEditor());
            itemHolder.book_prise.setText(Utils.formatPrise(book.getUnit_prise()));
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (books.get(position).getTitle().equals(Search.EMPTY_BOOK))
            return EMPTY_ELEMENT;
        else
            return ITEM_ELEMENT;
    }

    private static class TextHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public TextHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_message);
        }
    }

    private static class ItemHolder extends RecyclerView.ViewHolder  {
        ImageView book_image;
        TextView book_title, book_author, book_edition, book_prise;
        Button btn_commended;
        public ItemHolder (@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);
            book_image = itemView.findViewById(R.id.book_image);
            book_title = itemView.findViewById(R.id.book_title);
            book_author = itemView.findViewById(R.id.book_author);
            book_edition = itemView.findViewById(R.id.book_edition);
            book_prise = itemView.findViewById(R.id.book_prise);
            btn_commended = itemView.findViewById(R.id.btn_commended);

            btn_commended.setOnClickListener(v ->{
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    mListener.onItemCmdClick(position);
            });
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    mListener.onItemClick(position, book_image);
            });
        }
    }
}
