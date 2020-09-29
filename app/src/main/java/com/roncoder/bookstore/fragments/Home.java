package com.roncoder.bookstore.fragments;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.Query;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.AllCycleBook;
import com.roncoder.bookstore.activities.ClassBook;
import com.roncoder.bookstore.activities.MainActivity;
import com.roncoder.bookstore.adapters.HomeAdapter;
import com.roncoder.bookstore.dbHelpers.BookHelper;
import com.roncoder.bookstore.dbHelpers.ClassHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Classes;
import com.roncoder.bookstore.models.Factory;
import com.roncoder.bookstore.utils.Utils;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment implements View.OnClickListener {

    public static final String EXTRA_CYCLE = "CycleType";
    public static final String EXTRA_CLASS = "class_books";
    public static final String TRANSITION_IMAGE_NAME = "bookImage";
    public static final String EXTRA_IS_FRANCOPHONE = "is_francophone";
    private static final String TAG = "Home fragment";
    private static boolean is_francophone_section = true;
    @SuppressLint("StaticFieldLeak")
    private static Home instance = null;
    private String[] class_group;
    private ProgressBar progress_first, progress_second, progress_third, progress_class;
    private ImageView empty_first, empty_second, empty_third, empty_class;
    private TextView all_primary_cycle, all_first_cycle, all_second_cycle, nursery_text, primary_text, secondary_text, class_text;
    private MultiSnapRecyclerView nursery_cycle_recyclerView, primary_cycle_recyclerView, secondary_cycle_recyclerView, classes_recyclerView;
    private List<Factory> nursery_cycleBooks, primary_cycleBooks, secondary_cycleBooks, classesList;
    private HomeAdapter nursery_cycleBookAdapter, primary_cycleBookAdapter, second_cycleBookAdapter, classesAdapter;
    Query nurseryQuery, primaryQuery, secondaryQuery, classQuery;

    public Home() { }

    public static Home getInstance() {
        if (instance == null)
            instance = new Home();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.findViewById(R.id.section).setOnClickListener(this::showPopupMenu);
        class_group = getResources().getStringArray(R.array.cycles);
        initViews(root);
        initClickListeners();
        initRecyclerViews();
        initItemListener();
        if( is_francophone_section ) setToFrancoPhoneSection();
        else setToAngloPhoneSection();
        updateContentList();
        return root;
    }

    private void initViews(View root) {
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
        progress_class = root.findViewById(R.id.progress_class);
        progress_first = root.findViewById(R.id.progress_first);
        progress_second = root.findViewById(R.id.progress_second);
        progress_third = root.findViewById(R.id.progress_third);
        empty_class = root.findViewById(R.id.empty_class);
        empty_first = root.findViewById(R.id.empty_first);
        empty_second = root.findViewById(R.id.empty_second);
        empty_third = root.findViewById(R.id.empty_third);
    }

    /**
     * Function to set the listener to the recycler view.
     */
    private void initItemListener() {
        nursery_cycleBookAdapter.setOnCycleItemBookListener((position, view) -> startBookDetailActivity((Book) nursery_cycleBooks.get(position), view));
        primary_cycleBookAdapter.setOnCycleItemBookListener((position, view) -> startBookDetailActivity((Book) primary_cycleBooks.get(position), view));
        second_cycleBookAdapter.setOnCycleItemBookListener((position, view) -> startBookDetailActivity((Book) secondary_cycleBooks.get(position), view));
        classesAdapter.setOnClassItemClickListener(position -> {
            Intent classIntent = new Intent(requireContext(), ClassBook.class);
            classIntent.putExtra(EXTRA_CLASS, (Classes) classesList.get(position));
            requireActivity().startActivityForResult(classIntent, MainActivity.REQUEST_CODE_CYCLE_CLASS);
        });
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
        empty_first.setVisibility(View.INVISIBLE);
        empty_second.setVisibility(View.INVISIBLE);
        empty_third.setVisibility(View.INVISIBLE);
        empty_class.setVisibility(View.INVISIBLE);
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

    }

    private void setNurseryContent() {
        String cycle;
        if (is_francophone_section)
            cycle = Utils.NURSERY_F;
        else
            cycle = Utils.NURSERY_A;
        Log.i(TAG, "setNurseryContent: Cycle = " + cycle);
        nurseryQuery = BookHelper.getBooksByCycle (cycle);
        progress_first.setVisibility(View.VISIBLE);
        nurseryQuery.addSnapshotListener((value, error) -> {
            progress_first.setVisibility(View.GONE);
            if (error != null) {
                Log.e(TAG, "setNurseryContent: ", error);
                return;
            }
            if (value != null) {
                nursery_cycleBooks.clear();
                List<Book> books = value.toObjects(Book.class);
                nursery_cycleBooks.addAll(books);
                if (books.isEmpty())
                    empty_first.setVisibility(View.VISIBLE);
                else
                    empty_first.setVisibility(View.INVISIBLE);
                Log.i(TAG, "setNurseryContent List : " + nursery_cycleBooks);
                nursery_cycleBookAdapter.notifyDataSetChanged();
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

    }

    private void setPrimaryContent() {
        String cycle;
        if (is_francophone_section)
            cycle = Utils.PRIMARY_F;
        else
            cycle = Utils.PRIMARY_A;
        Log.i(TAG, "setPrimaryContent: Cycle = " + cycle);
        primaryQuery = BookHelper.getBooksByCycle (cycle);
        progress_second.setVisibility(View.VISIBLE);
        primaryQuery.addSnapshotListener((value, error) -> {
            progress_second.setVisibility(View.GONE);
            if (error != null) {
                Log.e(TAG, "setPrimaryContent: ", error);
                return;
            }
            if (value != null) {
                primary_cycleBooks.clear();
                List<Book> books = value.toObjects(Book.class);
                primary_cycleBooks.addAll(books);
                if (books.isEmpty())
                    empty_second.setVisibility(View.VISIBLE);
                else
                    empty_second.setVisibility(View.INVISIBLE);

                Log.i(TAG, "setPrimaryContent List :" + primary_cycleBooks);

                primary_cycleBookAdapter.notifyDataSetChanged();
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

    }

    private void setSecondaryContent() {
        String cycle;
        if (is_francophone_section)
            cycle = Utils.SECONDARY_F;
        else
            cycle = Utils.SECONDARY_A;
        Log.i(TAG, "setSecondaryContent: Cycle = " + cycle);
        secondaryQuery = BookHelper.getBooksByCycle (cycle);
        progress_third.setVisibility(View.VISIBLE);
        secondaryQuery.addSnapshotListener((value, error) -> {
            progress_third.setVisibility(View.GONE);
            if (error != null) {
                Log.e(TAG, "setSecondaryContent: ", error);
                return;
            }
            if (value != null) {
                secondary_cycleBooks.clear();
                List<Book> books = value.toObjects(Book.class);
                secondary_cycleBooks.addAll(books);
                if (books.isEmpty())
                    empty_third.setVisibility(View.VISIBLE);
                else
                    empty_third.setVisibility(View.INVISIBLE);

                Log.i(TAG, "setSecondaryContent List : " + secondary_cycleBooks);

                second_cycleBookAdapter.notifyDataSetChanged();
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

    }

    private void setClassContent() {
        String cycle;
        if (is_francophone_section)
            cycle = Utils.CLASS_F;
        else
            cycle = Utils.CLASS_A;
        classQuery = ClassHelper.getAllClass();
        progress_class.setVisibility(View.VISIBLE);
        classQuery.addSnapshotListener((value, error) -> {
            progress_class.setVisibility(View.GONE);
            if (error != null) {
                Log.e(TAG, "setClassContent: ", error);
                return;
            }

            if (value != null) {
                classesList.clear();
                List<Classes> classes = value.toObjects(Classes.class);

                for (Classes c : classes) {
                    if (c.getCycle().contains(cycle))
                        classesList.add(c);
                }

                if (classesList.isEmpty())
                    empty_class.setVisibility(View.VISIBLE);
                else
                    empty_class.setVisibility(View.INVISIBLE);

                Log.i(TAG, "setClassContent List : " + classesList);

                classesAdapter.notifyDataSetChanged();
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
