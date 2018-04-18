package com.imart.shop.model;

public class Voucher {
    private int poin, id, terpakai;
    private String nama, tanggal, gambar, user, kode;

    public Voucher() {}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setPoin(int poin) {
        this.poin = poin;
    }

    public int getPoin() {
        return poin;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getGambar() {
        return gambar;
    }

    public void setTerpakai(int terpakai) {
        this.terpakai = terpakai;
    }

    public int getTerpakai() {
        return terpakai;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
