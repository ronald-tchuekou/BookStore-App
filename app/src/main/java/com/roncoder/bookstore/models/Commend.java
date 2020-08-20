package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class Commend implements Parcelable {
    private int id;
    private Book book;
    private int quantity;
    private float total_prise;
    private Date date_cmd;
    public Commend () {
        id = -1;
        book = new Book();
        quantity = 0;
        total_prise = 0;
        date_cmd = new Date();
    }
    public Commend (int id, Book book, int quantity, Date date_cmd) {
        setId(id);
        setBook(book);
        setTotal_prise();
        setQuantity(quantity);
        setDate_cmd(date_cmd);
    }
    protected Commend(Parcel in) throws ParseException {
        id = in.readInt();
        book = in.readParcelable(Book.class.getClassLoader());
        quantity = in.readInt();
        total_prise = in.readFloat();
        date_cmd = DateFormat.getDateInstance().parse(Objects.requireNonNull(in.readString()));
    }
    public static final Creator<Commend> CREATOR = new Creator<Commend>() {
        @Override
        public Commend createFromParcel(Parcel in) {
            try {
                return new Commend(in);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Commend[] newArray(int size) {
            return new Commend[size];
        }
    };

    public int getId () { return id; }
    public void setId (int id) { this.id = id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book;}
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public float getTotal_prise() { return total_prise; }
    public void setTotal_prise() { total_prise = book.getUnit_prise() * quantity; }
    public Date getDate_cmd() { return date_cmd; }
    public void setDate_cmd(Date date_cmd) { this.date_cmd = date_cmd; }

    @NonNull
    @Override
    public String toString() {
        return "Commend{" +
                "id=" + id +
                ", book=" + book +
                ", quantity=" + quantity +
                ", total_prise=" + total_prise +
                ", date_cmd=" + date_cmd +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(book, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeInt(quantity);
        dest.writeFloat(total_prise);
        dest.writeString(DateFormat.getDateInstance().format(date_cmd));
    }
}
