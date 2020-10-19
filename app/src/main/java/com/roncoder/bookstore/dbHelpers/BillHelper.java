package com.roncoder.bookstore.dbHelpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.roncoder.bookstore.models.Bill;
import com.roncoder.bookstore.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class BillHelper {

    public static final String COLLECTION = "Bills";

    public static CollectionReference getCollectionRef () {
        return FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    public static Task<DocumentSnapshot> getBillByRef(String bill_ref) {
        return getCollectionRef().document(bill_ref).get();
    }

    public static Task<DocumentReference> saveBill (Bill bill) {
        return getCollectionRef().add(bill);
    }

    public static List<Task<Void>> billedCommend (List<String> commend_ids, String bill_ref) {
        List<Task<Void>> taskList = new ArrayList<>();
        for (String commend_id : commend_ids)
            taskList.add(CommendHelper.billedCommend(commend_id, bill_ref));
        return taskList;
    }

    public static Query getBillByShippingType(String shippingType) {
        return getCollectionRef()
                .whereEqualTo("shipping_type", shippingType)
                .whereEqualTo("state", Utils.BILL_IN_COURSE)
                .orderBy("shipping_date", Query.Direction.DESCENDING);
    }

    public static Query getBillByState (String state) {
        return getCollectionRef()
                .whereEqualTo("state", state)
                .orderBy("shipping_date");
    }

    public static Task<Void> validateBill(String ref) {
        return getCollectionRef().document(ref).update("state", Utils.BILL_DELIVER);
    }
}
