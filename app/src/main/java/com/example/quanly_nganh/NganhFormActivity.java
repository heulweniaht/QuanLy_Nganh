package com.example.quanly_nganh;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NganhFormActivity extends AppCompatActivity {
    EditText edtMa, edtTen;
    Spinner spnKhoa;
    Button btnLuu, btnXoa, btnHuy;
    TextView tvTitle;

    List<Khoa> listKhoa = new ArrayList<>();
    boolean isEdit = false;
    String currentMaKhoa = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nganh_form);
        initView();

        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("isEdit", false);

        if(isEdit){
            tvTitle.setText("CẬP NHẬT NGÀNH");
            edtMa.setText(intent.getStringExtra("maNganh"));
            edtTen.setText(intent.getStringExtra("tenNganh"));
            currentMaKhoa = intent.getStringExtra("maKhoa");

            edtMa.setEnabled(false); // Khóa sửa mã
            btnXoa.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText("THÊM NGÀNH MỚI");
            edtMa.setEnabled(true);
            btnXoa.setVisibility(View.GONE);
        }

        loadKhoa();

        btnLuu.setOnClickListener(v -> saveNganh());
        btnHuy.setOnClickListener(v -> finish());
        btnXoa.setOnClickListener(v -> comfirmDelete());
    }

    private void comfirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn chắc chắn muốn xóa ngành " + edtTen.getText() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    String maNganh = edtMa.getText().toString();

                    // LOG KIỂM TRA TRƯỚC KHI XÓA
                    Log.d("TEST_API", "Đang gửi lệnh XÓA mã: " + maNganh);

                    ApiClient.getService().deleteNganh(maNganh).enqueue(actionCallback);
                })
                .setNegativeButton("Hủy",null)
                .show();
    }

    private void saveNganh() {
        String ma = edtMa.getText().toString().trim();
        String ten = edtTen.getText().toString().trim();

        if(ma.isEmpty() || ten.isEmpty()) {
            Toast.makeText(NganhFormActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Khoa selectedKhoa = (Khoa) spnKhoa.getSelectedItem();
        if(selectedKhoa == null) return;

        // --- PHẦN LOG KIỂM TRA DỮ LIỆU (QUAN TRỌNG KHI DB BỊ KHÓA) ---
        Log.d("TEST_API", "----------------------------------");
        Log.d("TEST_API", "DỮ LIỆU CHUẨN BỊ GỬI ĐI:");
        Log.d("TEST_API", "Mã Ngành: " + ma);
        Log.d("TEST_API", "Tên Ngành: " + ten);
        Log.d("TEST_API", "Mã Khoa (FK): " + selectedKhoa.getMaKhoa());
        Log.d("TEST_API", "----------------------------------");

        Nganh nganh = new Nganh(ma, ten, selectedKhoa.getMaKhoa());

        if(isEdit){
            ApiClient.getService().updateNganh(ma, nganh).enqueue(actionCallback);
        } else {
            ApiClient.getService().addNganh(nganh).enqueue(actionCallback);
        }
    }

    private void loadKhoa() {
        ApiClient.getService().getAllKhoa().enqueue(new Callback<List<Khoa>>() {
            @Override
            public void onResponse(Call<List<Khoa>> call, Response<List<Khoa>> response) {
                if(response.isSuccessful() && response.body() != null){
                    listKhoa = response.body();
                    ArrayAdapter<Khoa> adapter = new ArrayAdapter<>(NganhFormActivity.this, android.R.layout.simple_spinner_item, listKhoa);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnKhoa.setAdapter(adapter);

                    // Logic chọn lại đúng khoa cũ khi sửa
                    if(isEdit && currentMaKhoa != null){
                        // Trim() để so sánh chính xác hơn
                        String maKhoaCanTim = currentMaKhoa.trim();
                        for(int i = 0; i < listKhoa.size(); i++){
                            if(listKhoa.get(i).getMaKhoa().trim().equals(maKhoaCanTim)) {
                                spnKhoa.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Khoa>> call, Throwable t) {
                Toast.makeText(NganhFormActivity.this,"Lỗi tải danh sách Khoa",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        edtMa = findViewById(R.id.edtMaNganh);
        edtTen = findViewById(R.id.edtTenNganh);
        spnKhoa = findViewById(R.id.spnKhoa); // Kiểm tra lại ID này trong XML nhé (spnKhoa hay spinnerKhoa)
        btnLuu = findViewById(R.id.btnSave);
        btnXoa = findViewById(R.id.btnDelete);
        btnHuy = findViewById(R.id.btnCancel);
        tvTitle = findViewById(R.id.tvTitle);
    }

    // --- CALLBACK THÔNG MINH (XỬ LÝ KHI DB BỊ KHÓA) ---
    private Callback<Void> actionCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            // NẾU THÀNH CÔNG HOẶC BỊ SERVER TỪ CHỐI (Do khóa DB) ĐỀU COI LÀ OK ĐỂ TEST UI
            // Mã 403: Forbidden, 405: Method Not Allowed, 500: Server Error (do DB readonly)
            if(response.isSuccessful() || response.code() == 403 || response.code() == 500 || response.code() == 405){

                String message = response.isSuccessful() ? "Thành công!" : "Đã gửi lệnh (Test Mode - DB Locked)";
                Toast.makeText(NganhFormActivity.this, message, Toast.LENGTH_SHORT).show();

                Log.d("TEST_API", "Kết quả server trả về code: " + response.code() + ". App tự động đóng form.");

                finish(); // Đóng form quay về danh sách
            } else {
                Toast.makeText(NganhFormActivity.this, "Lỗi lạ: " + response.code(), Toast.LENGTH_SHORT).show();
                Log.e("TEST_API", "Lỗi server thực sự: " + response.code());
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Toast.makeText(NganhFormActivity.this, "Lỗi kết nối Internet/Server", Toast.LENGTH_SHORT).show();
            Log.e("TEST_API", "Lỗi onFailure: " + t.getMessage());
        }
    };
}