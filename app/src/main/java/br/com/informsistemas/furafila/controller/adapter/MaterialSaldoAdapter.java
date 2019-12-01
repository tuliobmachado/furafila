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
import br.com.informsistemas.furafila.models.dao.MaterialDAO;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.MaterialSaldo;

public class MaterialSaldoAdapter extends RecyclerView.Adapter<MaterialSaldoAdapter.MyViewHolder>  {

    private List<MaterialSaldo> fList;
    private LayoutInflater fLayoutInflater;
    private Context context;

    public MaterialSaldoAdapter(Context c, List<MaterialSaldo> list){
        this.fList = list;
        this.context = c;
        this.fLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MaterialSaldoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_material_saldo, viewGroup, false);
        MaterialSaldoAdapter.MyViewHolder mvh = new MaterialSaldoAdapter.MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialSaldoAdapter.MyViewHolder myViewHolder, int position) {
        Material m = MaterialDAO.getInstance(context).findByIdAuxiliar("codigomaterial", fList.get(position).codigomaterial);

        myViewHolder.txtDescricao.setText(m.descricao);
        myViewHolder.txtSaldo.setText(String.format("%.2f", (fList.get(position).saldo / m.fator)) + " | " + m.unidadesaida);
        myViewHolder.txtPreco.setText(Misc.formatMoeda(m.precovenda1));
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

        public TextView txtDescricao;
        public TextView txtSaldo;
        public TextView txtPreco;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            txtSaldo = itemView.findViewById(R.id.txt_saldo);
            txtPreco = itemView.findViewById(R.id.txt_preco);
        }
    }
}
