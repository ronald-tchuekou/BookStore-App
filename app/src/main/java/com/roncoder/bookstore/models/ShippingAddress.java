package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.roncoder.bookstore.databases.CommendHelper;

/**
 * Class that hold the information about the shipping address.
 *
 * @author Ronald Tchuekou.
 */
public class ShippingAddress implements Parcelable {
    private int id;
    private String receiver_name;
    private String phone_number;
    private String district;
    private String street;
    private String shipping_type; // this is the type of the shipping (Instant / Standard / Express).
    private String more_description;
    private boolean is_default;

    /**
     * Default constructor of the class.
     */
    public ShippingAddress() {
        id = -1;
        receiver_name = "receiver_name";
        phone_number = "phone_number";
        district = "Yaoundé";
        street = "street";
        shipping_type = CommendHelper.INSTANT_SHIPPING_TYPE;
        more_description = "more_description";
        is_default = false;
    }

    public ShippingAddress(int id, String receiver_name, String phone_number, String district,
                           String street, String more_description, String shipping_type, boolean is_default) {
        this.id = id;
        this.receiver_name = receiver_name;
        this.phone_number = phone_number;
        this.district = district;
        this.street = street;
        this.shipping_type = shipping_type;
        this.more_description = more_description;
        this.is_default = is_default;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getShipping_type() {
        return shipping_type;
    }

    public void setShipping_type(String shipping_type) {
        this.shipping_type = shipping_type;
    }

    public String getMore_description() {
        return more_description;
    }

    public void setMore_description(String more_description) {
        this.more_description = more_description;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    protected ShippingAddress(Parcel in) {
        id = in.readInt();
        receiver_name = in.readString();
        phone_number = in.readString();
        district = in.readString();
        street = in.readString();
        shipping_type = in.readString();
        more_description = in.readString();
        is_default = in.readByte() != 0;
    }

    public static final Creator<ShippingAddress> CREATOR = new Creator<ShippingAddress>() {
        @Override
        public ShippingAddress createFromParcel(Parcel in) {
            return new ShippingAddress(in);
        }

        @Override
        public ShippingAddress[] newArray(int size) {
            return new ShippingAddress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(receiver_name);
        dest.writeString(phone_number);
        dest.writeString(district);
        dest.writeString(street);
        dest.writeString(more_description);
        dest.writeString(shipping_type);
        dest.writeInt(is_default ? 1 : 0);
    }
}
