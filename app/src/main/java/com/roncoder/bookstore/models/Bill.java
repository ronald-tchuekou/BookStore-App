package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


import com.roncoder.bookstore.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Bill implements Parcelable {
    private String ref;
    private List<String> commend_ids;
    private String user_id;
    private String shipping_ref;
    private Date shipping_date;
    private String state;
    private float total_prise;
    private String shipping_type;
    private String payment_type;
    private float shipping_cost;

    public Bill() {
        this.ref = "";
        this.commend_ids = new ArrayList<>();
        this.user_id = "";
        this.shipping_ref = "";
        this.shipping_date = new Date();
        this.state = Utils.BILL_IN_COURSE;
        this.total_prise = 0;
        this.shipping_type = Utils.SHIPPING_INSTANT;
        this.payment_type = "";
        this.shipping_cost = 0;
    }

    public Bill (String user_id, List<String> commend_ids, String shipping_ref, Date shipping_date, float total_prise,
                 String shipping_type) {
        setUser_id(user_id);
        setCommend_ids(commend_ids);
        setShipping_ref(shipping_ref);
        setShipping_date(shipping_date);
        setTotal_prise(total_prise);
        setShipping_type(shipping_type);

    }

    public Bill (String ref, List<String> commend_ids, String user_id, String shipping_ref,
                Date shipping_date, String state, float total_prise, String shipping_type,
                String payment_type, float shipping_cost) {
        setRef(ref);
        setCommend_ids(commend_ids);
        setUser_id(user_id);
        setShipping_ref(shipping_ref);
        setShipping_date(shipping_date);
        setState(state);
        setTotal_prise(total_prise);
        setShipping_type(shipping_type);
        setPayment_type(payment_type);
        setShipping_cost(shipping_cost);
    }

    protected Bill(Parcel in) throws ParseException {
        ref = in.readString();
        commend_ids = in.createStringArrayList();
        user_id = in.readString();
        shipping_ref = in.readString();
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

    public List<String> getCommend_ids() {
        return commend_ids;
    }

    public void setCommend_ids(List<String> commend_ids) {
        this.commend_ids = commend_ids;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getShipping_ref() {
        return shipping_ref;
    }

    public void setShipping_ref(String shipping_ref) {
        this.shipping_ref = shipping_ref;
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
                ", commend_ids=" + commend_ids +
                ", user=" + user_id +
                ", shippingAddress=" + shipping_ref +
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
        dest.writeStringList(commend_ids);
        dest.writeString(user_id);
        dest.writeString(shipping_ref);
        dest.writeString(DateFormat.getDateInstance().format(shipping_date));
        dest.writeString(state);
        dest.writeFloat(total_prise);
        dest.writeString(shipping_type);
        dest.writeString(payment_type);
        dest.writeFloat(shipping_cost);
    }
}
