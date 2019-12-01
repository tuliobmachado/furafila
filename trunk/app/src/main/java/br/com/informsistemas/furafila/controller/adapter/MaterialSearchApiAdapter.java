package br.com.informsistemas.furafila.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Material;

public class MaterialSearchApiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private onMaterialSearchApiListener fOnMaterialSearchApiListener;
    private List<Material> materialItems;
    private List<Material> itemsSelecionados;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean excluindo = false;

    public MaterialSearchApiAdapter(Context context, onMaterialSearchApiListener onMaterialSearchApiListener){
        this.context = context;
        materialItems = new ArrayList<>();
        itemsSelecionados = new ArrayList<>();
        fOnMaterialSearchApiListener = onMaterialSearchApiListener;
    }

    public List<Material> getMateriais(){
        return materialItems;
    }

    public void setMateriais(List<Material> materialItems){
        this.materialItems = materialItems;
    }

    public List<Material> getMaterialSelecionados(){ return itemsSelecionados; }

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
        View v = inflater.inflate(R.layout.recycler_item_material, parent, false);
        return new MaterialVH(v, fOnMaterialSearchApiListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        Material material = materialItems.get(position);

        switch (getItemViewType(position)){
            case ITEM:
                final MaterialVH materialVH = (MaterialVH) holder;

                materialVH.txtDescricao.setText(material.descricao);
                materialVH.txtCusto.setText("R$ "+ Misc.formatMoeda(material.precovenda1));
                materialVH.txtSaldo.setText("");

                if (materialItems.get(position).quantidade >= 1){
                    materialVH.imgExcluir.setImageResource(R.drawable.ic_remove_red_24dp);
                    materialVH.imgExcluir.setVisibility(View.VISIBLE);

                    if (materialItems.get(position).quantidade == 1){
                        materialVH.imgExcluir.setVisibility(View.VISIBLE);
                    }

                    if (materialItems.get(position).quantidade >= 2){
                        materialVH.txtQuantidade.setText(String.format("%.0f", materialItems.get(position).quantidade));
                    }else{
                        materialVH.txtQuantidade.setText("");
                    }

                    materialVH.imgSelecionado.setImageResource(R.drawable.ic_add_circle_adicionado_24dp);
                }else{
                    materialVH.imgExcluir.setVisibility(View.INVISIBLE);
                    materialVH.txtQuantidade.setText("");
                    materialVH.imgSelecionado.setImageResource(R.drawable.ic_add_circle_gray_24dp);
                }

                materialVH.imgSelecionado.startAnimation(Misc.getRotateAnimation(excluindo));
                break;
            case LOADING:
                break;
        }

    }

    @Override
    public int getItemCount(){
        return materialItems == null ? 0 : materialItems.size();
    }

    @Override
    public int getItemViewType(int position){
        return (position == materialItems.size() -1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Material m) {
        materialItems.add(m);
        notifyItemInserted(materialItems.size() - 1);
    }

    public void addAll(List<Material> materialList) {
        for (Material material : materialList) {
            add(setSelecionado(material));
        }
    }

    public void remove(Material m) {
        int position = materialItems.indexOf(m);
        if (position > -1) {
            materialItems.remove(position);
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
        add(new Material());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = materialItems.size() - 1;
        Material material = getItem(position);

        if (material != null) {
            materialItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Material getItem(int position) {
        return materialItems.get(position);
    }

    public Material setSelecionado(Material m){
        Material result = null;
        boolean encontrou = false;

        for (Material material : itemsSelecionados){
            if (material.codigomaterial.equals(m.codigomaterial)){
                try {
                    result = Misc.cloneMaterial(material);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                encontrou = true;
                break;
            }
        }
//        for (int i = 0; i < itemsSelecionados.size(); i++) {
//            if (m.codigomaterial == itemsSelecionados.get(i).codigomaterial){
//                try {
//                    result = Misc.cloneMaterial(itemsSelecionados.get(i));
//                } catch (CloneNotSupportedException e) {
//                    e.printStackTrace();
//                }
//                encontrou = true;
//                break;
//            }
//        }

        if (!encontrou){
            result = m;
        }

        return result;
    }

    public void addSelecionado(Material m){
        boolean encontrou = false;
        for (Material material : itemsSelecionados) {
            if (material.codigomaterial.equals(m.codigomaterial)){
                int position = itemsSelecionados.indexOf(material);
                if (position > -1){
                    itemsSelecionados.set(position, m);
                    encontrou = true;
                }
                break;
            }
        }

        if (!encontrou){
            itemsSelecionados.add(m);
        }
    }

    public void removeSelecionado(Material m){
        for (Material material : itemsSelecionados) {
            if (material.codigomaterial.equals(m.codigomaterial)){
                int position = itemsSelecionados.indexOf(material);
                if (position > -1){
                    itemsSelecionados.remove(position);
                }
                break;
            }
        }
    }

   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class MaterialVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView txtDescricao;
        private TextView txtCusto;
        private TextView txtQuantidade;
        private TextView txtAcao;
        private TextView txtSaldo;
        private ImageView imgSelecionado;
        private ImageView imgExcluir;
        private onMaterialSearchApiListener fOnMaterialSearchApiListener;

        public MaterialVH(View itemView, onMaterialSearchApiListener onMaterialSearchApiListener) {
            super(itemView);


            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            txtCusto = itemView.findViewById(R.id.txt_custo);
            txtQuantidade = itemView.findViewById(R.id.txt_quantidade);
            txtAcao = itemView.findViewById(R.id.txt_acao);
            txtSaldo = itemView.findViewById(R.id.txt_saldo);
            imgSelecionado = itemView.findViewById(R.id.img_adicionar);
            imgExcluir = itemView.findViewById(R.id.img_excluir);
            fOnMaterialSearchApiListener = onMaterialSearchApiListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            imgExcluir.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_excluir:
                    excluindo = true;
                    fOnMaterialSearchApiListener.onBotaoExcluirClick(getAdapterPosition());
                    break;
                default:
                    excluindo = false;
                    fOnMaterialSearchApiListener.onMaterialClick(getAdapterPosition());
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            fOnMaterialSearchApiListener.onMaterialLongClick(getAdapterPosition());
            return true;
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    public interface onMaterialSearchApiListener{
        void onBotaoExcluirClick(int position);
        void onMaterialClick(int position);
        void onMaterialLongClick(int position);
    }
}
