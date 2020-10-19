package com.roncoder.bookstore.dbHelpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.roncoder.bookstore.models.Book;

public class BookHelper {

    public static final String COLLECTION = "Books";
    public static final String DICTIONARIES = "Dictionaries";

    public static CollectionReference getDictionariesColRef () { return FirebaseFirestore.getInstance().collection(DICTIONARIES); }

    public static CollectionReference getCollectionRef () { return FirebaseFirestore.getInstance().collection(COLLECTION); }

    public static Query getBooksByCycle(String cycle) {
        return getCollectionRef().whereEqualTo("cycle", cycle).whereGreaterThan("stock_quantity", 0);
    }

    public static Query getClassBooks(String _class) {
        return getCollectionRef().whereEqualTo("classes", _class).whereGreaterThan("stock_quantity", 0);
    }

    public static Query getAllBooks() {
        return getCollectionRef().whereGreaterThan("stock_quantity", 0);
    }

    public static Task<DocumentReference> addBook(Book book) {
        return getCollectionRef().add(book);
    }

    public static Task<DocumentSnapshot> getBookById(String book_id) {
        return getCollectionRef().document(book_id).get();
    }

    public static Task<QuerySnapshot> getBookByTitle(String title) {
        return getCollectionRef().whereEqualTo("title", title).get();
    }

    public static Query getDictionaries() {
        return getDictionariesColRef().orderBy("title");
    }

    public static Task<QuerySnapshot> getDictionaryByTitle(String title) {
        return getDictionariesColRef().whereEqualTo("title", title).get();
    }

    public static Task<DocumentReference> addDictionary(Book book) {
        return getDictionariesColRef().add(book);
    }
}
