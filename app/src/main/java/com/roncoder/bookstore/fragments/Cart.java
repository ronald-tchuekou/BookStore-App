package com.roncoder.bookstore.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.BuyCommend;
import com.roncoder.bookstore.activities.Chat;
import com.roncoder.bookstore.adapters.CmdAdapter;
import com.roncoder.bookstore.dbHelpers.BillHelper;
import com.roncoder.bookstore.dbHelpers.BookHelper;
import com.roncoder.bookstore.dbHelpers.CommendHelper;
import com.roncoder.bookstore.dbHelpers.MessageHelper;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.models.ContactMessage;
import com.roncoder.bookstore.models.Message;
import com.roncoder.bookstore.models.MessageCmd;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Cart extends Fragment {

    public static final String TAG = "Cart";
    @SuppressLint("StaticFieldLeak")
    private static Cart instance = null;
    private View root, empty;
    private List<Commend> commends;
    private CmdAdapter adapter;
    private ProgressBar progress;
    private static boolean is_me = false;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public Cart() { }

    public static Cart getInstance() {
        if (instance == null)
            instance = new Cart();
        return instance;
    }

    @Override
    public void onResume() {
        super.onResume();
        is_me = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        is_me = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cart, container, false); // Root.
        // init view.
        RecyclerView recyclerView = root.findViewById(R.id.recycler_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        progress = root.findViewById(R.id.progress);
        empty = root.findViewById(R.id.empty);
        // Listeners.
        root.findViewById(R.id.btn_buy_all).setOnClickListener(v -> {
            if (commends.size() == 0){
                Utils.setDialogMessage(requireActivity(), R.string.not_commend_to_buy);
                return;
            }
            byAllCommand();
        });
        // Init variables.
        commends = new ArrayList<>();
        adapter = new CmdAdapter(commends);
        recyclerView.setAdapter(adapter);

        adapter.setOnCartListener(new CmdAdapter.OnCartActionListener() {
            @Override
            public void onMessageListener(int position) {
                message(position);
            }

            @Override
            public void onRemoveListener(int position) {
                remove(position);
            }

            @Override
            public void onBuyListener(int position) {
                byCommand(position);
            }

            @Override
            public void onChangeQuantity(int position, int value) {
                setNewQuantity(value, position);
            }

            @Override
            public void onValidateListener(int position, Button button) {
                validate(position);
            }
        });

        // Set the list of the content list.
        setCommendList();

        return root;
    }

    /**
     * Validate the command.
     * @param position at this position.
     */
    private void validate(int position) {
        Commend c = commends.get(position);
        if (!c.isIs_billed()){
            setDialog(R.string.cmd_not_billed_to_validate);
            return;
        }
        Utils.setProgressDialog(requireActivity(), getString(R.string.validation));
        CommendHelper.validateCommend(c.getId())
                .addOnCompleteListener(com -> {
                    Utils.dismissDialog();
                    if (!com.isSuccessful()) {
                        if (com.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                        else
                            Utils.setToastMessage(requireContext(), getString(R.string.failled));
                        Log.e(TAG, "onValidateListener: ", com.getException());
                        return;
                    }
                    Utils.setToastMessage(requireContext(), getString(R.string.valid_successful));
                });
    }

    /**
     * Messaged.
     * @param position at this position.
     */
    private void message(int position) {
        Commend cmd = commends.get(position);

        Utils.setProgressDialog(requireActivity(), getString(R.string.wait_a_moment));
        // Check if the comment are messaging.
        if (cmd.getCm_id() == null || cmd.getCm_id().equals("")) { // If this message is not messaging.
            getAdminUsers(cmd);
        } else {
            MessageHelper.getCMCollectionRef().document(cmd.getId())
                    .get().addOnCompleteListener(com-> {
                Utils.dismissDialog();
                if (!com.isSuccessful()) {
                    Log.e(TAG, "message - get contact message: ", com.getException());
                    Utils.setDialogMessage(requireActivity(), R.string.error_has_provide);
                    return;
                }
                Intent messageIntent = new Intent(requireActivity(), Chat.class);
                messageIntent.putExtra(MessageChat.CONTACT_MESSAGE_EXTRA,
                        Objects.requireNonNull(com.getResult()).toObject(ContactMessage.class));
                startActivity(messageIntent);
            });
        }
    }

    private void getAdminUsers (Commend cmd) {
        UserHelper.getCollectionRef().whereEqualTo("is_admin", true)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Utils.dismissDialog();
                        Log.e(TAG, "manageDMessageCmd: ", error);
                        return;
                    }
                    if (value != null) {
                        List<User> users = value.toObjects(User.class);
                        if (users.isEmpty()) {
                            Utils.dismissDialog();
                            Utils.setDialogMessage(requireActivity(), R.string.impossible_to_make_message);
                        } else {
                            int random = (int) (Math.random()*(users.size() + 1));
                            User user1 = users.get(random);

                            // Save new message.
                           Message message = new Message();
                            message.setType(Utils.SMS_SEND);
                            message.setText("Bonjour à vous, information sur cette command.");
                            message.setDate(Calendar.getInstance().getTime());
                            message.setIs_read(false);

                            // The message of this.
                            saveMessage(message, cmd, user1);
                        }
                    }
        });
    }

    private void saveMessage(Message message, Commend cmd, User user1) {
        BookHelper.getBookById(cmd.getBook_id()).addOnSuccessListener(s -> {
            MessageCmd mcd = new MessageCmd();
            Book book = s.toObject(Book.class);
            assert book != null;
            mcd.setImage(book.getImage1_front());

            message.setMessage_cmd(new MessageCmd(book.getImage1_front(), book.getTitle(),
                    cmd.getQuantity(), cmd.getTotal_prise()));
            // add message.
            MessageHelper.getCollectionRef().add(message).addOnCompleteListener(com -> {
                if (!com.isSuccessful()) {
                    Log.e(TAG, "message - add message : ", com.getException());
                    Utils.setDialogMessage(requireActivity(), R.string.error_has_provide);
                    return;
                }
                // Update id.
                String id = Objects.requireNonNull(com.getResult()).getId();
                message.setId(id);
                MessageHelper.getCollectionRef().document(id).update("id", id)
                        .addOnSuccessListener(suc -> {
                            Utils.dismissDialog();
                            // Add contactMessage.
                            ContactMessage cm = new ContactMessage();
                            cm.setSender(user.getUid());
                            cm.setReceiver(user1.getId());
                            cm.setLast_message(message);
                            cm.setDate(Calendar.getInstance().getTime());
                            cm.setNot_read_count(0);
                            MessageHelper.getCMCollectionRef().add(cm).addOnSuccessListener(success -> {
                                String cm_id = success.getId();
                               MessageHelper.getCMCollectionRef().document(cm_id).get().addOnSuccessListener(v -> {
                                   cm.setId(cm_id);
                                   CommendHelper.getCollectionRef().document(cmd.getId()).update("cm_id", cm.getId());
                                   Intent messageIntent = new Intent(requireActivity(), Chat.class);
                                   messageIntent.putExtra(MessageChat.CONTACT_MESSAGE_EXTRA, cm);
                                   startActivity(messageIntent);
                               });
                            });
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "onFailure: ", e);
                            Utils.dismissDialog();
                        });
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: ", e);
            Utils.dismissDialog();
        });
    }

    /**
     * By command
     * @param position at this position.
     */
    private void byCommand(int position) {
        Intent cmdIntent = new Intent(requireActivity(), BuyCommend.class);
        Bill bill = new Bill();
        Commend cmd = commends.get(position);

        if (cmd.isIs_billed()) {
            Utils.setProgressDialog(requireActivity(), getString(R.string.wait_a_moment));
            BillHelper.getBillByRef(cmd.getBill_ref()).addOnCompleteListener(command -> {
                Utils.dismissDialog();
                if (!command.isSuccessful()) {
                    Exception e = command.getException();
                    if (e instanceof FirebaseNetworkException)
                        Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                    else
                        Utils.setToastMessage(requireContext(), getString(R.string.failled));
                    Log.e(TAG, "onBuyListener: ", e);
                    return;
                }
                Bill b = Objects.requireNonNull(command.getResult()).toObject(Bill.class);
                cmdIntent.putExtra(Utils.BILL_EXTRA, b);
                startActivity(cmdIntent);
            });
        } else {
            List<String> cmd_id = new ArrayList<>();
            cmd_id.add(cmd.getId());
            bill.setCommend_ids(cmd_id);
            bill.setTotal_prise(cmd.getTotal_prise());
            bill.setUser_id(user.getUid());
        }

        cmdIntent.putExtra(Utils.BILL_EXTRA, bill);
        startActivity(cmdIntent);
    }

    /**
     * Removed commend.
     * @param position at this position.
     */
    private void remove(int position) {
        Commend cmd = commends.get(position);
        if (cmd.isIs_billed()) {
            Utils.setDialogMessage(requireActivity(), R.string.cmd_has_been_billed);
            return;
        }
        new MaterialAlertDialogBuilder(requireActivity(),
                R.style.Theme_MaterialComponents_Light_BottomSheetDialog)
                .setTitle(R.string.comfim)
                .setMessage(R.string.confir_removed_to_cart)
                .setPositiveButton(R.string.yes, (dialog1, which) -> deleteCmd(position))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    /**
     * Buying all the command into the command list.
     */
    private void byAllCommand() {
        List<String> notBillCmd_id = new ArrayList<>();
        float totalPrise = 0;
        for (int i=0; i < commends.size(); i++ ) {
            Commend c = commends.get(i);
            notBillCmd_id.add(c.getId());
            totalPrise += c.getTotal_prise();
        }

        if (notBillCmd_id.size() == 0)
            setDialog(R.string.all_cmd_has_dit_bill);
        else
        {
            Intent cmdIntent = new Intent(requireActivity(), BuyCommend.class);
            Bill bill = new Bill();
            bill.setCommend_ids(notBillCmd_id);
            bill.setTotal_prise(totalPrise);
            bill.setUser_id(user.getUid());
            cmdIntent.putExtra(Utils.BILL_EXTRA, bill);
            startActivity(cmdIntent);
        }
    }

    /**
     * Function that update the total prise.
     */
    private void updateTotalPrise ()  {
        float totalPrise = 0;
        for (Commend c : commends)
            totalPrise += c.getTotal_prise();
        ((TextView)root.findViewById(R.id.total_cmd_prise)).setText(Utils.formatPrise(totalPrise));
        // Send count commend broadcast.
        if (is_me)
            Utils.setCmdCountBroadcast(commends.size(), true, requireActivity());
    }

    /**
     * Function that delete a commend.
     * @param position Position in the commends list.
     */
    private void deleteCmd(int position) {
        Commend cmd = commends.get(position);
        Utils.setProgressDialog(requireActivity(), getString(R.string.deleting));
        CommendHelper.deleteCommend(cmd.getId())
                .addOnCompleteListener(com -> {
                    Utils.dismissDialog();
                    if (!com.isSuccessful()) {
                        if (com.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                        else
                            Utils.setToastMessage(requireActivity(), getString(R.string.error_has_provide));
                        Log.e(TAG, "deleteCmd: ", com.getException());
                        return;
                    }
                    Utils.setToastMessage(requireContext(), getString(R.string.deleted_succesful));
                });
    }

    /**
     * Function that change commend quantity.
     */
    private void setNewQuantity(int value, int position) {
        Commend cmd = commends.get(position);
        if (cmd.isIs_billed()) {
            Utils.setDialogMessage(requireActivity(), R.string.cmd_has_been_billed);
            return;
        }

        Utils.setProgressDialog(requireActivity(), getString(R.string.wait_a_moment));
        BookHelper.getBookById(cmd.getBook_id()).addOnCompleteListener(com -> {
            if (!com.isSuccessful()) {
                Utils.dismissDialog();
                if (com.getException() instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                else
                    Utils.setToastMessage(requireActivity(), getString(R.string.error_has_provide));
                Log.e(TAG, "setNewQuantity: ", com.getException());
                return;
            }

            Book book = Objects.requireNonNull(com.getResult()).toObject(Book.class);

            assert book != null;
            int newQuantity = cmd.getQuantity() + value;
            if (newQuantity >= book.getStock_quantity())
                newQuantity = book.getStock_quantity();
            else
                if (newQuantity <= 1)
                    newQuantity = 1;
            cmd.setQuantity(newQuantity);
            cmd.setTotal_prise(newQuantity * book.getUnit_prise());
            updateCommend(cmd, position);
        });
    }

    private void updateCommend(Commend cmd, int position) {
        CommendHelper.updateCmdQuantity(cmd.getId(), cmd.getQuantity(), cmd.getTotal_prise())
                .addOnCompleteListener(com -> {
                    Utils.dismissDialog();
                    if (!com.isSuccessful()) {
                        if (com.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                        else
                            Utils.setToastMessage(requireActivity(), getString(R.string.error_has_provide));
                        Log.e(TAG, "update Commend: ", com.getException());
                        return;
                    }

                    // Update the list of commends.
                    commends.remove(position);
                    commends.add(position, cmd);
                    adapter.notifyItemChanged(position);
                    updateTotalPrise();
                });
    }

    private void setCommendList () {
        progress.setVisibility(View.VISIBLE);
        CommendHelper.getAllClientCmd(user.getUid()).addSnapshotListener((value, error) -> {
            progress.setVisibility(View.GONE);
            if (error != null) {
                progress.setVisibility(View.GONE);
                Utils.setToastMessage(requireActivity(), getString(R.string.error_has_provide));
                Log.e(TAG, "setCommendList: ", error);
                return;
            }
            if (value != null) {
                commends.clear();
                commends.addAll(value.toObjects(Commend.class));
                Log.i(TAG, "setCommendList: Commends => " + commends);
                if (commends.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    ((TextView)root.findViewById(R.id.text_empty)).setText(R.string.empty_commend);
                } else
                    empty.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                updateTotalPrise();
            }
        });
    }

    private void setDialog (@StringRes int res) {
       Utils.setDialogMessage( requireActivity(), res);
    }
}
