package com.imart.shop.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemOrder implements Parcelable {
    private String id, nama, status, tanggal, catatan, address, telp, gcm_id, idUser;
    private int total, ttlongkir;
    private boolean onTheSpot;
    private ArrayList<ItemDetail> itemDetail = new ArrayList<>();

    public ItemOrder() {}

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id=id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama){
        this.nama=nama;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public ArrayList<ItemDetail> getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(ItemDetail itemDetail) {
        this.itemDetail.add(itemDetail);
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public String getGcm_id() {
        return gcm_id;
    }

    public void setGcm_id(String gcm_id) {
        this.gcm_id = gcm_id;
    }

    public int getTtlongkir() {
        return ttlongkir;
    }

    public void setTtlongkir(int ttlongkir) {
        this.ttlongkir = ttlongkir;
    }

    public boolean isOnTheSpot() {
        return onTheSpot;
    }

    public void setOnTheSpot(boolean onTheSpot) {
        this.onTheSpot = onTheSpot;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(idUser);
        out.writeString(nama);
        out.writeInt(total);
        out.writeString(status);
        out.writeString(tanggal);
        out.writeList(itemDetail);
        out.writeString(catatan);
        out.writeString(address);
        out.writeString(telp);
        out.writeString(gcm_id);
        out.writeInt(ttlongkir);
        out.writeByte((byte)(onTheSpot ? 1 : 0));
    }

    public static final Parcelable.Creator<ItemOrder> CREATOR = new Parcelable.Creator<ItemOrder>() {
        public ItemOrder createFromParcel(Parcel in) {
            return new ItemOrder(in);
        }

        public ItemOrder[] newArray(int size) {
            return new ItemOrder[size];
        }
    };

    private ItemOrder(Parcel in) {
        id = in.readString();
        idUser = in.readString();
        nama = in.readString();
        total = in.readInt();
        status = in.readString();
        tanggal = in.readString();
        itemDetail = in.readArrayList(ItemDetail.class.getClassLoader());
        catatan = in.readString();
        address = in.readString();
        telp = in.readString();
        gcm_id = in.readString();
        ttlongkir = in.readInt();
        onTheSpot = in.readByte() == 1;
    }
}
