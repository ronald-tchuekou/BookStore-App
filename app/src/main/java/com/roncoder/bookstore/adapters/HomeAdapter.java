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
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Classes;
import com.roncoder.bookstore.models.Factory;
import com.roncoder.bookstore.utils.Utils;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Factory> factories;
    private Utils.ListTypes types;
    private OnClassesItemClickListener mClassesListener;
    private OnCycleItemBookListener mCycleListener;
    private Context context;
    public interface OnCycleItemBookListener {
      void onCycleItemClickListener (int position, View view);
      void onBuyButtonClickListener(int position);
    }
    public interface OnClassesItemClickListener {
      void onClassesItemClickListener(int position);
    }
    public HomeAdapter (List<Factory> factories, Utils.ListTypes listTypes){
        this.factories = factories;
        this.types = listTypes;
    }
    public void setOnClassItemClickListener (OnClassesItemClickListener mListener) {
        mClassesListener = mListener;
    }
    public void setOnCycleItemBookListener (OnCycleItemBookListener mListener) {
        mCycleListener = mListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (types == Utils.ListTypes.PRIMARY_CYCLE) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_first_cycle_book, parent, false);
            return new CycleBooksHolder(v, mCycleListener);
        }if (types == Utils.ListTypes.CLASSES) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_classes_book, parent, false);
            return new ClassesBooksHolder(v, mClassesListener);
        }else{
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_second_third_cycle_book, parent, false);
            return new CycleBooksHolder(v, mCycleListener);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Factory factory = factories.get(position);
        if (holder instanceof CycleBooksHolder){
            Book book = (Book) factory;
            String title = book.getTitle() + " : " + book.getAuthor();
            CycleBooksHolder cycleHolder = (CycleBooksHolder) holder;
            cycleHolder.prise.setText(Utils.formatPrise(book.getUnit_prise()));
            cycleHolder.title.setText(title);
            Glide.with(context)
                    .load(book.getImage1_front())
                    .placeholder(R.drawable.bg_image)
                    .error(R.drawable.bg_image)
                    .into(cycleHolder.book_iamge);
        }
        else {
            Classes classes = (Classes) factories.get(position);
            ClassesBooksHolder classesHolder = (ClassesBooksHolder) holder;
            classesHolder.title.setText(classes.getName());
        }
    }

    @Override
    public int getItemCount() {
        return factories.size();
    }

    static class CycleBooksHolder extends RecyclerView.ViewHolder {
        ImageView book_iamge;
        TextView title, prise;
        Button buy;

        CycleBooksHolder(@NonNull View itemView, OnCycleItemBookListener listener) {
            super(itemView);
            book_iamge = itemView.findViewById(R.id.book_image);
            title = itemView.findViewById(R.id.book_title);
            prise = itemView.findViewById(R.id.book_prise);
            buy = itemView.findViewById(R.id.buy_button);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onCycleItemClickListener(position, book_iamge);
            });
            buy.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onBuyButtonClickListener(position);
            });
        }
    }

    static class ClassesBooksHolder extends RecyclerView.ViewHolder {
        ImageView image, icon;
        TextView title;

        ClassesBooksHolder(@NonNull View itemView, OnClassesItemClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    listener.onClassesItemClickListener(position);
            });
        }
    }
}
