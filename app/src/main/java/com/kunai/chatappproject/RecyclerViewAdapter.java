package com.kunai.chatappproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.BilgilerViewHolder> {
    ArrayList<String> kullanicilar;
    RecyclerViewClick recyclerViewClick;

    public RecyclerViewAdapter(ArrayList<String> kullanicilar,RecyclerViewClick recyclerViewClick) {
        this.kullanicilar = kullanicilar;
        this.recyclerViewClick = recyclerViewClick;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.BilgilerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclertasarim,parent,false);
        return new BilgilerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.BilgilerViewHolder holder, int position) {
            holder.chatNickName.setText(kullanicilar.get(position));

    }

    @Override
    public int getItemCount() {
        return kullanicilar.size();
    }

    public class BilgilerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView chatNickName;
        CircleImageView chatPhoto;
        public BilgilerViewHolder(@NonNull View itemView) {
            super(itemView);
            chatNickName = itemView.findViewById(R.id.chatNick);
            chatPhoto = itemView.findViewById(R.id.chatPhoto);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerViewClick.onClick(view,getAdapterPosition());
        }
    }
    public interface RecyclerViewClick{
        void onClick(View v, int position);
    }
}
