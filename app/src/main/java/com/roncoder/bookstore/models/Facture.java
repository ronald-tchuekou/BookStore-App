package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Facture implements Parcelable {
    private String ref;
    private List<Commend> commends;
    private User user;
    private ShippingAddress shippingAddress;
    private Date date_facture;
    private String state;
    private float total_prise;
    Facture () { }
    public Facture(String ref, List<Commend> commends, User user, ShippingAddress shippingAddress, Date date_facture,
                   String state) {
        setRef(ref);
        setCommends(commends);
        setUser(user);
        setShippingAddress(shippingAddress);
        setDate_facture(date_facture);
        setState(state);
        setTotal_prise();
    }

    protected Facture(Parcel in) throws ParseException {
        ref = in.readString();
        commends = in.createTypedArrayList(Commend.CREATOR);
        user = in.readParcelable(User.class.getClassLoader());
        shippingAddress = in.readParcelable(ShippingAddress.class.getClassLoader());
        date_facture = DateFormat.getDateInstance().parse(Objects.requireNonNull(in.readString()));
        state = in.readString();
        total_prise = in.readFloat();
    }

    public static final Creator<Facture> CREATOR = new Creator<Facture>() {
        @Override
        public Facture createFromParcel(Parcel in) {
            try {
                return new Facture(in);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Facture[] newArray(int size) {
            return new Facture[size];
        }
    };

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public List<Commend> getCommends() {
        return commends;
    }

    public void setCommends(List<Commend> commends) {
        this.commends = commends;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Date getDate_facture() {
        return date_facture;
    }

    public void setDate_facture(Date date_facture) {
        this.date_facture = date_facture;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public float getTotal_prise() {
        return total_prise;
    }

    public void setTotal_prise() {
        float prise = 0;
        for (Commend c : commends)
            prise += c.getTotal_prise();
        this.total_prise = prise;
    }

    @NonNull
    @Override
    public String toString() {
        return "Facture{" +
                "ref=" + ref +
                ", commends=" + commends +
                ", user=" + user +
                ", shippingAddress=" + shippingAddress +
                ", date_facture=" + date_facture +
                ", state='" + state + '\'' +
                ", total_prise=" + total_prise +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ref);
        dest.writeList(commends);
        dest.writeParcelable(user, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(shippingAddress, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeString(DateFormat.getDateInstance().format(date_facture));
        dest.writeString(state);
        dest.writeFloat(total_prise);
    }
}
