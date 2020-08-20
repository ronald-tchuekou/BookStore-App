package com.roncoder.bookstore.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.SearchAdapter;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Search extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static Search instance = null;
    private OnSearchFocusListener listener;
    public static final String EMPTY_BOOK = "Empty element";
    SearchAdapter searchAdapter;
    View progress_indicator;
    View.OnClickListener clickListener;
    List<Book> searchBooks = new ArrayList<>();
    RecyclerView recyclerSearch;
    public interface OnSearchFocusListener {
        void onSearchFocus(boolean hasFocus);
    }
    public void setOnSearchFocus (OnSearchFocusListener listener){
        this.listener = listener;
    }
    private Search() { }
    public static Search getInstance() {
        if (instance == null)
            instance = new Search();
        return instance;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setOnDrawerClickListener (View.OnClickListener listener)  {
        clickListener = listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        root.findViewById(R.id.btn_drawer).setOnClickListener(clickListener);
        EditText searchView = root.findViewById(R.id.editText_search);
        progress_indicator = root.findViewById(R.id.progress_indicator);
        searchView.addTextChangedListener(searchWatcher);
        searchView.setOnFocusChangeListener((v, hasFocus) -> this.listener.onSearchFocus(hasFocus));
        initViews(root);
        adaptedListViews();
        return root;
    }
    /**
     * Function to adapted all recycler view.
     */
    private void adaptedListViews() {

        LinearLayoutManager layoutManagerSearch = new LinearLayoutManager(requireContext());
        searchAdapter = new SearchAdapter(searchBooks);
        recyclerSearch.setLayoutManager(layoutManagerSearch);
        recyclerSearch.setHasFixedSize(true);
        recyclerSearch.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener((position, v) -> Utils.bookDetail(requireActivity(),
                searchBooks.get(position), v, Home.TRANSITION_IMAGE_NAME));
    }

    /**
     * Function to initialized all view.
     * @param root Root container.
     */
    private void initViews(View root) {
        recyclerSearch = root.findViewById(R.id.search_recycler);
    }

    /**
     * callback to watch the search queries.
     */
    private TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            progress_indicator.setVisibility(View.VISIBLE);
            setSearchList(s.toString().trim());
        }

        @Override
        public void afterTextChanged(Editable s) {
            progress_indicator.setVisibility(View.GONE);
        }
    };

    private void setSearchList(String result) {
        if (result.equals("")) {
            recyclerSearch.setVisibility(View.GONE);
            return ;
        }
        recyclerSearch.setVisibility(View.VISIBLE);
        searchBooks.clear();
        searchBooks.addAll(getNewBookList());
        searchAdapter.notifyDataSetChanged();
        progress_indicator.setVisibility(View.GONE);
    }

    private Collection<? extends Book> getNewBookList() {
        List<Book> bookList = new ArrayList<>();

        // TODO implement this to get book list.

        if (bookList.isEmpty())
            bookList.add(new Book(-1, EMPTY_BOOK, "", "", "", "",
                    "", 0f, 0));
        progress_indicator.setVisibility(View.GONE);
        return bookList;
    }

}
