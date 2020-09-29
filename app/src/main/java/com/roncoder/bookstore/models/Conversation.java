package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class ContactMessage implements Parcelable {
    private String id;
    private String sender;
    private String receiver;
    private Message last_message;
    private Date date;
    private int not_read_count;

    public ContactMessage() {
        setId("");
        setSender("");
        setReceiver("");
        setLast_message(new Message());
        setDate(new Date());
        setNot_read_count(0);
    }

    public ContactMessage(String sender, String receiver, Date date) {
        setId(id);
        setSender(sender);
        setReceiver(receiver);
        setLast_message(null);
        setDate(date);
        setNot_read_count(not_read_count);
    }

    protected ContactMessage(Parcel in) {
        id = in.readString();
        sender = in.readString();
        receiver = in.readString();
        last_message = in.readParcelable(Message.class.getClassLoader());
        String date_str = in.readString();
        if (date_str != null) {
            try {
                date = DateFormat.getDateInstance().parse(date_str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        not_read_count = in.readInt();
    }

    public static final Creator<ContactMessage> CREATOR = new Creator<ContactMessage>() {
        @Override
        public ContactMessage createFromParcel(Parcel in) {
            return new ContactMessage(in);
        }

        @Override
        public ContactMessage[] newArray(int size) {
            return new ContactMessage[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Message getLast_message() {
        return last_message;
    }

    public void setLast_message(Message last_message) {
        this.last_message = last_message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNot_read_count() {
        return not_read_count;
    }

    public void setNot_read_count(int not_read_count) {
        this.not_read_count = not_read_count;
    }

    @NonNull
    @Override
    public String toString() {
        return "ContactMessage{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", last_message='" + last_message + '\'' +
                ", date=" + date +
                ", not_read_count=" + not_read_count +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(sender);
        dest.writeString(receiver);
        dest.writeParcelable(last_message, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeString(DateFormat.getDateInstance().format(date));
        dest.writeInt(not_read_count);
    }
}
