package com.imart.shop.model;

public class ItemSub {

    private String Category_ID, Category_image, Category_name;

    public ItemSub() {

    }

    public String getId() {
        return Category_ID;
    }

    public void setId(String Category_ID) {
        this.Category_ID = Category_ID;
    }

    public String getName() {
        return Category_name;
    }

    public void setName(String Category_name) {
        this.Category_name = Category_name;
    }

    public String getImage() {
        return Category_image;
    }

    public void setImage(String Category_image) {
        this.Category_image = Category_image;
    }

    @Override
    public String toString() {
        return getName();
    }
}


