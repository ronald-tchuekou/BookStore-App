package com.roncoder.bookstore.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.BookDetails;
import com.roncoder.bookstore.activities.BuyCommend;
import com.roncoder.bookstore.activities.Commended;
import com.roncoder.bookstore.activities.MainActivity;
import com.roncoder.bookstore.api.Result;
import com.roncoder.bookstore.dbHelpers.CommendHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.models.User;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Interceptor;
import retrofit2.Call;

import static androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation;

public class Utils {

    // Cycle type
    public static final String PRIMARY_F = "Primaire";
    public static final String PRIMARY_A = "Primary";
    public static final String SECONDARY_F = "Secondaire";
    public static final String SECONDARY_A = "Secondary";
    public static final String NURSERY_F = "Maternelle";
    public static final String NURSERY_A = "Nursery";
    public static final String CLASS_F = "Francophone";
    public static final String CLASS_A = "Anglophone";

    // Current user.
    private static User user;

    // Payment types
    public static final String PAYMENT_AT_SHIPPING = "A la livraison";
    public static final String PAYMENT_BY_OM = "Orange Money";
    public static final String PAYMENT_BY_MM = "Mobile Money";

    // Shipping types
    public static final String SHIPPING_INSTANT = "Instantaneous";
    public static final String SHIPPING_STANDARD = "Standard";
    public static final String SHIPPING_EXPRESS = "Express";

    // Shipping costs.
    public static final float SHIPPING_COST_INSTANT = 500;
    public static final float SHIPPING_COST_STANDARD = 500;
    public static final float SHIPPING_COST_EXPRESS = 2000;

    // Bill state
    public static final String BILL_DELIVER = "Delivered";
    public static final String BILL_IN_COURSE = "In Course";
    public static final String BILL_OBSOLETE = "Obsolete";

    /**
     * Id of the current user.
     */
    public static int user_id;

    public static final int FRAG_HOME = 0;
    public static final int FRAG_SEARCH = 1;
    public static final int FRAG_KILT = 2;
    public static final int FRAG_ACCOUNT = 3;
    public static final int FRAG_MESSAGE = 4;
    public static final int COMMENDED_REQUEST_CODE = 12;
    public static final String EXTRA_COMMEND = "Commend";

    private static ProgressDialog progressDialog;

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
     * Function that return date instance of string.
     * @param date_str String date.
     * @return Date instance.
     */
    public static Date getDateOf (String date_str) {
        return new Date(Long.parseLong(date_str));
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
     * Function that return the current user.
     * @return User.
     */
    public static User getCurrentUser() {
        if (Utils.user == null)
            return new User();
        return Utils.user;
    }

    /**
     * Function that set the current user.
     * @param user User.
     */
    public static void setCurrentUser (User user) {
        Utils.user = user;
    }

    /**
     * Function that send broadcast to update the cart badge.
     * @param activity Activity.
     * @param count Commend count.
     * @param replace State of action.
     */
    public static void sendCmdCountBroadCast(Activity activity, int count, boolean replace) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(MainActivity.EXTRA_CMD_COUNT, count);
        broadcastIntent.putExtra(MainActivity.EXTRA_CMD_REPLACE, replace);
        activity.sendBroadcast(broadcastIntent, MainActivity.CMD_COUNT_ACTION);
    }

    public static void setProgressDialog(Activity activity, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void dismissDialog() {
        progressDialog.dismiss();
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
     * Function that set a dialog message.
     * @param activity Activity.
     * @param message message.
     */
    public static void setDialogMessage (Activity activity, @StringRes int message) {
        new MaterialAlertDialogBuilder(activity, R.style.Theme_MaterialComponents_Light_BottomSheetDialog)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
    /**
     * Function that set a dialog message.
     * @param activity Activity.
     * @param message message.
     */
    public static void setDialogMessage (Activity activity,String message) {
        new MaterialAlertDialogBuilder(activity, R.style.Theme_MaterialComponents_Light_BottomSheetDialog)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
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
     * @param commend The commend.
     */
    public static Call<Result> addToCart (Activity activity, Commend commend) {
        Utils.sendCmdCountBroadCast(activity, 1, false);
        return CommendHelper.addCommend (commend);
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
    /**
     * Function to set the toast message.
     * @param context Application context.
     * @param message Message.
     */
    public static void setToastMessage(Context context, String message) {
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, -100);
        @SuppressLint("InflateParams") View toastLayout = LayoutInflater.from(context)
                .inflate(R.layout.toast_layout_custom, null, false);
        toast.setView(toastLayout);
        ((TextView)toast.getView().findViewById(R.id.toast_message)).setText(message);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
