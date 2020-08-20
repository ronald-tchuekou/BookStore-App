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
    private String token;
    private String password;
    private String status;
    public User () {
        this.id = -1;
        this.name = "name";
        this.surname = "surname";
        this.phone = "phone";
        this.login = "login";
        this.token = "token";
        this.password = "";
        this.status = "client";
    }
    public User (int id, String name, String surname, String phone, String login, String token, String password,
                 String status) {
        setId(id);
        setName(name);
        setSurname(surname);
        setPhone(phone);
        setLogin(login);
        setToken(token);
        setPassword(password);
        setStatus(status);
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        surname = in.readString();
        phone = in.readString();
        login = in.readString();
        token = in.readString();
        password = in.readString();
        status = in.readString();
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
    public void setToken (String token) { this.token = token; }
    public String getToken () { return token; }
    public void setPassword (String password) { this.password = password; }
    public String getPassword () { return password; }
    public void setStatus (String status) { this.status = status; }
    public String getStatus () { return status; }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phone='" + phone + '\'' +
                ", login='" + login + '\'' +
                ", token='" + token + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
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
        dest.writeString(login);
        dest.writeString(token);
        dest.writeString(password);
        dest.writeString(status);
    }
}
