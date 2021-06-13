package com.kunai.chatappproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerChat extends RecyclerView.Adapter<RecyclerChat.MyHolder> {

    List<MesajModel> list ;
    Context context;
    Activity activity;
    String userName;
    Boolean state ;
    int v1=1 , v2 = 2;

    public RecyclerChat(List<MesajModel> list, Context context, Activity activity, String userName) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.userName = userName;
        state = false;
    }

    @NonNull
    @Override
    public RecyclerChat.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == v1){
            view = LayoutInflater.from(context).inflate(R.layout.send,parent,false);
            return new MyHolder(view);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.yollayan,parent,false);

            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerChat.MyHolder holder, int position) {
        holder.textKullanicilar.setText(list.get(position).getMesaj());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView textKullanicilar;
        ImageView imageDemo;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            if (state == true){
                textKullanicilar = itemView.findViewById(R.id.sendtextView);


            }else
                textKullanicilar = itemView.findViewById(R.id.recitextView);





        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getAlici().equals(userName)){
            state = true;
            return v1;
        }else
            state = false;
        return  v2;
    }
}
