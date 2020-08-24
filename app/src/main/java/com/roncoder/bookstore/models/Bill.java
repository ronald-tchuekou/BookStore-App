package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.roncoder.bookstore.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Bill implements Parcelable {

    @SerializedName("ref")
    @Expose
    private String ref;

    @SerializedName("commends")
    @Expose
    private List<Commend> commends;

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("shippingAddress")
    @Expose
    private ShippingAddress shippingAddress;

    @SerializedName("shipping_date")
    @Expose
    private Date shipping_date;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("total_prise")
    @Expose
    private float total_prise;

    @SerializedName("shipping_type")
    @Expose
    private String shipping_type;

    @SerializedName("payment_type")
    @Expose
    private String payment_type;

    @SerializedName("shipping_cost")
    @Expose
    private float shipping_cost;

    public Bill() {
        List<Commend> commends = new ArrayList<>();
        commends.add(new Commend(1, new Book(), 4, Calendar.getInstance().getTime(), true, false));
        commends.add(new Commend(1, new Book(), 2, Calendar.getInstance().getTime(), true, false));

        this.ref = "F331225";
        this.commends = commends;
        this.user = new User();
        this.shippingAddress = new ShippingAddress();
        this.shipping_date = Calendar.getInstance().getTime();
        this.state = Utils.BILL_IN_COURSE;
        this.total_prise = 5000;
        this.shipping_type = Utils.SHIPPING_EXPRESS;
        this.payment_type = Utils.PAYMENT_AT_SHIPPING;
        this.shipping_cost = Utils.SHIPPING_COST_EXPRESS;
    }

    public Bill(String ref, List<Commend> commends, User user, ShippingAddress shippingAddress,
                Date shipping_date, String state, float total_prise, String shipping_type,
                String payment_type, float shipping_cost) {
        setRef(ref);
        setCommends(commends);
        setUser(user);
        setShippingAddress(shippingAddress);
        setShipping_date(shipping_date);
        setState(state);
        setTotal_prise(total_prise);
        setShipping_type(shipping_type);
        setPayment_type(payment_type);
        setShipping_cost(shipping_cost);
    }

    protected Bill(Parcel in) throws ParseException {
        ref = in.readString();
        commends = in.createTypedArrayList(Commend.CREATOR);
        user = in.readParcelable(User.class.getClassLoader());
        shippingAddress = in.readParcelable(ShippingAddress.class.getClassLoader());
        shipping_date = DateFormat.getDateInstance().parse(Objects.requireNonNull(in.readString()));
        state = in.readString();
        total_prise = in.readFloat();
        shipping_type = in.readString();
        payment_type = in.readString();
        shipping_cost = in.readFloat();
    }

    public static final Creator<Bill> CREATOR = new Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel in) {
            try {
                return new Bill(in);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
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

    public Date getShipping_date() {
        return shipping_date;
    }

    public void setShipping_date(Date shipping_date) {
        this.shipping_date = shipping_date;
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

    public void setTotal_prise(float total_prise) {
        this.total_prise = total_prise;
    }

    public String getShipping_type() {
        return shipping_type;
    }

    public void setShipping_type(String shipping_type) {
        this.shipping_type = shipping_type;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public float getShipping_cost() {
        return shipping_cost;
    }

    public void setShipping_cost(float shipping_cost) {
        this.shipping_cost = shipping_cost;
    }

    @NonNull
    @Override
    public String toString() {
        return "Bill{" +
                "ref=" + ref +
                ", commends=" + commends +
                ", user=" + user +
                ", shippingAddress=" + shippingAddress +
                ", shipping_date=" + shipping_date +
                ", state='" + state + '\'' +
                ", total_prise=" + total_prise +
                ", shipping_type=" + shipping_type +
                ", payment_type=" + payment_type +
                ", shipping_cost=" + shipping_cost +
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
        dest.writeString(DateFormat.getDateInstance().format(shipping_date));
        dest.writeString(state);
        dest.writeFloat(total_prise);
        dest.writeString(shipping_type);
        dest.writeString(payment_type);
        dest.writeFloat(shipping_cost);
    }
}
