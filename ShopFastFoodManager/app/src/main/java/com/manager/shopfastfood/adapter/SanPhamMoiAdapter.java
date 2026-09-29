package com.manager.shopfastfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manager.shopfastfood.Interface.ItemClickListener;
import com.manager.shopfastfood.R;
import com.manager.shopfastfood.activity.ChiTietActivity;
import com.manager.shopfastfood.model.EventBus.SuaXoaEvent;
import com.manager.shopfastfood.model.SanPhamMoi;
import com.manager.shopfastfood.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.MyViewHolder>{
    Context context;
    List<SanPhamMoi> array;

    public SanPhamMoiAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPhamMoi sanPhamMoi = array.get(position);
        holder.txtten.setText(sanPhamMoi.getTensp());
//        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
//        holder.txtgia.setText("Giá: " + decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasp())) + "VND");
        String giasp = sanPhamMoi.getGiasp(); // Giá sản phẩm
        try {
            // Xử lý chuỗi giá để loại bỏ ký tự không hợp lệ (ví dụ: "VND")
            if (giasp != null && !giasp.isEmpty()) {
                String giaClean = giasp.replace("VND", "").trim(); // Loại bỏ "VND"
                double giaDouble = Double.parseDouble(giaClean);  // Chuyển đổi sang double
                DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                holder.txtgia.setText("Giá: " + decimalFormat.format(giaDouble) + " VND");
            } else {
                holder.txtgia.setText("Giá: N/A"); // Giá trị mặc định nếu giá rỗng
            }
        } catch (NumberFormatException e) {
            holder.txtgia.setText("Giá: N/A"); // Giá trị mặc định nếu lỗi định dạng
            e.printStackTrace(); // In lỗi ra logcat để kiểm tra
        }

        //Nếu không muốn lấy hình ảnh trong file thì có thể thêm bằng Link http
        if (sanPhamMoi.getHinhanh().contains("http")){
            Glide.with(context).load(sanPhamMoi.getHinhanh()).into(holder.imghinhanh);
        }else {//ngược lại lấy trong file thì ảnh up nên database sẽ có đuôi .jpg
            String hinh = Utils.BASE_URL+"images/"+sanPhamMoi.getHinhanh();
            Glide.with(context).load(hinh).into(holder.imghinhanh);
        }

        //Bắt sự kiện cho màn hình chính khi click vào thì sẽ ra luôn chi tiết sản phẩm
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if (!isLongClick){
                    //click
                    Intent intent = new Intent(context, ChiTietActivity.class);
                    intent.putExtra("chitiet",sanPhamMoi);//chuyền dữ liệu sang màn hình chi tiết sản phẩm
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else {
                    EventBus.getDefault().postSticky(new SuaXoaEvent(sanPhamMoi));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        TextView txtgia, txtten;
        ImageView imghinhanh;
        private ItemClickListener itemClickListener;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtgia = itemView.findViewById(R.id.itemsp_gia);
            txtten = itemView.findViewById(R.id.itemsp_ten);
            imghinhanh = itemView.findViewById(R.id.itemsp_image);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(),false);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            contextMenu.add(0,0,getAdapterPosition(),"Sửa");
            contextMenu.add(0,1,getAdapterPosition(),"Xóa");
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(),true);
            return false;
        }
    }
}
