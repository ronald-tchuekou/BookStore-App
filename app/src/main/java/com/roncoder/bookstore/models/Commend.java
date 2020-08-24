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
    private boolean is_billed;
    private boolean is_validate;
    private String bill_ref;

    public Commend () {
        id = -1;
        book = new Book();
        quantity = 0;
        total_prise = 0;
        date_cmd = new Date();
        is_billed = false;
        is_validate = false;
        bill_ref = "";
    }
    public Commend (int id, Book book, int quantity, Date date_cmd, boolean is_billed, boolean is_validate) {
        setId(id);
        setBook(book);
        setTotal_prise();
        setQuantity(quantity);
        setDate_cmd(date_cmd);
        setIs_billed(is_billed);
        setIs_validate(is_validate);
        setBill_ref("");
    }
    public Commend (int id, Book book, int quantity,float total_prise, Date date_cmd, boolean is_billed,
                    boolean is_validate, String bill_ref) {
        setId(id);
        setBook(book);
        setTotal_prise(total_prise);
        setQuantity(quantity);
        setDate_cmd(date_cmd);
        setIs_billed(is_billed);
        setIs_validate(is_validate);
        setBill_ref(bill_ref);
    }
    protected Commend(Parcel in) throws ParseException {
        id = in.readInt();
        book = in.readParcelable(Book.class.getClassLoader());
        quantity = in.readInt();
        total_prise = in.readFloat();
        date_cmd = DateFormat.getDateInstance().parse(Objects.requireNonNull(in.readString()));
        is_billed = in.readInt() != 0;
        is_validate = in.readInt() != 0;
        bill_ref = in.readString();
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
    public void setTotal_prise() { total_prise = book.getUnit_prise() * quantity; }
    public Date getDate_cmd() { return date_cmd; }
    public void setDate_cmd(Date date_cmd) { this.date_cmd = date_cmd; }
    public float getTotal_prise() { return total_prise; }
    public void setTotal_prise(float total_prise) { this.total_prise = total_prise; }
    public boolean isIs_billed() { return is_billed; }
    public void setIs_billed(boolean is_billed) { this.is_billed = is_billed; }
    public boolean isIs_validate() { return is_validate; }
    public void setIs_validate(boolean is_validate) { this.is_validate = is_validate; }
    public String getBill_ref () { return bill_ref; }
    public void setBill_ref (String bill_ref) { this.bill_ref = bill_ref; }

    @NonNull
    @Override
    public String toString() {
        return "Commend{" +
                "id=" + id +
                ", book=" + book +
                ", quantity=" + quantity +
                ", total_prise=" + total_prise +
                ", date_cmd=" + date_cmd +
                ", is_billed=" + is_billed +
                ", is_validate=" + is_validate +
                ", bill_ref=" + bill_ref +
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
        dest.writeInt(is_billed ? 1 : 0);
        dest.writeInt(is_validate ? 1 : 0);
        dest.writeString(bill_ref);
    }
}
