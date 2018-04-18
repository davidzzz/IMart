package com.imart.shop.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Cart implements Parcelable {
	private String idMenu, namaMenu, gambar, satuan;
    private int harga;
    private int total = 0;
    private int quantity = 0;
    private int poin = 0;
    
    public Cart() {
    	
    }
    
    public String getIdMenu(){
        return idMenu;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }
    
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total){
    	this.total=total;
    }

    public int getPoin() {
        return poin;
    }

    public void setPoin(int poin) {
        this.poin = poin;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getGambar() {
        return gambar;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(idMenu);
        out.writeString(namaMenu);
        out.writeInt(harga);
        out.writeInt(total);
        out.writeInt(quantity);
        out.writeInt(poin);
        out.writeString(gambar);
        out.writeString(satuan);
    }

    public static final Parcelable.Creator<Cart> CREATOR = new Parcelable.Creator<Cart>() {
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    private Cart(Parcel in) {
        idMenu = in.readString();
        namaMenu = in.readString();
        harga = in.readInt();
        total = in.readInt();
        quantity = in.readInt();
        poin = in.readInt();
        gambar = in.readString();
        satuan = in.readString();
    }

}
