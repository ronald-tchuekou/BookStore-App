package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    private int id;
    private String name;
    private String surname;
    private String phone;
    private String login;
    private String password;
    private boolean is_admin;

    public User () {
        this.id = -1;
        this.name = "name";
        this.surname = "surname";
        this.phone = "phone";
        this.login = "login";
        this.password = "";
        this.is_admin = false;
    }

    public User (int id, String name, String surname, String phone, String login, String password,
                 boolean is_admin) {
        setId(id);
        setName(name);
        setSurname(surname);
        setPhone(phone);
        setLogin(login);
        setPassword(password);
        setIs_admin(is_admin);
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        surname = in.readString();
        phone = in.readString();
        login = in.readString();
        password = in.readString();
        is_admin = in.readInt() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setId(int id) { this.id = id; }
    public int getId () { return id; }
    public String getName () { return name; }
    public void setName (String name) { this.name = name; }
    public String getSurname () { return surname; }
    public void setSurname (String surname) { this.surname = surname; }
    public void setPhone (String phone) { this.phone = phone;}
    public String getPhone () { return phone; }
    public void setLogin (String login) { this.login = login; }
    public String getLogin () { return login; }
    public void setPassword (String password) { this.password = password; }
    public String getPassword () { return password; }
    public void setIs_admin (boolean is_admin) { this.is_admin = is_admin; }
    public boolean isIs_admin() { return is_admin; }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phone='" + phone + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", is_admin='" + is_admin + '\'' +
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
        dest.writeString(surname);
        dest.writeString(phone);
        dest.writeString(login);
        dest.writeString(password);
        dest.writeInt(is_admin ? 1 : 0);
    }
}
