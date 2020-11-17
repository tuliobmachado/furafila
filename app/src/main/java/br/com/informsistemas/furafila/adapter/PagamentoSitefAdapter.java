package br.com.informsistemas.furafila.adapter;

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
import br.com.informsistemas.furafila.model.ModoPagamento;

public class PagamentoSitefAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private onPagamentoSitefListener fOnPagamentoSitefListener;
    private List<ModoPagamento> modoItems;
    private Context context;

    private boolean isLoadingAdded = false;

    public PagamentoSitefAdapter(Context context, onPagamentoSitefListener onPagamentoSitefListener){
        this.context = context;
        modoItems = new ArrayList<>();
        fOnPagamentoSitefListener = onPagamentoSitefListener;
    }

    public List<ModoPagamento> getModos(){
        return modoItems;
    }

    public void setFormas(List<ModoPagamento> formaItems){
        this.modoItems = formaItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return getViewHolder(parent, inflater);
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater){
        View v = inflater.inflate(R.layout.recycler_item_pagamento, parent, false);
        return new PagamentoSitefVH(v, fOnPagamentoSitefListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        ModoPagamento modoPagamento = modoItems.get(position);
        PagamentoSitefVH pagamentoStoneVH = (PagamentoSitefVH) holder;

        pagamentoStoneVH.txtDescricao.setText(modoPagamento.descricao);
    }

    @Override
    public int getItemCount(){
        return modoItems == null ? 0 : modoItems.size();
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(ModoPagamento f) {
        modoItems.add(f);
        notifyItemInserted(modoItems.size() - 1);
    }

    public void addAll(List<ModoPagamento> formaList) {
        for (ModoPagamento formaPagamento : formaList) {
            add(formaPagamento);
        }
    }

    public void remove(ModoPagamento f) {
        int position = modoItems.indexOf(f);
        if (position > -1) {
            modoItems.remove(position);
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

    public ModoPagamento getItem(int position) {
        return modoItems.get(position);
    }

   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class PagamentoSitefVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtDescricao;
        private  onPagamentoSitefListener fOnPagamentoSitefListener;

        public PagamentoSitefVH(View itemView, onPagamentoSitefListener onPagamentoSitefListener) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            fOnPagamentoSitefListener = onPagamentoSitefListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fOnPagamentoSitefListener.onClick(getAdapterPosition());
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    public interface onPagamentoSitefListener{
        void onClick(int position);
    }
}
