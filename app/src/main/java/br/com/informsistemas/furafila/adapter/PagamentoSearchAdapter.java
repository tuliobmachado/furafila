package br.com.informsistemas.furafila.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.interfaces.ItemClickListener;
import br.com.informsistemas.furafila.model.ModoPagamento;

public class PagamentoSearchAdapter extends RecyclerView.Adapter<PagamentoSearchAdapter.MyViewHolder>  {

    private List<ModoPagamento> fList;
    private LayoutInflater fLayoutInflater;
    private Context context;
    private ItemClickListener fItemClickListener;

    public PagamentoSearchAdapter(Context c, List<ModoPagamento> list, ItemClickListener itemClickListener){
        this.fList = list;
        this.context = c;
        this.fLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_pagamento, viewGroup, false);
        return new MyViewHolder(v, fItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.txtDescricao.setText(fList.get(position).descricao);
    }

    @Override
    public int getItemCount() {
        if (fList != null){
            return fList.size();
        }else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtDescricao;
        public ItemClickListener fItemClickListener;

        public MyViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            fItemClickListener = itemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
