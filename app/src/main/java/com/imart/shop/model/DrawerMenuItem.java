package com.imart.shop.model;

/**
 * Created by liau on 5/10/2016.
 */
public class DrawerMenuItem {
    private int i;
    private String mText;
    private int mIcon;

    public DrawerMenuItem(int i, String mText, int mIcon) {
        // TODO Auto-generated constructor stub
        this.i = i;
        this.mText = mText;
        this.mIcon = mIcon;
    }

    public int getId() {
        return i;
    }

    public void setId(int i) {
        this.i = i;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int mIcon) {
        this.mIcon = mIcon;
    }
}

