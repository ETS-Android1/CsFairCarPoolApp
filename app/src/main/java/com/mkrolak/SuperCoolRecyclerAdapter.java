package com.mkrolak;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SuperCoolRecyclerAdapter<E> extends RecyclerView.Adapter<SuperCoolRecyclerAdapter.MyViewHolder> {
    private ArrayList<E> list;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder<E> extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public E textView;
        public MyViewHolder(E v) {
            super((View)v);
            textView = v;
        }
    }



}
