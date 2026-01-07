package com.example.quanly_nganh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView rcvNganh;
    FloatingActionButton fabAdd;
    NganhAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rcvNganh = findViewById(R.id.rcvNganh);
        fabAdd = findViewById(R.id.fabAdd);
        rcvNganh.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NganhFormActivity.class);
            intent.putExtra("isEdit", false);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        loadData();
    }

    private void loadData() {
        // BƯỚC 1: Lấy danh sách Khoa
        ApiClient.getService().getAllKhoa().enqueue(new Callback<List<Khoa>>() {
            @Override
            public void onResponse(Call<List<Khoa>> call, Response<List<Khoa>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Khoa> listKhoa = response.body();

                    // Tạo Map và QUAN TRỌNG: Cắt khoảng trắng (trim) và chuyển về chữ hoa
                    Map<String, String> mapKhoa = new HashMap<>();
                    for (Khoa k : listKhoa) {
                        if (k.getMaKhoa() != null) {
                            // key: MaKhoa đã làm sạch
                            String key = k.getMaKhoa().trim().toUpperCase();
                            mapKhoa.put(key, k.getTenKhoa());

                            // Log kiểm tra xem Khoa lấy về có đúng không
                            android.util.Log.d("DEBUG_MAP", "Đã thêm vào từ điển: Key='" + key + "' -> Value='" + k.getTenKhoa() + "'");
                        }
                    }

                    // BƯỚC 2: Lấy Ngành
                    loadNganhVaMapTenKhoa(mapKhoa);
                }
            }

            @Override
            public void onFailure(Call<List<Khoa>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối Khoa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNganhVaMapTenKhoa(Map<String, String> mapKhoa) {
        ApiClient.getService().getAllNganh().enqueue(new Callback<List<Nganh>>() {
            @Override
            public void onResponse(Call<List<Nganh>> call, Response<List<Nganh>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Nganh> listNganh = response.body();

                    // BƯỚC 3: Map dữ liệu (Cũng phải trim mã bên Ngành)
                    for (Nganh nganh : listNganh) {
                        String maKhoaGoc = nganh.getMaKhoa();

                        if (maKhoaGoc != null) {
                            // Làm sạch mã tìm kiếm y hệt lúc tạo Map
                            String maKhoaTimKiem = maKhoaGoc.trim().toUpperCase();

                            if (mapKhoa.containsKey(maKhoaTimKiem)) {
                                String tenTimDuoc = mapKhoa.get(maKhoaTimKiem);
                                nganh.setTenKhoa(tenTimDuoc);
                            } else {
                                // Nếu không tìm thấy, In lỗi ra Logcat để xem tại sao
                                android.util.Log.e("DEBUG_MAP", "KHÔNG KHỚP: Ngành '" + nganh.getTenNganh()
                                        + "' có mã khoa gốc là '" + maKhoaGoc + "' (đã trim: '" + maKhoaTimKiem + "') không tìm thấy trong Map.");
                                nganh.setTenKhoa("Không xác định (" + maKhoaGoc + ")");
                            }
                        } else {
                            nganh.setTenKhoa("Lỗi dữ liệu (Mã null)");
                        }
                    }

                    // Đổ vào Adapter
                    adapter = new NganhAdapter(MainActivity.this, listNganh);
                    rcvNganh.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Nganh>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối Ngành", Toast.LENGTH_SHORT).show();
            }
        });
    }
}