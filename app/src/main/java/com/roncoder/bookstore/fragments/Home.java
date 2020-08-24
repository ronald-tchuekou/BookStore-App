package com.roncoder.bookstore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.AllCycleBook;
import com.roncoder.bookstore.activities.ClassBook;
import com.roncoder.bookstore.activities.MainActivity;
import com.roncoder.bookstore.adapters.HomeAdapter;
import com.roncoder.bookstore.api.Result;
import com.roncoder.bookstore.dbHelpers.BookHelper;
import com.roncoder.bookstore.dbHelpers.ClassHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Classes;
import com.roncoder.bookstore.models.Factory;
import com.roncoder.bookstore.utils.Utils;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment implements View.OnClickListener {

    public static final String EXTRA_CYCLE = "CycleType";
    public static final String EXTRA_CLASS = "class_books";
    public static final String TRANSITION_IMAGE_NAME = "bookImage";
    public static final String EXTRA_IS_FRANCOPHONE = "is_francophone";
    private static final String TAG = "Home fragment";
    private static boolean is_francophone_section = true;
    private String[] class_group;
    private TextView all_primary_cycle, all_first_cycle, all_second_cycle, nursery_text, primary_text, secondary_text, class_text;
    private MultiSnapRecyclerView nursery_cycle_recyclerView, primary_cycle_recyclerView, secondary_cycle_recyclerView,
            classes_recyclerView;
    private List<Factory> nursery_cycleBooks, primary_cycleBooks, secondary_cycleBooks, classesList;
    private HomeAdapter nursery_cycleBookAdapter, primary_cycleBookAdapter, second_cycleBookAdapter, classesAdapter;

    public Home() { }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.findViewById(R.id.section).setOnClickListener(this::showPopupMenu);
        class_group = getResources().getStringArray(R.array.class_groups);
        all_primary_cycle = root.findViewById(R.id.all_primary_cycle);
        all_first_cycle = root.findViewById(R.id.all_first_cycle);
        all_second_cycle = root.findViewById(R.id.all_second_cycle);
        nursery_cycle_recyclerView = root.findViewById(R.id.first_cycle_recyclerView);
        primary_cycle_recyclerView = root.findViewById(R.id.second_cycle_recyclerView);
        secondary_cycle_recyclerView = root.findViewById(R.id.third_cycle_recyclerView);
        classes_recyclerView = root.findViewById(R.id.classes_recyclerView);
        nursery_text = root.findViewById(R.id.textView9);
        primary_text = root.findViewById(R.id.textView10);
        secondary_text = root.findViewById(R.id.textView11);
        class_text = root.findViewById(R.id.textView12);
        initClickListeners();
        initRecyclerViews();
        initItemListener();
        setToFrancoPhoneSection();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContentList();
    }

    /**
     * Function to set the listener to the recycler view.
     */
    private void initItemListener() {
        nursery_cycleBookAdapter.setOnCycleItemBookListener(new HomeAdapter.OnCycleItemBookListener() {
            @Override
            public void onCycleItemClickListener(int position, View view) {
                startBookDetailActivity((Book) nursery_cycleBooks.get(position), view);
            }

            @Override
            public void onBuyButtonClickListener(int position) {
                setToCommendActivity((Book) nursery_cycleBooks.get(position));
            }
        });
        primary_cycleBookAdapter.setOnCycleItemBookListener(new HomeAdapter.OnCycleItemBookListener() {
            @Override
            public void onCycleItemClickListener(int position, View view) {
                startBookDetailActivity((Book) primary_cycleBooks.get(position), view);
            }

            @Override
            public void onBuyButtonClickListener(int position) {
                setToCommendActivity((Book) primary_cycleBooks.get(position));
            }
        });
        second_cycleBookAdapter.setOnCycleItemBookListener(new HomeAdapter.OnCycleItemBookListener() {
            @Override
            public void onCycleItemClickListener(int position, View view) {
                startBookDetailActivity((Book) secondary_cycleBooks.get(position), view);
            }

            @Override
            public void onBuyButtonClickListener(int position) {
                setToCommendActivity((Book) secondary_cycleBooks.get(position));
            }
        });
        classesAdapter.setOnClassItemClickListener(position -> {
            Intent classIntent = new Intent(requireContext(), ClassBook.class);
            classIntent.putExtra(EXTRA_CLASS, (Classes) classesList.get(position));
            requireActivity().startActivityForResult(classIntent, MainActivity.REQUEST_CODE_CYCLE_CLASS);
        });
    }

    private void setToCommendActivity(Book book) {
        Utils.commendActivity(requireActivity(), book);
    }

    /**
     * Function to start the books detail activities.
     * @param book Book item.
     */
    private void startBookDetailActivity(Book book, View v) {
       Utils.bookDetail(requireActivity(), book, v, TRANSITION_IMAGE_NAME);
    }

    private void initClickListeners() {
        all_primary_cycle.setOnClickListener(this);
        all_first_cycle.setOnClickListener(this);
        all_second_cycle.setOnClickListener(this);
    }

    private void initRecyclerViews() {
        adapter_nursery_cycleBooks();
        adapter_primary_cycleBooks();
        adapt_secondary_cycleBooks();
        adaptClassesBooks();
        updateContentList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_primary_cycle:
                setToAllBook(Utils.ListTypes.PRIMARY_CYCLE);
                break;
            case R.id.all_first_cycle:
                setToAllBook(Utils.ListTypes.FIRST_CYCLE);
                break;
            case R.id.all_second_cycle:
                setToAllBook(Utils.ListTypes.SECOND_CYCLE);
                break;
        }
    }

    private void updateContentList() {
        setNurseryContent();
        setPrimaryContent();
        setSecondaryContent();
        setClassContent();
    }

    /**
     * Function to set at all cycle books.
     * @param cycleType Cycle type.
     */
    private void setToAllBook(Utils.ListTypes cycleType) {
        Intent allBookIntent = new Intent(requireContext(), AllCycleBook.class);
        allBookIntent.putExtra(EXTRA_CYCLE, cycleType);
        allBookIntent.putExtra(EXTRA_IS_FRANCOPHONE, is_francophone_section);
        requireActivity().startActivityForResult(allBookIntent, MainActivity.REQUEST_CODE_CYCLE_CLASS);
    }

    private void adapter_nursery_cycleBooks() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
                false);
        nursery_cycle_recyclerView.setHasFixedSize(true);
        nursery_cycle_recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        nursery_cycle_recyclerView.setLayoutManager(layoutManager);

        // initialisation of the adapter.
        if (nursery_cycleBooks == null)
            nursery_cycleBooks = new ArrayList<>();
        else
            nursery_cycleBooks.clear();

        nursery_cycleBookAdapter = new HomeAdapter(nursery_cycleBooks, Utils.ListTypes.PRIMARY_CYCLE);
        nursery_cycle_recyclerView.setAdapter(nursery_cycleBookAdapter);

        setNurseryContent();
    }

    private void setNurseryContent() {
        String cycle;
        if (is_francophone_section)
            cycle = Utils.NURSERY_F;
        else
            cycle = Utils.NURSERY_A;

        BookHelper.getBooksByCycle (cycle).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.i(TAG, "onResponse: Call => " + call + " Result => " + response.body());
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
                        nursery_cycleBooks.add(gson.fromJson(element, Book.class));
                    }
                    nursery_cycleBookAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.setDialogMessage(requireActivity(), t.getMessage());
                Log.e(TAG, "onFailure: " + call, t);
            }
        });
    }

    private void adapter_primary_cycleBooks() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
                false);
        primary_cycle_recyclerView.setHasFixedSize(true);
        primary_cycle_recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        primary_cycle_recyclerView.setLayoutManager(layoutManager);
        // initialisation of the adapter.
        if (primary_cycleBooks == null)
            primary_cycleBooks = new ArrayList<>();
        else
            primary_cycleBooks.clear();
        primary_cycleBookAdapter = new HomeAdapter(primary_cycleBooks, Utils.ListTypes.FIRST_CYCLE);
        primary_cycle_recyclerView.setAdapter(primary_cycleBookAdapter);

        setPrimaryContent();
    }

    private void setPrimaryContent() {
        String cycle;
        if (is_francophone_section)
            cycle = Utils.PRIMARY_F;
        else
            cycle = Utils.PRIMARY_A;

        BookHelper.getBooksByCycle (cycle).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Result result = response.body();
                Log.i(TAG, "onResponse: Call => " + call + " Result => " + response.body());

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
                        primary_cycleBooks.add(gson.fromJson(element, Book.class));
                    }
                    primary_cycleBookAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.setDialogMessage(requireActivity(), t.getMessage());
                Log.e(TAG, "onFailure: " + call, t);
            }
        });
    }

    private void adapt_secondary_cycleBooks() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
                false);
        secondary_cycle_recyclerView.setHasFixedSize(true);
        secondary_cycle_recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        secondary_cycle_recyclerView.setLayoutManager(layoutManager);
        // initialisation of the adapter.
        if (secondary_cycleBooks == null)
            secondary_cycleBooks = new ArrayList<>();
        else
            secondary_cycleBooks.clear();
        second_cycleBookAdapter = new HomeAdapter(secondary_cycleBooks, Utils.ListTypes.SECOND_CYCLE);
        secondary_cycle_recyclerView.setAdapter(second_cycleBookAdapter);

        setSecondaryContent();
    }

    private void setSecondaryContent() {
        String cycle;
        if (is_francophone_section)
            cycle = Utils.SECONDARY_F;
        else
            cycle = Utils.SECONDARY_A;

        BookHelper.getBooksByCycle (cycle).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.i(TAG, "onResponse: Call => " + call + " Result => " + response.body());
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
                        secondary_cycleBooks.add(gson.fromJson(element, Book.class));
                    }
                    second_cycleBookAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.setDialogMessage(requireActivity(), t.getMessage());
                Log.e(TAG, "onFailure: " + call, t);
            }
        });
    }

    private void adaptClassesBooks() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
                false);
        classes_recyclerView.setHasFixedSize(true);
        classes_recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        classes_recyclerView.setLayoutManager(layoutManager);
        // initialisation of the adapter.
        if (classesList == null)
            classesList = new ArrayList<>();
        else
            classesList.clear();

        classesAdapter = new HomeAdapter(classesList, Utils.ListTypes.CLASSES);
        classes_recyclerView.setAdapter(classesAdapter);

        setClassContent();
    }

    private void setClassContent() {
        String cycle;
        if (is_francophone_section)
            cycle = Utils.CLASS_F;
        else
            cycle = Utils.CLASS_A;
        ClassHelper.getClassesByCycle(cycle).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.i(TAG, "onResponse: Call => " + call + " Result => " + response.body());
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
                        classesList.add(gson.fromJson(element, Classes.class));
                    }
                    classesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.setDialogMessage(requireActivity(), t.getMessage());
                Log.e(TAG, "onFailure: " + call, t);
            }
        });
    }

    /**
     * Function to show popup menu to change de section presentation of books.
     * @param view View holder.
     */
    private void showPopupMenu (View view) {
        PopupMenu popupMenu = new PopupMenu(requireActivity(), view);
        popupMenu.inflate(R.menu.menu_section);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.franco_option:
                    setToFrancoPhoneSection();
                    break;
                case R.id.anglo_option:
                    setToAngloPhoneSection();
                    break;
            }
            updateContentList();
            return true;
        });
        popupMenu.show();
    }

    /**
     * Function to set the francophone section.
     */
    private void setToFrancoPhoneSection () {
        is_francophone_section = true;
        changeTextView(nursery_text, class_group[1]);
        changeTextView(primary_text, class_group[3]);
        changeTextView(secondary_text, class_group[5]);
        changeTextView(class_text, getString(R.string.classes));
    }

    /**
     * Function to set the anglophone section.
     */
    private void setToAngloPhoneSection () {
        is_francophone_section = false;
        changeTextView(nursery_text, class_group[0]);
        changeTextView(primary_text, class_group[2]);
        changeTextView(secondary_text, class_group[4]);
        changeTextView(class_text, getString(R.string.classes));
    }
    /**
     * Function that change the text of the textView.
     * @param textView TextView.
     * @param stringRes String resource.
     */
    private void changeTextView (TextView textView, String stringRes) {
        textView.animate()
                .alphaBy(1f)
                .setDuration(150)
                .withEndAction(() -> textView.setText(stringRes));
        textView.animate()
                .setStartDelay(150)
                .alphaBy(0f)
                .setDuration(150);
    }
}
