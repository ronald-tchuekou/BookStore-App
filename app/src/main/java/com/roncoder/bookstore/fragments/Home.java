package com.roncoder.bookstore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.AllCycleBook;
import com.roncoder.bookstore.activities.ClassBook;
import com.roncoder.bookstore.activities.MainActivity;
import com.roncoder.bookstore.adapters.HomeAdapter;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Classe;
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
    private static boolean is_francophone_section = true;
    private String[] class_group;
    private TextView all_primary_cycle, all_first_cycle, all_second_cycle, nursery_text, primary_text, secondary_text, class_text;
    private MultiSnapRecyclerView primary_cycle_recyclerView, first_cycle_recyclerView, second_cycle_recyclerView,
            classes_recyclerView;
    private List<Factory> primary_cycleBooks, first_cycleBooks, second_cycleBooks, classesBooks;
    private HomeAdapter primary_cycleBookAdapter, first_cycleBookAdapter, second_cycleBookAdapter, classesBookAdapter;

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
        primary_cycle_recyclerView = root.findViewById(R.id.first_cycle_recyclerView);
        first_cycle_recyclerView = root.findViewById(R.id.second_cycle_recyclerView);
        second_cycle_recyclerView = root.findViewById(R.id.third_cycle_recyclerView);
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

    /**
     * Function to set the listener to the recycler view.
     */
    private void initItemListener() {
        primary_cycleBookAdapter.setOnCycleItemBookListener(new HomeAdapter.OnCycleItemBookListener() {
            @Override
            public void onCycleItemClickListener(int position, View view) {
                startCycleActivity((Book) primary_cycleBooks.get(position), view);
            }

            @Override
            public void onBuyButtonClickListener(int position) {
                setToCommendActivity((Book) primary_cycleBooks.get(position));
            }
        });
        first_cycleBookAdapter.setOnCycleItemBookListener(new HomeAdapter.OnCycleItemBookListener() {
            @Override
            public void onCycleItemClickListener(int position, View view) {
                startCycleActivity((Book) first_cycleBooks.get(position), view);
            }

            @Override
            public void onBuyButtonClickListener(int position) {
                setToCommendActivity((Book) first_cycleBooks.get(position));
            }
        });
        second_cycleBookAdapter.setOnCycleItemBookListener(new HomeAdapter.OnCycleItemBookListener() {
            @Override
            public void onCycleItemClickListener(int position, View view) {
                startCycleActivity((Book) second_cycleBooks.get(position), view);
            }

            @Override
            public void onBuyButtonClickListener(int position) {
                setToCommendActivity((Book) second_cycleBooks.get(position));
            }
        });
        classesBookAdapter.setOnClassItemClickListener(position -> {
            Intent classIntent = new Intent(requireContext(), ClassBook.class);
            classIntent.putExtra(EXTRA_CLASS, (Classe) classesBooks.get(position));
            requireActivity().startActivityForResult(classIntent, MainActivity.REQUEST_CODE_CYCLE_CLASS);
        });
    }

    private void setToCommendActivity(Book book) {
        Utils.commendActivity(requireActivity(), book);
    }

    /**
     * Function to start the cycles books activities.
     * @param book Book item.
     */
    private void startCycleActivity(Book book, View v) {
       Utils.bookDetail(requireActivity(), book, v, TRANSITION_IMAGE_NAME);
    }

    private void initClickListeners() {
        all_primary_cycle.setOnClickListener(this);
        all_first_cycle.setOnClickListener(this);
        all_second_cycle.setOnClickListener(this);
    }
    private void initRecyclerViews() {
        adapter_primary_cycleBooks();
        adapter_first_cycleBooks();
        adapt_second_cycleBooks();
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
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres",
                "NMI", "image", "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBooks.add(new Book(2, "L'Excellence en Français 6e", "Joseph NANFAH et autres", "NMI", "image",
                "Neuf", "Secondaire Francophone", 3000, 10));
        primary_cycleBookAdapter = new HomeAdapter(primary_cycleBooks, Utils.ListTypes.PRIMARY_CYCLE);
        primary_cycle_recyclerView.setAdapter(primary_cycleBookAdapter);
    }
    private void adapter_first_cycleBooks() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
                false);
        first_cycle_recyclerView.setHasFixedSize(true);
        first_cycle_recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        first_cycle_recyclerView.setLayoutManager(layoutManager);
        // initialisation of the adapter.
        if (first_cycleBooks == null)
            first_cycleBooks = new ArrayList<>();
        else
            first_cycleBooks.clear();
        first_cycleBooks.addAll(primary_cycleBooks);
        first_cycleBookAdapter = new HomeAdapter(first_cycleBooks, Utils.ListTypes.FIRST_CYCLE);
        first_cycle_recyclerView.setAdapter(first_cycleBookAdapter);
    }
    private void adapt_second_cycleBooks() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
                false);
        second_cycle_recyclerView.setHasFixedSize(true);
        second_cycle_recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        second_cycle_recyclerView.setLayoutManager(layoutManager);
        // initialisation of the adapter.
        if (second_cycleBooks == null)
            second_cycleBooks = new ArrayList<>();
        else
            second_cycleBooks.clear();
        second_cycleBooks.addAll(first_cycleBooks);
        second_cycleBookAdapter = new HomeAdapter(second_cycleBooks, Utils.ListTypes.SECOND_CYCLE);
        second_cycle_recyclerView.setAdapter(second_cycleBookAdapter);
    }

    private void adaptClassesBooks() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
                false);
        classes_recyclerView.setHasFixedSize(true);
        classes_recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        classes_recyclerView.setLayoutManager(layoutManager);
        // initialisation of the adapter.
        if (classesBooks == null)
            classesBooks = new ArrayList<>();
        else
            classesBooks.clear();
        classesBooks.add(new Classe(1, "SIL", "SIL"));
        classesBooks.add(new Classe(2, "CP" ,"CP"));
        classesBooks.add(new Classe(3, "CE1", "cours préparatoire 1"));
        classesBooks.add(new Classe(4, "CE2", "cours préparatoire 2"));
        classesBooks.add(new Classe(5, "CM1", "Cours moins 1"));
        classesBooks.add(new Classe(6, "CM2", "Cours moins 2"));
        classesBooks.add(new Classe(7, "CLASS I", "CLASS I"));
        classesBooks.add(new Classe(8, "CLASS II", "CLASS II"));
        classesBooks.add(new Classe(9, "CLASS III", "CLASS III"));
        classesBooks.add(new Classe(10, "CLASS IV", "CLASS IV"));
        classesBookAdapter = new HomeAdapter(classesBooks, Utils.ListTypes.CLASSES);
        classes_recyclerView.setAdapter(classesBookAdapter);
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
            return true;
        });
        popupMenu.show();
    }

    /**
     * Function to set the francophone section.
     */
    private void setToFrancoPhoneSection () {
        is_francophone_section = true;
        nursery_text.setText(class_group[1]);
        primary_text.setText(class_group[3]);
        secondary_text.setText(class_group[5]);
        class_text.setText(getString(R.string.classes));
        Utils.setToast(requireActivity(), "To implement francophone section.");
    }

    /**
     * Function to set the anglophone section.
     */
    private void setToAngloPhoneSection () {
        is_francophone_section = false;
        nursery_text.setText(class_group[0]);
        primary_text.setText(class_group[2]);
        secondary_text.setText(class_group[4]);
        class_text.setText(getString(R.string.classes));
        Utils.setToast(requireActivity(), "To implement anglophone section.");
    }

}
