package com.codigopostal.svjchrysler.codigopostal.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codigopostal.svjchrysler.codigopostal.Models.Ubication;
import com.codigopostal.svjchrysler.codigopostal.R;

import java.util.LinkedList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    LinkedList<Ubication> listUbications;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCalle, tvCodigoPostal, tvId;
        public ImageView imgHouse;
        public ViewHolder(View v) {
            super(v);
            tvCalle = (TextView)v.findViewById(R.id.tvCalle);
            tvCodigoPostal = (TextView)v.findViewById(R.id.tvCodigoPostal);
            tvId = (TextView)v.findViewById(R.id.tvId);

            imgHouse = (ImageView)v.findViewById(R.id.imgHouse);
        }
    }

    public ListAdapter(LinkedList<Ubication> listUbications) {
        this.listUbications = listUbications;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_location, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvId.setText(listUbications.get(position).id);
        holder.tvCalle.setText(listUbications.get(position).calle);
        holder.tvCodigoPostal.setText(listUbications.get(position).codigoPostal);

        String imageEncode = listUbications.get(position).imagen;
        byte[] decodeString = Base64.decode(imageEncode, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        holder.imgHouse.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return listUbications.size();
    }
}
