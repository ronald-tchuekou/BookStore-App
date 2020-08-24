package com.roncoder.bookstore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.activities.Chat;
import com.roncoder.bookstore.adapters.CmdAdapter;
import com.roncoder.bookstore.api.Result;
import com.roncoder.bookstore.dbHelpers.CommendHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roncoder.bookstore.utils.Utils.EXTRA_COMMEND;

public class Cart extends Fragment {

    public static final String TAG = "Cart";
    private View root;
    private List<Commend> commends;
    private CmdAdapter adapter;
    private Gson gson;

    public Cart() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.setToastMessage(requireContext(), "The cart fragment is resumed.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cart, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_cart);
        root.findViewById(R.id.btn_buy_all).setOnClickListener(v -> {
            List<Commend> notBillCmd = new ArrayList<>();
            for (Commend c : commends)
                if (!c.isIs_billed())
                    notBillCmd.add(c);

            if (notBillCmd.isEmpty())
                Utils.setDialogMessage(requireActivity(), R.string.all_cmd_has_dit_bill);
            else
                Utils.buyCommend(requireActivity(), notBillCmd);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        commends = new ArrayList<>();
        adapter = new CmdAdapter(commends);
        recyclerView.setAdapter(adapter);
        setCommendList();
        adapter.setOnCartListener(new CmdAdapter.OnCartActionListener() {
            @Override
            public void onMessageListener(int position) {
                List<Commend> commendList = new ArrayList<>();
                commendList.add(commends.get(position));
                setToMessage(commendList);
            }

            @Override
            public void onRemoveListener(int position) {
                new MaterialAlertDialogBuilder(requireActivity(),
                        R.style.Theme_MaterialComponents_Light_BottomSheetDialog)
                        .setTitle(R.string.comfim)
                        .setMessage(R.string.confir_removed_to_cart)
                        .setPositiveButton(R.string.yes, (dialog1, which) -> deleteCmd(position))
                        .setNegativeButton(R.string.no, null)
                        .show();
            }

            @Override
            public void onBuyListener(int position) {
                List<Commend> buyCommends = new ArrayList<>();
                buyCommends.add(commends.get(position));
                Utils.buyCommend(requireActivity(), buyCommends);
            }

            @Override
            public void onChangeQuantity(int position, int value) {
                setNewQuantity(commends.get(position), value, position);
            }

            @Override
            public void onMoreInfoListener(int position, View view) {
                Utils.bookDetail(requireActivity(), commends.get(position).getBook(), view,
                        Home.TRANSITION_IMAGE_NAME);
            }

            @Override
            public void onValidateListener(int position, Button button) {
                Commend c = commends.get(position);
                if (!c.isIs_billed()){
                    Utils.setDialogMessage(requireActivity(), R.string.cmd_not_billed_to_validate);
                    return;
                }
                Utils.setProgressDialog(requireActivity(), getString(R.string.validation));
                CommendHelper.validateCommend(c.getId()).enqueue(new Callback<Result>() {
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
                            button.setEnabled(false);
                            Utils.setToastMessage(requireContext(), getString(R.string.valid_successful));
                        }
                        Utils.dismissDialog();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Utils.setDialogMessage(requireActivity(), t.getMessage());
                        Log.e(TAG, "onFailure: " + call, t);
                        Utils.dismissDialog();
                    }
                });
            }
        });
        return root;
    }

    /**
     * Function that update the total prise.
     */
    private void updateTotalPrise ()  {
        float totalPrise = 0;
        for (Commend c : commends)
            totalPrise += c.getTotal_prise();

        Utils.sendCmdCountBroadCast(requireActivity(), commends.size(), true);

        ((TextView)root.findViewById(R.id.total_cmd_prise)).setText(Utils.formatPrise(totalPrise));
    }

    /**
     * Function that delete a commend.
     * @param position Position in the commends list.
     */
    private void deleteCmd(int position) {
        Utils.setProgressDialog(requireActivity(), getString(R.string.deleting));
        CommendHelper.deleteCommend(commends.get(position)).enqueue(new Callback<Result>() {
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

                    // Update the list of commends.
                    commends.remove(position);
                    adapter.notifyItemRemoved(position);
                    updateTotalPrise();
                }
                Utils.dismissDialog();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.setDialogMessage(requireActivity(), t.getMessage());
                Log.e(TAG, "onFailure: " + call, t);
                Utils.dismissDialog();
            }
        });
    }

    /**
     * Function that change commend quantity.
     * @param commend Commend.
     */
    private void setNewQuantity(Commend commend, int value, int position) {
        Book book = commend.getBook();
        int newQuantity = commend.getQuantity() + value;
        if (newQuantity >= book.getStock_quantity())
            newQuantity = book.getStock_quantity();
        else
            if (newQuantity <= 1)
                newQuantity = 1;
        int finalNewQuantity = newQuantity;
        Utils.setProgressDialog(requireActivity(), getString(R.string.wait_a_moment));
        CommendHelper.updateCmdQuantity(commend.getId(), newQuantity, book.getUnit_prise())
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                        Result result = response.body();
                        if (result.getError()) {
                            Utils.setDialogMessage(requireActivity(), result.getMessage());
                            Log.e(TAG, "Error process : " + result.getMessage(), null);
                        }
                        else if (result.getSuccess()){
                            String value = result.getValue();
                            commend.setQuantity(finalNewQuantity);
                            commend.setTotal_prise();

                            // Update the list of commends.
                            commends.remove(position);
                            commends.add(position, commend);
                            adapter.notifyItemChanged(position);
                            updateTotalPrise();
                            Log.i(TAG, "success: " + value, null);
                        }
                        Utils.dismissDialog();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Utils.setDialogMessage(requireActivity(), t.getMessage());
                        Log.e(TAG, "onFailure: " + call, t);
                        Utils.dismissDialog();
                    }
                });
    }

    private void setToMessage(List<Commend> commends) {
        if (!allCmdIsBilled(commends))
            Utils.setDialogMessage(requireActivity(), R.string.this_has_no_billed_cmd);
        else{
            Intent chatIntent = new Intent(requireActivity(), Chat.class);
            chatIntent.putExtra(EXTRA_COMMEND, (ArrayList<? extends Parcelable>) commends);
        /* TO GET THIS ELEMENT, we make this method.
           ArrayList<Parcelable> commend = getIntent().getParcelableArrayListExtra(EXTRA_COMMEND);
         */
            startActivity(chatIntent);
        }
    }

    /**
     * Function that checked if all list commends is billed.
     * @param commends List of commends.
     * @return value.
     */
    private boolean allCmdIsBilled(List<Commend> commends) {
        for (Commend c : commends)
            if (!c.isIs_billed()) return false;
        return true;
    }

    private void setCommendList () {
        CommendHelper.getAllClientCmd(Utils.user_id).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Result result = response.body();

                if (result == null)
                    return;

                if (result.getError()) { // Where an error has provided.
                    Utils.setDialogMessage(requireActivity(), result.getMessage());
                    Log.e(TAG, "Error process : " + result.getMessage(), null);
                }
                else if (result.getSuccess()) { // Where event is successful.
                    String value = result.getValue();
                    Log.i(TAG, "Success process : " + value);

                    JsonArray commendArray = (JsonArray) JsonParser.parseString(value);
                    for (JsonElement element : commendArray) {
                        commends.add(gson.fromJson(element, Commend.class));
                    }
                    adapter.notifyDataSetChanged();
                    updateTotalPrise();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.setDialogMessage(requireActivity(), t.getMessage());
                Log.e(TAG, "get commend list failed : ", t);
            }
        });
    }
}
