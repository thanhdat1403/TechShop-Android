package com.example.shopfastfood.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopfastfood.R;
import com.example.shopfastfood.model.Item;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

public class ChitietAdapter extends RecyclerView.Adapter<ChitietAdapter.MyViewHolder> {
    Context context;
    List<Item> itemList;

    public ChitietAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public ChitietAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chitiet, parent, false);
        return new MyViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        Item item = itemList.get(position);
//        Log.d("Adapter", "Item tại vị trí " + position + ": " + item.toString());
//        holder.txtten.setText(item.getTensp());
//        holder.txtsoluong.setText("Số lượng: " + item.getSoluong() + "");
//        Log.d("Item Debug", "Số lượng: " + item.getSoluong());
//        holder.txtgia.setText("Giá: " + item.getGia() + " VND");
//        Glide.with(context).load(item.getHinhanh()).into(holder.imagechitiet);
//    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Kiểm tra nếu số lượng hợp lệ (>= 0), nếu không thì gán giá trị mặc định là 0
        int soluong = item.getSoluong() >= 0 ? item.getSoluong() : 0;

        // Log kiểm tra
        Log.d("Adapter", "Số lượng sản phẩm tại vị trí " + position + ": " + soluong);

        // Hiển thị tên sản phẩm, số lượng và giá lên giao diện
        holder.txtten.setText(item.getTensp());
        holder.txtsoluong.setText("Số lượng: " + item.getSoluong() + "");
        holder.txtgia.setText("Giá: " + item.getGia() + " VND");

        // Tải ảnh sản phẩm bằng Glide
        Glide.with(context).load(item.getHinhanh()).into(holder.imagechitiet);
    }




    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{
        ImageView imagechitiet;
        TextView txtten, txtsoluong, txtgia;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagechitiet = itemView.findViewById(R.id.item_imagechitiet);
            txtten = itemView.findViewById(R.id.item_tenspchitiet);
            txtsoluong = itemView.findViewById(R.id.item_soluongchitiet);
            txtgia = itemView.findViewById(R.id.item_giachitiet);
        }
    }
}
