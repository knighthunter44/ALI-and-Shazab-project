package com.example.carhub.Model;

public class C_newcars {
    private String cname;
    private String cimage;


    public C_newcars() {
    }

    public C_newcars(String cname, String cimage) {
        this.cname = cname;
        this.cimage = cimage;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCimage() {
        return cimage;
    }

    public void setCimage(String cimage) {
        this.cimage = cimage;
    }
}
