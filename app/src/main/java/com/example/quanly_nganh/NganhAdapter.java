package com.example.quanly_nganh;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NganhAdapter extends RecyclerView.Adapter<NganhAdapter.NganhViewHolder> {
    private Context context;
    private List<Nganh> listNganh;

    public NganhAdapter(Context context, List<Nganh> listNganh) {
        this.context = context;
        this.listNganh = listNganh;
    }

    @NonNull
    @Override
    public NganhViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_nganh, parent, false);
        return new NganhViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NganhViewHolder holder, int position){
        Nganh nganh = listNganh.get(position);
        holder.tvTen.setText(nganh.getTenNganh());
        //Hiển thị Mã Ngành và tên Khoa (Nếu null thì trống)
        String tenKhoa = (nganh.getTenKhoa() != null) ? nganh.getTenKhoa() : nganh.getMakhoa();
        holder.tvMa.setText("Mã: " + nganh.getMaNganh() + " | Khoa: " + tenKhoa);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NganhFormActivity.class);
            intent.putExtra("isEdit", true);
            intent.putExtra("maNganh", nganh.getMaNganh());
            intent.putExtra("tenNganh", nganh.getTenNganh());
            intent.putExtra("maKhoa", nganh.getMakhoa());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount(){
        return listNganh.size();
    }
    public class NganhViewHolder extends RecyclerView.ViewHolder{
        TextView tvTen, tvMa;
        public NganhViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTenNganh);
            tvMa = itemView.findViewById(R.id.tvMaNganh);
        }
    }
}
