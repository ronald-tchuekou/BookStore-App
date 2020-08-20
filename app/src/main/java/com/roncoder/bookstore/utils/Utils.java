package com.roncoder.bookstore.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;

import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.BookDetails;
import com.roncoder.bookstore.activities.BuyCommend;
import com.roncoder.bookstore.activities.Commended;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation;

public class Utils {

    /**
     * To managed the commend status.
     */
    public static class FactureState {
        public static final String INSTANT = "Instant";
        public static final String STANDARD = "Standard";
        public static final String EXPRESS = "Express";
        public static final String SHIPPING = "Shipping";
        public static final String OBSOLETED = "Obsolete";
    }
    public static final int FRAG_HOME = 0;
    public static final int FRAG_SEARCH = 1;
    public static final int FRAG_KILT = 2;
    public static final int FRAG_ACCOUNT = 3;
    public static final int FRAG_MESSAGE = 4;
    public static final int COMMENDED_REQUEST_CODE = 12;
    public static final String EXTRA_COMMEND = "Commend";

    /**
     * Function to format the date.
     * @param date Date.
     */
    public static String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        String dateMessage =  DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        if (currentDate.equals(dateMessage))
            return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
        else
            return dateMessage;
    }
    /**
     * Function to format the date.
     * @param date Date.
     */
    public static String getDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    }
    /**
     * Function to format the date time.
     * @param date Date.
     */
    public static String getTime(Date date) {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    /**
     * Function to get the current date.
     */
    public static Date now () { return Calendar.getInstance().getTime(); }
    /**
     * Function to buy a commend.
     * @param activity Activity of application.
     * @param commends The commend.
     */
    public static void buyCommend(Activity activity, List<Commend> commends) {
        Intent buyCommend = new Intent(activity, BuyCommend.class);
        buyCommend.putExtra(EXTRA_COMMEND, (ArrayList<? extends Parcelable>) commends);
        activity.startActivity(buyCommend);
    }

    /**
     * Function to set the toast message.
     * @param activity Activity app.
     * @param message Message.
     */
    public static void setToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Type of list indicator.
     */
    public enum ListTypes {
        PRIMARY_CYCLE, FIRST_CYCLE, SECOND_CYCLE, CLASSES
    }

    public static final String EXTRA_FRAG_TYPE = "frag_type";
    public static final String EXTRA_BOOK = "book_info";

    /**
     * Function that set to the book details activity.
     * @param activity Activity.
     * @param book Book.
     * @param view View.
     * @param tag Tag animation.
     */
    public static void bookDetail(Activity activity, Book book, View view, String tag) {
        ActivityOptionsCompat activityOptions = makeSceneTransitionAnimation(activity, view, tag);
        Intent cycleIntent = new Intent(activity, BookDetails.class);
        cycleIntent.putExtra(EXTRA_BOOK, book);
        activity.startActivity(cycleIntent, activityOptions.toBundle());
    }

    /**
     * Function to add book to the kilt.
     * @param activity Activity.
     * @param commend The commend.
     */
    public static void addToCart (Activity activity, Commend commend) {
        // TODO
        Toast.makeText(activity, "add this article to kilt. " + commend, Toast.LENGTH_SHORT).show();
    }

    /**
     * Function to commended the book.
     * @param activity Application activity.
     */
    public static void commendActivity(Activity activity, Book book) {
        Intent commendIntent = new Intent(activity, Commended.class);
        commendIntent.putExtra(Utils.EXTRA_BOOK, book);
        activity.startActivityForResult(commendIntent, Utils.COMMENDED_REQUEST_CODE);
        activity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    /**
     * Function to format the prise and get this with comas.
     * @param prise Prise.
     */
    public static String formatPrise (double prise) {
        String prise_str = "" + (int)prise;
        String start;
        String end;
        StringBuilder value = new StringBuilder();
        do {
            if (prise_str.length() <= 3){
                value = new StringBuilder(prise_str);
                break;
            }
            start = prise_str.substring(0, prise_str.length() - 3);
            end = prise_str.substring(prise_str.length() - 3);
            if (start.length() <= 3)
                value.insert(0, start + "," + end);
            else{
                value.insert(0, "," + end);
                prise_str = start;
            }
        }while (start.length() > 3);

        return value + " FCFA";
    }
}
