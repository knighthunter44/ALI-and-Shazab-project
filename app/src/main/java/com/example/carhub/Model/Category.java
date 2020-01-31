package com.example.carhub.Model;

import androidx.annotation.NonNull;

public class Category {
    private String Name;
    private String Image;

    public Category()
    {

    }
    public Category(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    @Override
    public String toString() {
        return Name;
    }
}