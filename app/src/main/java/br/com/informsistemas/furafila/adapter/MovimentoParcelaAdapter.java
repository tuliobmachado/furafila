package br.com.informsistemas.furafila.adapter;

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
import br.com.informsistemas.furafila.dao.ModoPagamentoDAO;
import br.com.informsistemas.furafila.model.ModoPagamento;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.MovimentoParcela;

public class MovimentoParcelaAdapter extends RecyclerView.Adapter<MovimentoParcelaAdapter.MyViewHolder> {

    private List<MovimentoParcela> fList;
    private List<Integer> selectedIds = new ArrayList<>();
    private LayoutInflater fLayoutInflater;
    private Context context;

    public MovimentoParcelaAdapter(Context context, List<MovimentoParcela> fList) {
        this.fList = fList;
        this.context = context;
        this.fLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_movimento_parcela, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Boolean selecionado = false;
        ModoPagamento m = null;

        if (Constants.MOVIMENTO.atual.resgate.equals("T")){
            m = ModoPagamentoDAO.getInstance(context).findByIdModoResgate("codigotipoevento", fList.get(position).codigotipoevento);
        }else {
            m = ModoPagamentoDAO.getInstance(context).findByIdModoPadrao("codigotipoevento", fList.get(position).codigotipoevento);
        }

        myViewHolder.txtDescricao.setText(m.descricao);
        myViewHolder.txtValor.setText(Misc.formatMoeda(fList.get(position).valor));

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
        public TextView txtValor;
        public CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            txtValor = itemView.findViewById(R.id.txt_valor);
            cardView = itemView.findViewById(R.id.card_movimento_parcela);
        }
    }
}
