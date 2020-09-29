package com.roncoder.bookstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class Message {

    private String id;
    private String con_id;
    private String sender;
    private String text;
    private Date date;
    private boolean is_read;
    private MessageCmd message_cmd;

    public Message() {
    }

    public Message(String con_id, String sender, String text, Date date) {
        this.con_id = con_id;
        this.sender = sender;
        this.text = text;
        this.date = date;
        this.is_read =  false;
        this.message_cmd = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCon_id() {
        return con_id;
    }

    public void setCon_id(String con_id) {
        this.con_id = con_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
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
                ", con_id='" + con_id + '\'' +
                ", sender='" + sender + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", is_read=" + is_read +
                ", message_cmd=" + message_cmd +
                '}';
    }
}
