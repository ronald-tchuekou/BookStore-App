package com.roncoder.bookstore.dbHelpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.roncoder.bookstore.models.Message;

public class MessageHelper {
    public static final String COLLECTION = "Messages";
    public static final String CON_COLLECTION = "Conversations";

    public static CollectionReference getCollectionRef () {
        return FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    public static CollectionReference getConCollectionRef () {
        return FirebaseFirestore.getInstance().collection(CON_COLLECTION);
    }

    public static Query getMessages (String container) {
        return getCollectionRef().whereEqualTo("con_id", container).orderBy("date");
    }

    public static Query getConversations(String user_id) {
        return getConCollectionRef()
                .whereArrayContains("members", user_id)
                .orderBy("date", Query.Direction.DESCENDING);
    }

    public static Task<DocumentReference> sendMessage(Message message) {
        return getCollectionRef().add(message);
    }
}
