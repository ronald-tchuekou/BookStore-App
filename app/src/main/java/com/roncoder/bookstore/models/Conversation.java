package com.roncoder.bookstore.models;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class Conversation {
    private String id;
    private String creator;
    private List<String> members;
    private Message last_message;
    private Date date;
    private int not_read_count1;
    private int not_read_count2;

    public Conversation() { }

    public Conversation(String creator, List<String> members, Date date) {
        setId(id);
        setCreator(creator);
        setMembers(members);
        setLast_message(last_message);
        setDate(date);
        setNot_read_count1(0);
        setNot_read_count2(0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
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

    public int getNot_read_count1() {
        return not_read_count1;
    }

    public void setNot_read_count1(int not_read_count1) {
        this.not_read_count1 = not_read_count1;
    }

    public int getNot_read_count2() {
        return not_read_count2;
    }

    public void setNot_read_count2(int not_read_count2) {
        this.not_read_count2 = not_read_count2;
    }

    @NonNull
    @Override
    public String toString() {
        return "ContactMessage{" +
                "id='" + id + '\'' +
                ", creator='" + creator + '\'' +
                ", last_message='" + last_message + '\'' +
                ", date=" + date +
                ", not_read_count1=" + not_read_count1 +
                ", not_read_count2=" + not_read_count2 +
                '}';
    }

}
