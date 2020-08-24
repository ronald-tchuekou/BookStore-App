package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that hold the information about the shipping address.
 *
 * @author Ronald Tchuekou.
 */
public class ShippingAddress implements Parcelable {
    private String ref;
    private String receiver_name;
    private String phone_number;
    private String district;
    private String street;
    private String more_description;
    private boolean is_default;

    /**
     * Default constructor of the class.
     */
    public ShippingAddress() {
        ref = "F12";
        receiver_name = "receiver_name";
        phone_number = "phone_number";
        district = "Yaoundé";
        street = "street";
        more_description = "more_description";
        is_default = false;
    }

    public ShippingAddress(String ref, String receiver_name, String phone_number, String district,
                           String street, String more_description, boolean is_default) {
        setRef(ref);
        setReceiver_name(receiver_name);
        setPhone_number(phone_number);
        setDistrict(district);
        setStreet(street);
        setMore_description(more_description);
        setIs_default(is_default);
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String id) {
        this.ref = ref;
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
        ref = in.readString();
        receiver_name = in.readString();
        phone_number = in.readString();
        district = in.readString();
        street = in.readString();
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
        dest.writeString(ref);
        dest.writeString(receiver_name);
        dest.writeString(phone_number);
        dest.writeString(district);
        dest.writeString(street);
        dest.writeString(more_description);
        dest.writeInt(is_default ? 1 : 0);
    }
}
