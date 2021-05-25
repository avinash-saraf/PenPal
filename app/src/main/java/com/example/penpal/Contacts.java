package com.example.penpal;

public class Contacts {

    public String displayname, status;

    public Contacts(){}

    public Contacts(String displayname, String status) {
        this.displayname = displayname;
        this.status = status;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String username) {
        this.displayname = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
