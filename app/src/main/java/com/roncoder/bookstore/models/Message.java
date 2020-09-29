package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class Message implements Parcelable {

    private String id;
    private String type;
    private String container;
    private Date date;
    private String text;
    private MessageCmd message_cmd;
    private boolean is_read;

    public Message ( ) {
        id = "";
        type = "";
        container = "";
        date = new Date();
        text = "";
        message_cmd = new MessageCmd();
        is_read = false;
    }
    public Message (String type, Date date, String text, String container) {
        this.id = id;
        this.type = type;
        this.container = container;
        this.date = date;
        this.text = text;
        this.message_cmd = new MessageCmd();
        this.is_read = false;
    }

    protected Message(Parcel in) {
        id = in.readString();
        type = in.readString();
        container = in.readString();
        try {
            date = DateFormat.getDateInstance().parse(Objects.requireNonNull(in.readString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        text = in.readString();
        message_cmd = in.readParcelable(MessageCmd.class.getClassLoader());
        is_read = in.readByte() != 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getId () { return id; }
    public void setId (String id) { this.id = id; }
    public String getType () { return type; }
    public void setType (String type) { this.type = type; }
    public Date getDate () { return date; }
    public void setDate (Date date) { this.date = date; }
    public String getText () { return text; }
    public void setText (String text) { this.text = text; }
    public boolean isIs_read () { return is_read; }
    public void setIs_read (boolean is_read) { this.is_read = is_read; }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public MessageCmd getMessage_cmd() {
        return message_cmd;
    }

    public void setMessage_cmd(MessageCmd message_cmd) {
        this.message_cmd = message_cmd;
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", sender='" + container + '\'' +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", message_cmd=" + message_cmd +
                ", is_read=" + is_read +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(container);
        dest.writeString(date.toString());
        dest.writeString(text);
        dest.writeParcelable(message_cmd, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
    }
}
