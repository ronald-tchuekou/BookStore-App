package com.roncoder.bookstore.dbHelpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.roncoder.bookstore.models.ShippingAddress;

public class ShippingAddressHelper {

    public static final String COLLECTION = "ShippingAddress";

    public static CollectionReference getCollectionRef() {
        return FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    public static Task<DocumentSnapshot> getShippingAddressByRef(String ship_ref) {
        return getCollectionRef().document(ship_ref).get();
    }

    public static Task<DocumentReference> saveShippingAddress(ShippingAddress shippingAddress) {
        return getCollectionRef().add(shippingAddress);
    }

    public static Task<Void> update(ShippingAddress shippingAddress) {
        return getCollectionRef().document(shippingAddress.getRef()).set(shippingAddress);
    }
}
