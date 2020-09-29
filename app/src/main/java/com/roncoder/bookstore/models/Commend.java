package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class Commend implements Parcelable {
    private String id;
    private String user_id;
    private String book_id;
    private int quantity;
    private float total_prise;
    private Date date_cmd;
    private boolean is_billed;
    private boolean is_validate;
    private String cm_id;
    private String bill_ref;

    public Commend () {
        id = "";
        user_id = "";
        book_id = "";
        quantity = 0;
        total_prise = 0;
        date_cmd = new Date();
        is_billed = false;
        is_validate = false;
        cm_id = "";
        bill_ref = "";
    }
    public Commend (String book_id, String user_id, int quantity, Date date_cmd, float total_prise) {
        setId("");
        setUser_id(user_id);
        setBook_id(book_id);
        setQuantity(quantity);
        setDate_cmd(date_cmd);
        setIs_billed(false);
        setIs_validate(false);
        setCm_id("");
        setBill_ref("");
        setTotal_prise(total_prise);
    }
    public Commend (String id, String book_id, int quantity, float total_prise, Date date_cmd, boolean is_billed,
                    boolean is_validate, String bill_ref) {
        setId(id);
        setUser_id("");
        setBook_id(book_id);
        setTotal_prise(total_prise);
        setQuantity(quantity);
        setDate_cmd(date_cmd);
        setIs_billed(is_billed);
        setIs_validate(is_validate);
        setCm_id("");
        setBill_ref(bill_ref);
    }
    protected Commend(Parcel in) throws ParseException {
        id = in.readString();
        user_id = in.readString();
        book_id = in.readString();
        quantity = in.readInt();
        total_prise = in.readFloat();
        date_cmd = DateFormat.getDateInstance().parse(Objects.requireNonNull(in.readString()));
        is_billed = in.readInt() != 0;
        is_validate = in.readInt() != 0;
        cm_id = in.readString();
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

    public String getId () { return id; }
    public void setId (String id) { this.id = id; }
    public String getUser_id () { return user_id; }
    public void setUser_id (String user_id) { this.user_id = user_id; }
    public String getBook_id() { return book_id; }
    public void setBook_id(String book_id) { this.book_id = book_id;}
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Date getDate_cmd() { return date_cmd; }
    public void setDate_cmd(Date date_cmd) { this.date_cmd = date_cmd; }
    public float getTotal_prise() { return total_prise; }
    public void setTotal_prise(float total_prise) { this.total_prise = total_prise; }
    public boolean isIs_billed() { return is_billed; }
    public void setIs_billed(boolean is_billed) { this.is_billed = is_billed; }
    public boolean isIs_validate() { return is_validate; }
    public void setIs_validate(boolean is_validate) { this.is_validate = is_validate; }
    public String getCm_id() { return cm_id; }
    public void setCm_id(String cm_id) { this.cm_id = cm_id; }
    public String getBill_ref () { return bill_ref; }
    public void setBill_ref (String bill_ref) { this.bill_ref = bill_ref; }

    @NonNull
    @Override
    public String toString() {
        return "Commend{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", book_id=" + book_id +
                ", quantity=" + quantity +
                ", total_prise=" + total_prise +
                ", date_cmd=" + date_cmd +
                ", is_billed=" + is_billed +
                ", is_validate=" + is_validate +
                ", cm_id=" + cm_id +
                ", bill_ref=" + bill_ref +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_id);
        dest.writeString(book_id);
        dest.writeInt(quantity);
        dest.writeFloat(total_prise);
        dest.writeString(DateFormat.getDateInstance().format(date_cmd));
        dest.writeInt(is_billed ? 1 : 0);
        dest.writeInt(is_validate ? 1 : 0);
        dest.writeString(cm_id);
        dest.writeString(bill_ref);
    }
}
