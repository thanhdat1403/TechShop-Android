package com.manager.shopfastfood.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manager.shopfastfood.Interface.IImageClickListener;
import com.manager.shopfastfood.R;
import com.manager.shopfastfood.model.EventBus.TinhTongEvent;
import com.manager.shopfastfood.model.GioHang;
import com.manager.shopfastfood.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder> {
    Context context;
    List<GioHang> gioHangList;

    public GioHangAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (gioHangList == null || position >= gioHangList.size()) {
            return; // Avoid IndexOutOfBoundsException
        }

        GioHang gioHang = gioHangList.get(position);
        holder.item_giohang_tensp.setText(gioHang.getTensp());
        holder.item_giohang_soluong.setText(String.valueOf(gioHang.getSoluong()));

        Glide.with(context).load(gioHang.getHinhsp()).into(holder.item_giohang_image);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_giohang_gia.setText(decimalFormat.format(gioHang.getGiasp()) + " VND");

        long gia = gioHang.getSoluong() * gioHang.getGiasp();
        holder.item_giohang_giasp2.setText(decimalFormat.format(gia) + " VND");

        // Checkbox logic
        holder.checkBox.setOnCheckedChangeListener(null); // Reset listener to avoid unwanted callbacks
        holder.checkBox.setChecked(Utils.mangmuahang.contains(gioHang));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!Utils.mangmuahang.contains(gioHang)) {
                    Utils.mangmuahang.add(gioHang);
                }
            } else {
                Utils.mangmuahang.remove(gioHang);
            }
            EventBus.getDefault().postSticky(new TinhTongEvent());
        });

        holder.setListener((view, pos, giatri) -> {
            if (giatri == 1) { // Decrease quantity
                if (gioHangList.get(pos).getSoluong() > 1) {
                    int soluongmoi = gioHangList.get(pos).getSoluong() - 1;
                    gioHangList.get(pos).setSoluong(soluongmoi);

                    notifyItemChanged(pos);
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                } else {
                    // Confirm deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng không?");
                    builder.setPositiveButton("Đồng ý", (dialog, which) -> {
                        Utils.manggiohang.remove(gioHangList.get(pos));
                        gioHangList.remove(pos);
                        notifyItemRemoved(pos);
                        EventBus.getDefault().postSticky(new TinhTongEvent());
                    });
                    builder.setNegativeButton("Hủy", (dialogInterface, which) -> dialogInterface.dismiss());
                    builder.show();
                }
            } else if (giatri == 2) { // Increase quantity
                if (gioHangList.get(pos).getSoluong() < 11) {
                    int soluongmoi = gioHangList.get(pos).getSoluong() + 1;
                    gioHangList.get(pos).setSoluong(soluongmoi);

                    notifyItemChanged(pos);
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return gioHangList != null ? gioHangList.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_giohang_image, imgtru, imgcong;
        TextView item_giohang_tensp, item_giohang_gia, item_giohang_soluong, item_giohang_giasp2;
        CheckBox checkBox;
        IImageClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_giohang_image = itemView.findViewById(R.id.item_giohang_image);
            item_giohang_tensp = itemView.findViewById(R.id.item_giohang_tensp);
            item_giohang_gia = itemView.findViewById(R.id.item_giohang_gia);
            item_giohang_soluong = itemView.findViewById(R.id.item_giohang_soluong);
            item_giohang_giasp2 = itemView.findViewById(R.id.item_giohang_giasp2);
            imgtru = itemView.findViewById(R.id.item_giohang_tru);
            imgcong = itemView.findViewById(R.id.item_giohang_cong);
            checkBox = itemView.findViewById(R.id.item_giohang_check);

            imgtru.setOnClickListener(this);
            imgcong.setOnClickListener(this);
        }

        public void setListener(IImageClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int pos = getAdapterPosition();
                if (view == imgtru) {
                    listener.onImageClick(view, pos, 1);
                } else if (view == imgcong) {
                    listener.onImageClick(view, pos, 2);
                }
            }
        }
    }
}
