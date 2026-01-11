package com.example.quanly_nganh;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/Khoa")
    Call<List<Khoa>> getAllKhoa();

    @GET("api/Nganh")
    Call<List<Nganh>> getAllNganh();

    @POST("api/Nganh")
    Call<Void> addNganh(@Body Nganh nganh);

    @PUT("api/Nganh/{id}")
    Call<Void> updateNganh(@Path("id") String maNganh, @Body Nganh nganh);

    @DELETE("api/Nganh/{id}")
    Call<Void> deleteNganh(@Path("id") String maNganh);
}
