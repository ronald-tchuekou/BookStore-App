package com.roncoder.bookstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.fragments.Search;
import com.roncoder.bookstore.models.Book;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    public static final int EMPTY_ELEMENT = 0;
    public static final int ITEM_ELEMENT = 1;
    private OnItemClickListener listener;
    private List<Book> books;
    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }
    public void setOnItemClickListener (OnItemClickListener listener) {
        this.listener = listener;
    }
    public SearchAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        if (holder instanceof TextHolder){
            TextHolder textHolder = (TextHolder) holder;
            // TODO implement the error state.
        }else  {
            ItemHolder itemHolder = (ItemHolder) holder;
            // TODO implement the value state.
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
        public ItemHolder (@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);
            book_image = itemView.findViewById(R.id.book_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    mListener.onItemClick(position, book_image);
            });
        }
    }
}
