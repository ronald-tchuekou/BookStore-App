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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.adapters.SearchAdapter;
import com.roncoder.bookstore.api.Result;
import com.roncoder.bookstore.dbHelpers.BookHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends Fragment {
    private static final String TAG = "Search";
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
        getNewBookList(result);
    }

    private void getNewBookList(String query) {
        BookHelper.getQueryBooks(query).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Result result = response.body();

                if (result == null)
                    return;

                if (result.getError()) {
                    Utils.setDialogMessage(requireActivity(), result.getMessage());
                    Log.e(TAG, "Error process : " + result.getMessage(), null);
                }
                else if (result.getSuccess()) {
                    String value = result.getValue();
                    Log.i(TAG, "Success process : " + value);
                    Gson gson = new Gson();
                    JsonArray bookArray = (JsonArray) JsonParser.parseString(value);
                    for (JsonElement element : bookArray) {
                        searchBooks.add(gson.fromJson(element, Book.class));
                    }

                    /* If the content list is empty */
                    if (searchBooks.isEmpty()){
                        Book empty_book = new Book();
                        empty_book.setTitle(EMPTY_BOOK);
                        empty_book.setAuthor(query);
                        searchBooks.add(empty_book);
                    }

                    searchAdapter.notifyDataSetChanged();
                }
                progress_indicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.setDialogMessage(requireActivity(), t.getMessage());
                Log.e(TAG, "onFailure: " + call, t);
                progress_indicator.setVisibility(View.GONE);
            }
        });
    }

}
