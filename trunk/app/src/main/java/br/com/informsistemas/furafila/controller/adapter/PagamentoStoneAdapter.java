package br.com.informsistemas.furafila.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.pojo.FormaPagamento;

public class PagamentoStoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private onPagamentoStoneListener fOnPagamentoStoneListener;
    private List<FormaPagamento> formaItems;
    private Context context;

    private boolean isLoadingAdded = false;

    public PagamentoStoneAdapter(Context context, onPagamentoStoneListener onPagamentoStoneListener){
        this.context = context;
        formaItems = new ArrayList<>();
        fOnPagamentoStoneListener = onPagamentoStoneListener;
    }

    public List<FormaPagamento> getFormas(){
        return formaItems;
    }

    public void setFormas(List<FormaPagamento> formaItems){
        this.formaItems = formaItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return getViewHolder(parent, inflater);
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater){
        View v = inflater.inflate(R.layout.recycler_item_pagamento, parent, false);
        return new PagamentoStoneVH(v, fOnPagamentoStoneListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        FormaPagamento formaPagamento = formaItems.get(position);
        PagamentoStoneVH pagamentoStoneVH = (PagamentoStoneVH) holder;

        pagamentoStoneVH.txtDescricao.setText(formaPagamento.descricao);
    }

    @Override
    public int getItemCount(){
        return formaItems == null ? 0 : formaItems.size();
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(FormaPagamento f) {
        formaItems.add(f);
        notifyItemInserted(formaItems.size() - 1);
    }

    public void addAll(List<FormaPagamento> formaList) {
        for (FormaPagamento formaPagamento : formaList) {
            add(formaPagamento);
        }
    }

    public void remove(FormaPagamento f) {
        int position = formaItems.indexOf(f);
        if (position > -1) {
            formaItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public FormaPagamento getItem(int position) {
        return formaItems.get(position);
    }

   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class PagamentoStoneVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtDescricao;
        private  onPagamentoStoneListener fOnPagamentoStoneListener;

        public PagamentoStoneVH(View itemView, onPagamentoStoneListener onPagamentoStoneListener) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            fOnPagamentoStoneListener = onPagamentoStoneListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fOnPagamentoStoneListener.onClick(getAdapterPosition());
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    public interface onPagamentoStoneListener{
        void onClick(int position);
    }
}
