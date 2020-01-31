package com.example.carhub.Model;

import androidx.annotation.NonNull;

public class CategorySpinner {
    private String id;
    private String name;

    public CategorySpinner() {
    }

    public CategorySpinner(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
