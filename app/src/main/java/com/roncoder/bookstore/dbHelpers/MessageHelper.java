package com.roncoder.bookstore.dbHelpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.roncoder.bookstore.models.Message;

public class MessageHelper {
    public static final String COLLECTION = "Messages";
    public static final String CM_COLLECTION = "Contacts_messages";

    public static CollectionReference getCollectionRef () {
        return FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    public static CollectionReference getCMCollectionRef () {
        return FirebaseFirestore.getInstance().collection(CM_COLLECTION);
    }

    public static Query getMessages (String container) {
        return getCollectionRef().whereEqualTo("container", container);
    }

    public static Query getContactMessages (String user_id) {
        return getCollectionRef().whereEqualTo("sender", user_id).whereEqualTo("receiver", user_id);
    }

    public static Task<DocumentReference> sendMessage(Message message) {
        return getCollectionRef().add(message);
    }
}
