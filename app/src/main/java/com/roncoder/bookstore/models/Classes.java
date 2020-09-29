package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Classes extends Factory implements Parcelable {
    private String id;
    private String name;
    private String libel;
    private String cycle;
    public Classes() {
        this.id = "";
        this.name = "";
        this.libel = "";
        this.cycle = "";
    }
    public Classes(String id, String name, String libel, String cycle) {
        setId(id);
        setName(name);
        setLibel(libel);
        setCycle(cycle);
    }

    protected Classes(Parcel in) {
        id = in.readString();
        name = in.readString();
        libel = in.readString();
        cycle = in.readString();
    }

    public static final Creator<Classes> CREATOR = new Creator<Classes>() {
        @Override
        public Classes createFromParcel(Parcel in) {
            return new Classes(in);
        }

        @Override
        public Classes[] newArray(int size) {
            return new Classes[size];
        }
    };

    public String getId () { return id; }
    public void setId (String id) { this.id = id; }
    public String getName () { return name; }
    public void setName (String name) { this.name = name; }
    public String getLibel() { return libel; }
    public void setLibel(String libel) { this.libel = libel; }
    public String getCycle () { return cycle; }
    public void setCycle (String cycle) { this.cycle = cycle; }

    @NonNull
    @Override
    public String toString() {
        return "Classe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", libelle='" + libel + '\'' +
                ", cycle='" + cycle + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(libel);
        dest.writeString(cycle);
    }
}
