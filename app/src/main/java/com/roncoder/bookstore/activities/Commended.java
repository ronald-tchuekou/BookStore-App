package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.BillHelper;
import com.roncoder.bookstore.dbHelpers.CommendHelper;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Commended extends AppCompatActivity {
    private static final String TAG = "Commended";
    Book book;
    TextView book_title, book_author, book_prise, book_edition, total_prise, book_quantity;
    ImageView front_image;
    ImageButton remove_quantity, add_quantity;
    Button finish;
    FirebaseAuth auth;
    private int quantity = 1;
    double total_unit_prise = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commended);
        // Auth
        auth = FirebaseAuth.getInstance();
        // Get book.
        book = getIntent().getParcelableExtra(Utils.EXTRA_BOOK);
        initViews();
        setViewValue();
        finish.setOnClickListener(v -> setTheCommend());
        add_quantity.setOnClickListener(v -> addQuantity(1));
        remove_quantity.setOnClickListener(v -> addQuantity(-1));
    }
    private void updateTotalPrise () {
        book_quantity.setText(String.valueOf(quantity));
        total_unit_prise = quantity * book.getUnit_prise();
        total_prise.setText(Utils.formatPrise(total_unit_prise));
    }
    private void setTheCommend() {
        int quantity = Integer.parseInt(book_quantity.getText().toString());
        Commend commend = new Commend(book.getId(), FirebaseAuth.getInstance().getUid(), quantity,
                Calendar.getInstance().getTime(), book.getUnit_prise() * quantity);
        Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
        Task<DocumentReference> task = Utils.addToCart(this, commend);
        if (task != null)
            task.addOnCompleteListener(com -> {
                if (!com.isSuccessful()) {
                    Utils.dismissDialog();
                    if (com.getException() instanceof FirebaseNetworkException)
                        Utils.setDialogMessage(this, R.string.network_not_allowed);
                    Log.e(TAG, "Error where add commend to cart", com.getException());
                    return;
                }
                String id = Objects.requireNonNull(com.getResult()).getId();
                commend.setId(id);
                CommendHelper.getCollectionRef().document(id).update("id", id).addOnCompleteListener(command -> {
                    if (!command.isSuccessful()){
                        Utils.setToastMessage(this, getString(R.string.error_has_provide));
                        return;
                    }
                    Utils.dismissDialog();
                    Utils.setCmdCountBroadcast(1, false, this);
                    // Fish to commend in BuyCommend activity.
                    buyCommend(commend);
                });
            });
    }

    /**
     * Finish to buy this commend.
     * @param cmd Commend
     */
    private void buyCommend(Commend cmd) {
        Intent cmdIntent = new Intent(this, BuyCommend.class);
        Bill bill = new Bill();

        if (cmd.isIs_billed()) { // If this commend are billing.
            Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
            BillHelper.getBillByRef(cmd.getBill_ref()).addOnCompleteListener(command -> {
                Utils.dismissDialog();
                if (!command.isSuccessful()) {
                    Exception e = command.getException();
                    if (e instanceof FirebaseNetworkException)
                        Utils.setDialogMessage(this, R.string.network_not_allowed);
                    else
                        Utils.setToastMessage(this, getString(R.string.failled));
                    Log.e(TAG, "onBuyListener: ", e);
                    return;
                }
                Bill b = Objects.requireNonNull(command.getResult()).toObject(Bill.class);
                cmdIntent.putExtra(Utils.BILL_EXTRA, b);
                startActivity(cmdIntent);
                finish();
            });
        } else { // If this commend are not billing.
            List<String> cmd_id = new ArrayList<>();
            cmd_id.add(cmd.getId());
            bill.setCommend_ids(cmd_id);
            bill.setTotal_prise(cmd.getTotal_prise());
            bill.setUser_id(auth.getUid());
            cmdIntent.putExtra(Utils.BILL_EXTRA, bill);
            startActivity(cmdIntent);
            finish();
        }
    }

    private void addQuantity(int number) {
        this.quantity += number;
        if (this.quantity < 1)
            this.quantity = 1;
        else if (this.quantity > book.getStock_quantity())
            this.quantity = book.getStock_quantity();

        updateTotalPrise();
    }

    private void setViewValue() {
        String edition = "Edition : " + book.getEditor();
        book_title.setText(book.getTitle());
        book_author.setText(book.getAuthor());
        book_edition.setText(edition);
        book_prise.setText(Utils.formatPrise(book.getUnit_prise()));
        updateTotalPrise();
        Glide.with(this)
                .load(book.getImage1_front())
                .placeholder(R.drawable.bg_image)
                .error(R.drawable.bg_image)
                .into(front_image);
    }

    private void initViews() {
        book_title = findViewById(R.id.book_title);
        book_author = findViewById(R.id.book_author);
        book_prise = findViewById(R.id.book_prise);
        book_edition = findViewById(R.id.book_edition);
        total_prise = findViewById(R.id.total_prise);
        front_image = findViewById(R.id.front_image);
        remove_quantity = findViewById(R.id.remove_quantity);
        add_quantity = findViewById(R.id.add_quantity);
        finish = findViewById(R.id.finish);
        book_quantity = findViewById(R.id.book_quantity);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    public void backListener(View view) {
        onBackPressed();
    }
}