package com.example.carhub.Model;

public class Cars {
  private String Image ,Name , Model,Price,BodyColor,MenuId,Description;
    private String lDescription;
    private String lUnfrontPrice;
    private String mInstallment;
    private String pInstallment;


    public Cars() {
    }

    public Cars(String image, String name, String model, String price, String bodyColor, String menuId,
                String description, String lDescription, String lUnfrontPrice, String mInstallment, String pInstallment) {
        Image = image;
        Name = name;
        Model = model;
        Price = price;
        BodyColor = bodyColor;
        MenuId = menuId;
        Description = description;
        this.lDescription = lDescription;
        this.lUnfrontPrice = lUnfrontPrice;
        this.mInstallment = mInstallment;
        this.pInstallment = pInstallment;
    }

    public String getlDescription() {
        return lDescription;
    }

    public void setlDescription(String lDescription) {
        this.lDescription = lDescription;
    }

    public String getlUnfrontPrice() {
        return lUnfrontPrice;
    }

    public void setlUnfrontPrice(String lUnfrontPrice) {
        this.lUnfrontPrice = lUnfrontPrice;
    }

    public String getmInstallment() {
        return mInstallment;
    }

    public void setmInstallment(String mInstallment) {
        this.mInstallment = mInstallment;
    }

    public String getpInstallment() {
        return pInstallment;
    }

    public void setpInstallment(String pInstallment) {
        this.pInstallment = pInstallment;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getBodyColor() {
        return BodyColor;
    }

    public void setBodyColor(String bodyColor) {
        BodyColor = bodyColor;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
