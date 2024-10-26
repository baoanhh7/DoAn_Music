package com.example.doan_music.adapter.thuvien;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_music.R;
import com.example.doan_music.database.ConnectionClass;
import com.example.doan_music.m_interface.OnItemClickListener;
import com.example.doan_music.model.ThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ThuVienAdapter extends RecyclerView.Adapter<ThuVienAdapter.ViewHolder> implements Filterable {
    //khai báo biến
    Fragment context;
    ArrayList<ThuVien> arr, arr1;
    private OnItemClickListener onItemClickListener;

    public ThuVienAdapter(Fragment context, ArrayList<ThuVien> arr) {
        this.context = context;
        this.arr = arr;
        this.arr1 = arr;
    }
    public void removeItem(int position) {
        String tensp = arr.get(position).getTensp();
        Log.d("Tên Playlist", tensp); // Kiểm tra tên trước khi xóa
        DeleteDataPlaylist(position);
        DeleteDataArtist(position);
        notifyItemRemoved(position); // Thông báo RecyclerView cập nhật
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.items_thuvien, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThuVien tv = arr.get(position);
        byte[] hinhAlbumByteArray = tv.getHinh();
        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAlbumByteArray, 0, hinhAlbumByteArray.length);
        holder.img.setImageBitmap(bitmap);
        holder.txtTen.setText(tv.getTensp());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(arr.get(position).getTensp());
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()) {
                    arr = arr1;
                } else {
                    ArrayList<ThuVien> arrayList = new ArrayList<>();
                    for (ThuVien thuVien : arr1) {
                        if (thuVien.getTensp().toLowerCase().contains(strSearch.toLowerCase())) {
                            arrayList.add(thuVien);
                        }
                    }
                    arr = arrayList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = arr;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arr = (ArrayList<ThuVien>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtTen, txtND;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_thuvien);
            txtTen = itemView.findViewById(R.id.txtTen_thuvien);
        }
    }
    private void DeleteDataPlaylist(int position) {
        // Tạo CallableStatement để gọi stored procedure
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();
        Log.e("SQL", "DeletePlaylistByName");
        if (connection != null) {
            try {
                String query = "EXEC DeletePlaylistByName ?"; // Thay thế tên stored procedure và tham số tương ứng
                PreparedStatement stmt = connection.prepareStatement(query);
                Log.d("SQL", query);
                stmt.setString(1, arr.get(position).getTensp());
                // Thực thi câu lệnh và lấy số lượng bản ghi đã bị xóa
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    arr.remove(position);
                    Log.d("SQL", "Playlist deleted successfully.");
                } else {
                    Log.d("SQL", "No Playlist found with that name.");
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }
    private void DeleteDataArtist(int position) {
        // Tạo CallableStatement để gọi stored procedure
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();
        Log.e("SQL", "DeleteUserArtistByName");
        if (connection != null) {
            try {
                String query = "EXEC DeleteUserArtistByName ?"; // Thay thế tên stored procedure và tham số tương ứng
                //String query = "DELETE ua FROM User_Artist ua JOIN Artist a ON ua.ArtistID = a.ArtistID WHERE a.ArtistName = ?";

                PreparedStatement stmt = connection.prepareStatement(query);
                Log.d("SQL", query);
                stmt.setString(1, arr.get(position).getTensp());
                // Thực thi câu lệnh và lấy số lượng bản ghi đã bị xóa
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    arr.remove(position);
                    Log.d("SQL", "Artist deleted successfully.");
                } else {
                    Log.d("SQL", "No Artist found with that name.");
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }
}
