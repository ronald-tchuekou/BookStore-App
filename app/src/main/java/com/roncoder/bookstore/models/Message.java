package com.roncoder.bookstore.models;

import java.util.Date;

public class Message {
    private int id;
    private String type;
    private Date date;
    private String text;
    public Message () { }
    public Message (int id, String type, Date date, String text) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.text = text;
    }
    public int getId () { return id; }
    public void setId (int id) { this.id = id; }
    public String getType () { return type; }
    public void setType (String type) { this.type = type; }
    public Date getDate () { return date; }
    public void setDate (Date date) { this.date = date; }
    public String getText () { return text; }
    public void setText (String text) { this.text = text; }
}
