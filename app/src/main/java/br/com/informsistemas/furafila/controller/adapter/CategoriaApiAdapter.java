package br.com.informsistemas.furafila.controller.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.pojo.Categoria;

public class CategoriaApiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private onCategoriaApiListener fOnCategoriaApiListener;
    private List<Categoria> categoriaItems;
    private Context context;
    private Categoria categoriaSelecionada;

    private boolean isLoadingAdded = false;

    public CategoriaApiAdapter(Context context, onCategoriaApiListener onCategoriaApiListener){
        this.context = context;
        categoriaItems = new ArrayList<>();
        fOnCategoriaApiListener = onCategoriaApiListener;
    }

    public List<Categoria> getCategorias(){
        return categoriaItems;
    }

    public void setCategorias(List<Categoria> categoriaItems){
        this.categoriaItems = categoriaItems;
    }

    public Categoria getCategoriaSelecionada(){
        return categoriaSelecionada;
    }

    public void setCategoriaSelecionada(Categoria c){
        categoriaSelecionada = c;
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
        View v = inflater.inflate(R.layout.recycler_item_categoria, parent, false);
        return new CategoriaVH(v, fOnCategoriaApiListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        Categoria categoria = categoriaItems.get(position);

        switch (getItemViewType(position)){
            case ITEM:
                final CategoriaVH categoriaVH = (CategoriaVH) holder;
                boolean selecionado = false;

                categoriaVH.txtDescricao.setText(categoria.descricao);

                if (categoriaSelecionada != null) {
                    if (categoriaSelecionada.codigogrupo.equals(categoria.codigogrupo)) {
                        selecionado = true;
                    }
                }

                if (selecionado){
                    categoriaVH.cardView.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.cardSelecionado)));
                }else{
                    categoriaVH.cardView.setForeground(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
                }
                break;
            case LOADING:
                break;
        }

    }

    @Override
    public int getItemCount(){
        return categoriaItems == null ? 0 : categoriaItems.size();
    }

    @Override
    public int getItemViewType(int position){
        return (position == categoriaItems.size() -1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Categoria c) {
        categoriaItems.add(c);
        notifyItemInserted(categoriaItems.size() - 1);
    }

    public void addAll(List<Categoria> categoriaList) {
        for (Categoria categoria : categoriaList) {
            add(categoria);
        }
    }

    public void remove(Categoria c) {
        int position = categoriaItems.indexOf(c);
        if (position > -1) {
            categoriaItems.remove(position);
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
        add(new Categoria());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = categoriaItems.size() - 1;
        Categoria categoria = getItem(position);

        if (categoria != null) {
            categoriaItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Categoria getItem(int position) {
        return categoriaItems.get(position);
    }

   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class CategoriaVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtDescricao;
        private CardView cardView;
        private onCategoriaApiListener fOnCategoriaApiListener;

        public CategoriaVH(View itemView, onCategoriaApiListener onCategoriaApiListener) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            cardView = itemView.findViewById(R.id.card_categoria);
            fOnCategoriaApiListener = onCategoriaApiListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fOnCategoriaApiListener.onClick(getAdapterPosition());
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    public interface onCategoriaApiListener{
        void onClick(int position);
    }
}
