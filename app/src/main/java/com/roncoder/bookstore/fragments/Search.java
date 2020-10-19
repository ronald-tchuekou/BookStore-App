package com.roncoder.bookstore.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.firestore.Query;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.SearchAdapter;
import com.roncoder.bookstore.dbHelpers.BookHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    private static final String TAG = "Search";
    @SuppressLint("StaticFieldLeak")
    private static Search instance = null;
    private OnSearchFocusListener listener;
    public static final String EMPTY_BOOK = "Empty element";
    EditText searchView;
    ImageView init_view_search;
    SearchAdapter searchAdapter;
    View progress_indicator, btn_search;
    View.OnClickListener clickListener;

    List<Book> searchBooks = new ArrayList<>();
    RecyclerView recyclerSearch;
    String query = "";

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
        init_view_search = root.findViewById(R.id.init_view_search);
        btn_search = root.findViewById(R.id.btn_search);
        root.findViewById(R.id.btn_drawer).setOnClickListener(clickListener);
        btn_search.setOnClickListener(v -> setSearchList(query));
        searchView = root.findViewById(R.id.editText_search);
        progress_indicator = root.findViewById(R.id.progress_indicator);
        searchView.addTextChangedListener(searchWatcher);
        searchView.setOnFocusChangeListener((v, hasFocus) -> this.listener.onSearchFocus(hasFocus));
        initViews(root);
        adaptedListViews();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        searchView.getEditableText().clear();
        searchBooks.clear();
        searchAdapter.notifyDataSetChanged();
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
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Utils.bookDetail(requireActivity(), searchBooks.get(position), v,
                        Home.TRANSITION_IMAGE_NAME);
            }

            @Override
            public void onItemCmdClick(int position) {
                Utils.commendActivity(requireActivity(), searchBooks.get(position));
            }
        });
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
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { query = s.toString().trim(); }
        @Override
        public void afterTextChanged(Editable s) { }
    };

    private void setSearchList(String result) {
        if (result.equals("")) {
            recyclerSearch.setVisibility(View.GONE);
            return ;
        }
        init_view_search.setVisibility(View.GONE);
        recyclerSearch.setVisibility(View.VISIBLE);
        searchBooks.clear();
        getNewBookList(result);
    }

    private void getNewBookList(String query) {
        Query queries = BookHelper.getAllBooks();
        progress_indicator.setVisibility(View.VISIBLE);

            queries.addSnapshotListener((value, error) -> {
                Utils.dismissDialog();
                if (error != null) {
                    Log.e(TAG, "getNewBookList: ", error);
                    return;
                }
                if (value != null) {
                    searchBooks.clear();
                    List<Book> books = value.toObjects(Book.class);
                    for (Book b : books) {
                        if (b.getTitle().contains(query) || b.getAuthor().contains(query) || b.getEditor().contains(query))
                            searchBooks.add(b);
                    }
                    /* If the content list is empty */
                    if (searchBooks.isEmpty()) {
                        Book empty_book = new Book();
                        empty_book.setTitle(EMPTY_BOOK);
                        empty_book.setAuthor(query);
                        searchBooks.add(empty_book);
                    }
                    searchAdapter.notifyDataSetChanged();
                    progress_indicator.setVisibility(View.GONE);

                }
            });
    }
}
