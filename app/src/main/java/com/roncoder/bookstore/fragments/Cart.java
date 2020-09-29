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
import com.roncoder.bookstore.models.Conversation;
import com.roncoder.bookstore.models.Message;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.roncoder.bookstore.fragments.MessageChat.CHAT_CON_ID_EXTRA;
import static com.roncoder.bookstore.fragments.MessageChat.CHAT_DESTINATION_EXTRA;

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
        if (cmd.getCon_id() == null || cmd.getCon_id().equals("")) { // If this message is not messaging.
            getAdminUsers(cmd.getId());
        }
        else { // If this commend are messaging.
            MessageHelper.getConCollectionRef().document(cmd.getCon_id()).get().addOnCompleteListener(com-> {
                Utils.dismissDialog();
                if (!com.isSuccessful()) {
                    Log.e(TAG, "message - get contact message: ", com.getException());
                    if (com.getException() instanceof FirebaseNetworkException)
                        Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                    else
                        Utils.setDialogMessage(requireActivity(), R.string.error_has_provide);
                    return;
                }
                if (com.getResult() != null) {
                    setToMessage(Objects.requireNonNull(com.getResult().toObject(Conversation.class)));
                }
            });
        }
    }

    /**
     * Open activity of messages.
     * @param conversation Contact message.
     */
    private void setToMessage(Conversation conversation) {
        List<String> members = conversation.getMembers();
        String receiver = members.get(0).equals(user.getUid()) ? members.get(1) : members.get(0);
        Intent chatIntent = new Intent(requireContext(), Chat.class);
        chatIntent.putExtra(CHAT_DESTINATION_EXTRA, receiver);
        chatIntent.putExtra(CHAT_CON_ID_EXTRA, conversation.getId());
        startActivity(chatIntent);
    }

    /**
     * Function to get the receiver message.
     */
    private void getAdminUsers (String cmd_id) {
        UserHelper.getCollectionRef().whereEqualTo("is_admin", true)
                .addSnapshotListener((value, error) -> {

                    // Where error are provided.
                    if (error != null) {
                        Utils.dismissDialog();
                        Log.e(TAG, "manageDMessageCmd: ", error);
                        return;
                    }

                    // Where values.
                    if (value != null) {
                        List<User> users = value.toObjects(User.class);
                        if (users.isEmpty()) {
                            Utils.dismissDialog();
                            Utils.setDialogMessage(requireActivity(), R.string.impossible_to_make_message);
                            return;
                        }

                        // Random an user. (receiver user);
                        User user1; int test = 0;
                        do{
                            int random = (int) (Math.random()*(users.size()));
                            user1 = users.get(random);
                            test ++;
                        }while(user1.getId().equals(user.getUid()) && test <= 10);

                        if (test > 10 && user1 == null) { Utils.setDialogMessage(requireActivity(), R.string.impossible_to_make_message); return; }

                        // Save new contact message.
                        List<String> members = new ArrayList<>();
                        members.add(user.getUid());
                        members.add(user1.getId());
                        Conversation conversation = new Conversation(user.getUid(), members, Calendar.getInstance().getTime());
                        MessageHelper.getConCollectionRef().add(conversation).addOnSuccessListener(success -> {
                            String conversation_id = success.getId();

                            // UPDATE CONVERSATION ID.
                            MessageHelper.getConCollectionRef().document(conversation_id).update("id", conversation_id).addOnSuccessListener(v -> {
                                conversation.setId(conversation_id);
                                // UPDATE THE PARENT COMMEND.
                                CommendHelper.getCollectionRef().document(cmd_id).update("con_id", conversation.getId())
                                        .addOnCompleteListener(command -> {
                                            if (!command.isSuccessful()) {
                                                Utils.dismissDialog();
                                                Log.e(TAG, "Error where update the commend contact id : ", command.getException());
                                                if (command.getException() instanceof FirebaseNetworkException)
                                                    Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                                                else
                                                    Utils.setDialogMessage(requireActivity(), R.string.error_has_provide);
                                                // Delete the last added contact message if saver as failed.
                                                MessageHelper.getConCollectionRef().document(conversation_id).delete();
                                                return;
                                            }
                                            // Save an message.
                                           sendMessage(cmd_id, conversation);
                                        });
                            }).addOnFailureListener(fail -> {
                                Utils.dismissDialog();
                                // Delete the last added contact message if saver as failed.
                                MessageHelper.getConCollectionRef().document(conversation_id).delete();
                                if (fail instanceof FirebaseNetworkException)
                                    Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                                else
                                    Utils.setDialogMessage(requireActivity(), R.string.error_has_provide);
                            });
                        }).addOnFailureListener(fail -> {
                            Utils.dismissDialog();
                            if (fail instanceof FirebaseNetworkException)
                                Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                            else
                                Utils.setDialogMessage(requireActivity(), R.string.error_has_provide);
                        });
                    }
        });
    }

    /**
     * Function to send the message.
     * @param cmd_id Commend id.
     * @param conversation Conversation id.
     */
    private void sendMessage(String cmd_id, Conversation conversation) {
        Message message = new Message(conversation.getId(), user.getUid(), "Message sur la commend *"+cmd_id+"*",
                Calendar.getInstance().getTime());
        MessageHelper.sendMessage(message).addOnCompleteListener(command -> {
            if (!command.isSuccessful()) {
                Utils.dismissDialog();
                Log.e(TAG, "Error where update the commend contact id : ", command.getException());
                if (command.getException() instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                else
                    Utils.setDialogMessage(requireActivity(), R.string.error_has_provide);
                return;
            }
            if (command.getResult() != null) {
                String message_id = command.getResult().getId();
                message.setId(message_id);
                // UPDATE THE MESSAGE ID.
                MessageHelper.getCollectionRef().document(message_id).update("id", message_id)
                        .addOnCompleteListener(com -> {
                            if (!command.isSuccessful()) {
                                Utils.dismissDialog();
                                Log.e(TAG, "Error where update the commend contact id : ", command.getException());
                                if (command.getException() instanceof FirebaseNetworkException)
                                    Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                                else
                                    Utils.setDialogMessage(requireActivity(), R.string.error_has_provide);
                                return;
                            }
                            // UPDATE LAST MESSAGE OF CONVERSATION.
                            MessageHelper.getConCollectionRef().document(conversation.getId()).update("last_message", message)
                                    .addOnSuccessListener(suc -> {
                                        Utils.dismissDialog();
                                        setToMessage(conversation);
                                    })
                                    .addOnFailureListener(fail -> {
                                        Utils.dismissDialog();
                                        Log.e(TAG, "Error where update the commend contact id : ", fail);
                                        if (fail instanceof FirebaseNetworkException)
                                            Utils.setDialogMessage(requireActivity(), R.string.network_not_allowed);
                                        else
                                            Utils.setDialogMessage(requireActivity(), R.string.error_has_provide);
                                    });
                        });
            }
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

        if (cmd.isIs_billed()) { // If this commend are billing.
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
        } else { // If this commend are not billing.
            List<String> cmd_id = new ArrayList<>();
            cmd_id.add(cmd.getId());
            bill.setCommend_ids(cmd_id);
            bill.setTotal_prise(cmd.getTotal_prise());
            bill.setUser_id(user.getUid());
            cmdIntent.putExtra(Utils.BILL_EXTRA, bill);
            startActivity(cmdIntent);
        }
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
