package com.roncoder.bookstore.dbHelpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to managed the commends.
 */
public class CommendHelper {
    public static final String COLLECTION = "Commends";

    public static CollectionReference getCollectionRef() { return FirebaseFirestore.getInstance().collection(COLLECTION); }

    public static Task<Void> updateCmdQuantity(String commend_id, int newQuantity, float total_prise) {
        Map<String, Object> data = new HashMap<>();
        data.put("quantity", newQuantity);
        data.put("total_prise", total_prise);
        return getCollectionRef().document(commend_id).update(data);
    }

    public static Query getAllClientCmd(String user_id) {
        return getCollectionRef().whereEqualTo("user_id", user_id)
                .whereEqualTo("is_validate", false)
                .orderBy("date_cmd", Query.Direction.DESCENDING);
    }

    public static Task<Void> deleteCommend(String commend_id) {
        return getCollectionRef().document(commend_id).delete();
    }

    public static Task<DocumentReference> addCommend(Commend commend) {
        return getCollectionRef().add(commend);
    }

    public static Task<Void> validateCommend(String cmd_id) {
        return getCollectionRef().document(cmd_id).update("is_validate", true);
    }

    public static Task<QuerySnapshot> userHasCommendThis(String user_id, Book book) {
        return getCollectionRef()
                .whereEqualTo("user_id", user_id)
                .whereEqualTo("book.id", book.getId())
                .get();
    }

    public static Task<Void> billedCommend(String commend_id, String bill_ref) {
        Map<String ,Object> data = new HashMap<>();
        data.put("is_billed", true);
        data.put("bill_ref", bill_ref);
        return getCollectionRef().document(commend_id).update(data);
    }
}
