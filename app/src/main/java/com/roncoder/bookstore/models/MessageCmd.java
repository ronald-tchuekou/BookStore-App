package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MessageCmd implements Parcelable {

    private String image;
    private String title;
    private int quantity;
    private float total_prise;

    public MessageCmd() {
        this.image = "";
        this.title = "";
        this.quantity = 0;
        this.total_prise = 0;
    }

    public MessageCmd(String image, String title, int quantity, float total_prise) {
        this.image = image;
        this.title = title;
        this.quantity = quantity;
        this.total_prise = total_prise;
    }

    protected MessageCmd(Parcel in) {
        image = in.readString();
        title = in.readString();
        quantity = in.readInt();
        total_prise = in.readFloat();
    }

    public static final Creator<MessageCmd> CREATOR = new Creator<MessageCmd>() {
        @Override
        public MessageCmd createFromParcel(Parcel in) {
            return new MessageCmd(in);
        }

        @Override
        public MessageCmd[] newArray(int size) {
            return new MessageCmd[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getTotal_prise() {
        return total_prise;
    }

    public void setTotal_prise(float total_prise) {
        this.total_prise = total_prise;
    }

    @NonNull
    @Override
    public String toString() {
        return "MessageCmd{" +
                "image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", quantity=" + quantity +
                ", total_prise=" + total_prise +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(title);
        dest.writeInt(quantity);
        dest.writeFloat(total_prise);
    }
}
