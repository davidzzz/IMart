package com.imart.shop.model;

public class FlashDeal {
    private String id, gambar, nama, harga, hargaAsli, satuan;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getGambar() {
        return gambar;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getHarga() {
        return harga;
    }

    public void setHargaAsli(String hargaAsli) {
        this.hargaAsli = hargaAsli;
    }

    public String getHargaAsli() {
        return hargaAsli;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }
}
