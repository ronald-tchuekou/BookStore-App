package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Book extends Factory implements Parcelable {
    private String id;
    private String title;
    private String author;
    private String editor;
    private String image1_front;
    private String book_state;
    private String classes;
    private String cycle;
    private float unit_prise;
    private int stock_quantity;
    public Book () {
        this.id = "";
        this.title = "";
        this.author = "";
        this.editor = "";
        this.image1_front = "";
        this.book_state = "";
        this.classes = "";
        this.cycle = "";
        this.unit_prise = 0;
        this.stock_quantity = 0;
    }
    public Book(String id, String title, String author, String editor, String image1_front,
                String book_state, String classes, String cycle, float unit_prise, int stock_quantity) {
        setId(id);
        setTitle(title);
        setAuthor(author);
        setEditor(editor);
        setImage1_front(image1_front);
        setBook_state(book_state);
        setClasses(classes);
        setCycle(cycle);
        setUnit_prise(unit_prise);
        setStock_quantity(stock_quantity);
    }
    protected Book(Parcel in) {
        id = in.readString();
        title = in.readString();
        author = in.readString();
        editor = in.readString();
        image1_front = in.readString();
        book_state = in.readString();
        classes = in.readString();
        cycle = in.readString();
        unit_prise = in.readFloat();
        stock_quantity = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getImage1_front() {
        return image1_front;
    }

    public void setImage1_front(String image1_front) {
        this.image1_front = image1_front;
    }

    public String getBook_state() {
        return book_state;
    }

    public void setBook_state(String book_state) {
        this.book_state = book_state;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public float getUnit_prise() {
        return unit_prise;
    }

    public void setUnit_prise(float unit_prise) {
        this.unit_prise = unit_prise;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    @NonNull
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", editor='" + editor + '\'' +
                ", image1_front='" + image1_front + '\'' +
                ", book_state='" + book_state + '\'' +
                ", classes='" + classes + '\'' +
                ", cycle='" + cycle + '\'' +
                ", unit_prise=" + unit_prise +
                ", stock_quantity=" + stock_quantity +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(editor);
        dest.writeString(image1_front);
        dest.writeString(book_state);
        dest.writeString(classes);
        dest.writeString(cycle);
        dest.writeFloat(unit_prise);
        dest.writeInt(stock_quantity);
    }
}
