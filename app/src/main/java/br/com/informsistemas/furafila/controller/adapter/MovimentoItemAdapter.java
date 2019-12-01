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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.dao.MaterialDAO;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;

public class MovimentoItemAdapter extends RecyclerView.Adapter<MovimentoItemAdapter.MyViewHolder> {

    private List<MovimentoItem> fList;
    private List<Integer> selectedIds = new ArrayList<>();
    private LayoutInflater fLayoutInflater;
    private Context context;

    public MovimentoItemAdapter(Context context, List<MovimentoItem> fList) {
        this.fList = fList;
        this.context = context;
        this.fLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_movimento_item, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Boolean selecionado = false;
        float acrescimo = 0;

        acrescimo = fList.get(position).valoricmsfecoepst + fList.get(position).valoripi + fList.get(position).valoricmssubst;

        if (acrescimo > 0){
            myViewHolder.txtTotalAcrescimo.setText(Misc.formatMoeda(acrescimo));
            myViewHolder.txtTotalLiquido.setText(Misc.formatMoeda(fList.get(position).totalitem + acrescimo));
        }else{
            myViewHolder.layoutAcrescimo.setVisibility(View.GONE);
        }

        Material m = MaterialDAO.getInstance(context).findByIdAuxiliar("codigomaterial", fList.get(position).codigomaterial);

        myViewHolder.txtDescricao.setText(m.descricao);
        myViewHolder.txtUnidade.setText(m.unidadesaida);
        myViewHolder.txtQuantidadeCusto.setText(String.format("%.2f", fList.get(position).quantidade) + " X " + Misc.formatMoeda((fList.get(position).custo)));
        myViewHolder.txtTotalItem.setText(Misc.formatMoeda((fList.get(position).totalitem)));

        for (int i : selectedIds){
            if (fList.get(position).id == i){
                selecionado = true;
            }
        }

        if (selecionado){
            myViewHolder.cardView.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.cardSelecionado)));
        }else{
            myViewHolder.cardView.setForeground(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        }
    }

    @Override
    public int getItemCount() {
        if (fList != null) {
            return fList.size();
        }else
            return 0;
    }

    public void setSelectedIds(List<Integer> selectedIds){
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView txtDescricao;
        public TextView txtUnidade;
        public TextView txtQuantidadeCusto;
        public TextView txtTotalItem;
        public TextView txtTotalAcrescimo;
        public TextView txtTotalLiquido;
        public CardView cardView;
        public LinearLayout layoutAcrescimo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            txtUnidade = itemView.findViewById(R.id.txt_unidade);
            txtQuantidadeCusto = itemView.findViewById(R.id.txt_quantidade_custo);
            txtTotalItem = itemView.findViewById(R.id.txt_total_item);
            txtTotalAcrescimo = itemView.findViewById(R.id.txt_total_acrescimo);
            txtTotalLiquido = itemView.findViewById(R.id.txt_total_liquido);
            cardView = itemView.findViewById(R.id.card_movimento_item);
            layoutAcrescimo = itemView.findViewById(R.id.layout_acrescimo);
        }
    }
}
