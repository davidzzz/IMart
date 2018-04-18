package com.imart.shop.model;

public class Wishlist {
    private String nama, produk, merek, waktu;

    public Wishlist() {}

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setProduk(String produk) {
        this.produk = produk;
    }

    public String getProduk() {
        return produk;
    }

    public void setMerek(String merek) {
        this.merek = merek;
    }

    public String getMerek() {
        return merek;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getWaktu() {
        return waktu;
    }
}
