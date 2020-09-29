package com.roncoder.bookstore.dbHelpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.roncoder.bookstore.models.Classes;

public class ClassHelper {

    public static final String COLLECTION = "Classes";

    public static CollectionReference getCollectionRef () {
        return FirebaseFirestore.getInstance().collection(COLLECTION);
    }
    public static Query getClassesByCycle(String cycle) {
        return getCollectionRef().orderBy("cycle").startAt(cycle).endAt(cycle+'\uf8ff');
    }

    public static Query getAllClass() {
        return getCollectionRef().orderBy("name");
    }

    public static Task<DocumentReference> addClass(Classes classes) {
        return getCollectionRef().add(classes);
    }

    public static Task<QuerySnapshot> getClassByName(String name) {
        return getCollectionRef().whereEqualTo("name", name).get();
    }
}
