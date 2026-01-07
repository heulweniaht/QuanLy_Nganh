package com.example.quanly_nganh;

public class Nganh {
    String maNganh;
    String tenNganh;
    String maKhoa;
    String tenKhoa;

    public Nganh(String maNganh, String tenNganh, String maKhoa) {
        this.maKhoa = maKhoa;
        this.maNganh = maNganh;
        this.tenNganh = tenNganh;
    }

    public String getMakhoa() {
        return maKhoa;
    }

    public void setMakhoa(String makhoa) {
        this.maKhoa = makhoa;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public String getTenNganh() {
        return tenNganh;
    }

    public void setTenNganh(String tenNganh) {
        this.tenNganh = tenNganh;
    }

    public String getMaKhoa() {
        return maKhoa;
    }
}
