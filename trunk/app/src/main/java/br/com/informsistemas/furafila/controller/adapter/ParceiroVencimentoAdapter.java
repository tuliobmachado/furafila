package br.com.informsistemas.furafila.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.ParceiroVencimento;

public class ParceiroVencimentoAdapter extends RecyclerView.Adapter<ParceiroVencimentoAdapter.MyViewHolder>  {

    private List<ParceiroVencimento> fList;
    private LayoutInflater fLayoutInflater;
    private Context context;

    public ParceiroVencimentoAdapter(Context c, List<ParceiroVencimento> list){
        this.fList = list;
        this.context = c;
        this.fLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ParceiroVencimentoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_parceiro_vencimento, viewGroup, false);
        ParceiroVencimentoAdapter.MyViewHolder mvh = new ParceiroVencimentoAdapter.MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ParceiroVencimentoAdapter.MyViewHolder myViewHolder, int position) {
        myViewHolder.txtDataEmissao.setText("Emissão: "+ Misc.formatDate(fList.get(position).dataemissao, "dd/MM/yyyy"));
        myViewHolder.txtDataVencimento.setText("Vencimento: "+ Misc.formatDate(fList.get(position).datavencimento, "dd/MM/yyyy"));
        myViewHolder.txtValor.setText("Total: R$ "+ Misc.formatMoeda(fList.get(position).valor));
        myViewHolder.txtStatus.setText("Status: "+fList.get(position).status);
    }

    @Override
    public int getItemCount() {
        if (fList != null){
            return fList.size();
        }else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDataVencimento;
        public TextView txtDataEmissao;
        public TextView txtValor;
        public TextView txtStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDataVencimento = itemView.findViewById(R.id.txt_data_vencimento);
            txtDataEmissao = itemView.findViewById(R.id.txt_data_emissao);
            txtValor = itemView.findViewById(R.id.txt_valor);
            txtStatus = itemView.findViewById(R.id.txt_status);
        }
    }
}
