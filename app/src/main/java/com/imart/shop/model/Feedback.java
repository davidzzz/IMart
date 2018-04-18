package com.imart.shop.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Feedback implements Parcelable {
    private String nama, komentar, waktu;
    private float rating1, rating2, rating3, rating4;

    public Feedback() {}

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setRating1(float rating1) {
        this.rating1 = rating1;
    }

    public float getRating1() {
        return rating1;
    }

    public void setRating2(float rating2) {
        this.rating2 = rating2;
    }

    public float getRating2() {
        return rating2;
    }

    public void setRating3(float rating3) {
        this.rating3 = rating3;
    }

    public float getRating3() {
        return rating3;
    }

    public void setRating4(float rating4) {
        this.rating4 = rating4;
    }

    public float getRating4() {
        return rating4;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getWaktu() {
        return waktu;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(nama);
        out.writeFloat(rating1);
        out.writeFloat(rating2);
        out.writeFloat(rating3);
        out.writeFloat(rating4);
        out.writeString(komentar);
        out.writeString(waktu);
    }

    public static final Parcelable.Creator<Feedback> CREATOR = new Parcelable.Creator<Feedback>() {
        public Feedback createFromParcel(Parcel in) {
            return new Feedback(in);
        }

        public Feedback[] newArray(int size) {
            return new Feedback[size];
        }
    };

    private Feedback(Parcel in) {
        nama = in.readString();
        rating1 = in.readFloat();
        rating2 = in.readFloat();
        rating3 = in.readFloat();
        rating4 = in.readFloat();
        komentar = in.readString();
        waktu = in.readString();
    }
}
