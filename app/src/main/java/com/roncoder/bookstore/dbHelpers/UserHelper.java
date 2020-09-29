package com.roncoder.bookstore.dbHelpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.roncoder.bookstore.models.User;

public class UserHelper {

    public static final String COLLECTION = "Users";

    public static CollectionReference getCollectionRef() {
        return FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    public static Task<Void> addUser (User user) {
        return getCollectionRef().document(user.getId()).set(user);
    }

    public static Task<DocumentSnapshot> getUserById (String id) {
        return getCollectionRef().document(id).get();
    }
}
