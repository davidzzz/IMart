package com.imart.shop.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemDetail implements Parcelable {
    private int qty, subtotal, poin;
    private String nama;

    public ItemDetail() {}

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getQty() {
        return qty;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setPoin(int poin) {
        this.poin = poin;
    }

    public int getPoin() {
        return poin;
    }

    @Override
    public String toString() {
        return getQty() + " item " + getNama() + " = Rp. " + getSubtotal();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(nama);
        out.writeInt(qty);
        out.writeInt(subtotal);
        out.writeInt(poin);
    }

    public static final Parcelable.Creator<ItemDetail> CREATOR = new Parcelable.Creator<ItemDetail>() {
        public ItemDetail createFromParcel(Parcel in) {
            return new ItemDetail(in);
        }

        public ItemDetail[] newArray(int size) {
            return new ItemDetail[size];
        }
    };

    private ItemDetail(Parcel in) {
        nama = in.readString();
        qty = in.readInt();
        subtotal = in.readInt();
        poin = in.readInt();
    }
}
