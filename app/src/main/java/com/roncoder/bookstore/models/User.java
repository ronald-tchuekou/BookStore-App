package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    private String id;
    private String name;
    private String surname;
    private String phone;
    private String login;
    private String password;
    private boolean is_admin;
    private String token;
    private String profile;

    public User () {
        this.id = "";
        this.name = "";
        this.surname = "";
        this.phone = "";
        this.login = "";
        this.password = "";
        this.is_admin = false;
        this.token = "";
        this.profile = "";
    }

    public User (String id, String name, String surname, String phone, String login, String password,
                 boolean is_admin) {
        setId(id);
        setName(name);
        setSurname(surname);
        setPhone(phone);
        setLogin(login);
        setPassword(password);
        setIs_admin(is_admin);
        setToken("");
        setProfile("");
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        surname = in.readString();
        phone = in.readString();
        login = in.readString();
        password = in.readString();
        is_admin = in.readInt() != 0;
        token = in.readString();
        profile = in.readString();
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

    public void setId(String id) { this.id = id; }
    public String getId () { return id; }
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
    public void setToken (String token) { this.token = token; }
    public String getToken () { return token; }
    public void setProfile (String profile) { this.profile = profile; }
    public String getProfile () { return profile; }

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
                ", token='" + token + '\'' +
                ", profile='" + profile + '\'' +
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
        dest.writeString(surname);
        dest.writeString(phone);
        dest.writeString(login);
        dest.writeString(password);
        dest.writeInt(is_admin ? 1 : 0);
        dest.writeString(token);
        dest.writeString(profile);
    }
}
