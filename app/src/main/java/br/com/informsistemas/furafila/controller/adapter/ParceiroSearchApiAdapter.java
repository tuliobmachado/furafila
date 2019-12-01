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
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.utils.CPFCNPJMask;

public class ParceiroSearchApiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private onParceiroSearchApiListener fOnParceiroSearchApiListener;
    private List<Parceiro> parceiroItems;
    private Context context;

    private boolean isLoadingAdded = false;

    public ParceiroSearchApiAdapter(Context context, onParceiroSearchApiListener onParceiroSearchApiListener){
        this.context = context;
        parceiroItems = new ArrayList<>();
        fOnParceiroSearchApiListener = onParceiroSearchApiListener;
    }

    public List<Parceiro> getParceiros(){
        return parceiroItems;
    }

    public void setParceiros(List<Parceiro> parceiroItems){
        this.parceiroItems = parceiroItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v);
                break;
        }

        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater){
        View v = inflater.inflate(R.layout.recycler_item_parceiro, parent, false);
        return new ParceiroVH(v, fOnParceiroSearchApiListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        Parceiro parceiro = parceiroItems.get(position);

        switch (getItemViewType(position)){
            case ITEM:
                final ParceiroVH parceiroVH = (ParceiroVH) holder;

                parceiroVH.txtCodigo.setText(parceiro.codigoparceiro);
                parceiroVH.txtDescricao.setText(parceiro.descricao);
                parceiroVH.txtNomeFantasia.setText(parceiro.nomefantasia);
                parceiroVH.txtCPFCGC.setText(CPFCNPJMask.getMask(parceiro.cpfcgc));
                break;
            case LOADING:
                break;
        }

    }

    @Override
    public int getItemCount(){
        return parceiroItems == null ? 0 : parceiroItems.size();
    }

    @Override
    public int getItemViewType(int position){
        return (position == parceiroItems.size() -1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Parceiro p) {
        parceiroItems.add(p);
        notifyItemInserted(parceiroItems.size() - 1);
    }

    public void addAll(List<Parceiro> parceiroList) {
        for (Parceiro parceiro : parceiroList) {
            add(parceiro);
        }
    }

    public void remove(Parceiro p) {
        int position = parceiroItems.indexOf(p);
        if (position > -1) {
            parceiroItems.remove(position);
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

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Parceiro());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = parceiroItems.size() - 1;
        Parceiro parceiro = getItem(position);

        if (parceiro != null) {
            parceiroItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Parceiro getItem(int position) {
        return parceiroItems.get(position);
    }

   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class ParceiroVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtCodigo;
        private TextView txtDescricao;
        private TextView txtNomeFantasia;
        private TextView txtCPFCGC;
        private  onParceiroSearchApiListener fOnParceiroSearchApiListener;

        public ParceiroVH(View itemView, onParceiroSearchApiListener onParceiroSearchApiListener) {
            super(itemView);

            txtCodigo = itemView.findViewById(R.id.txt_codigo_parceiro);
            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            txtNomeFantasia = itemView.findViewById(R.id.txt_nome_fantasia);
            txtCPFCGC = itemView.findViewById(R.id.txt_cpfcgc);
            fOnParceiroSearchApiListener = onParceiroSearchApiListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fOnParceiroSearchApiListener.onClick(getAdapterPosition());
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    public interface onParceiroSearchApiListener{
        void onClick(int position);
    }


}
