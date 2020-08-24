package com.roncoder.bookstore.api;

import androidx.annotation.NonNull;

public class Result {

    private boolean success;
    private boolean error;
    private String message;
    private String value;

    Result () {
        this.success = false;
        this.error = false;
        this.message = "";
        this.value = "";
    }

    Result (boolean success, boolean error, String message, String value) {
        this.success = success;
        this.error = error;
        this.message = message;
        this.value = value;
    }

    public boolean getSuccess () { return success; }
    public void setSuccess (boolean success) { this.success = success; }
    public boolean getError () { return error; }
    public void setError (boolean error) { this.error = error; }
    public String getMessage () { return message; }
    public void setMessage (String message) { this.message = message; }
    public String getValue () { return value; }
    public void setValue (String value) { this.value = value; }

    @NonNull
    public String toString () {
        return "Result: {" +
                "success:" + success +
                ", error:" + error +
                ", message:" + message +
                ", value:" + value +
                "}";
    }
}
