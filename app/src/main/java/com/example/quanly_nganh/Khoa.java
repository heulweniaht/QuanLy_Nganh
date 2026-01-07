package com.example.quanly_nganh;

import java.io.Serializable;

public class Khoa implements Serializable {
    String maKhoa;
    String tenKhoa;

    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    @Override
    public String toString(){
        return tenKhoa;
    }

}
