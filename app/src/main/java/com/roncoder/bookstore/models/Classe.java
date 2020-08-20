package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Classe extends Factory implements Parcelable {
    private int id;
    private String name;
    private String libelle;
    public Classe() {
        this.id = -1;
        this.name = "";
        this.libelle = "";
    }
    public Classe (int id, String name, String libelle) {
        this.id = id;
        this.name = name;
        this.libelle = libelle;
    }

    protected Classe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        libelle = in.readString();
    }

    public static final Creator<Classe> CREATOR = new Creator<Classe>() {
        @Override
        public Classe createFromParcel(Parcel in) {
            return new Classe(in);
        }

        @Override
        public Classe[] newArray(int size) {
            return new Classe[size];
        }
    };

    public int getId () { return id; }
    public void setId (int id) { this.id = id; }
    public String getName () { return name; }
    public void setName (String name) { this.name = name; }
    public String getLibelle() { return libelle; }
    public void setLibelle (String libelle) { this.libelle = libelle; }
    @NonNull
    @Override
    public String toString() {
        return "Classe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", libelle='" + libelle + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(libelle);
    }
}
