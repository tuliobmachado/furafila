package br.com.informsistemas.furafila.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.interfaces.ItemClickListener;

public class RelatorioPedidoAdapter extends RecyclerView.Adapter<RelatorioPedidoAdapter.MyViewHolder> {

    private List<Movimento> fList;
    private LayoutInflater fLayoutInflater;
    private Context context;
    private ItemClickListener fItemClickListener;

    public RelatorioPedidoAdapter(Context c, List<Movimento> list, ItemClickListener itemClickListener) {
        this.fList = list;
        this.context = c;
        this.fLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RelatorioPedidoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_relatorio_pedido, viewGroup, false);
        return new RelatorioPedidoAdapter.MyViewHolder(v, fItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatorioPedidoAdapter.MyViewHolder myViewHolder, int position) {

        Parceiro p = ParceiroDAO.getInstance(context).findByIdAuxiliar("codigoparceiro", fList.get(position).codigoparceiro);

        if (fList.get(position).sincronizado.equals("T")) {
            myViewHolder.frmStatus.setBackgroundColor(context.getResources().getColor(R.color.movSincronizado));
        } else if (fList.get(position).sincronizado.equals("P")){
            myViewHolder.frmStatus.setBackgroundColor(context.getResources().getColor(R.color.parceiroAVencer));
        }else{
            myViewHolder.frmStatus.setBackgroundColor(context.getResources().getColor(R.color.movNaoSincronizado));
        }

        myViewHolder.txtCodigoParceiro.setText(p.codigoparceiro);
        myViewHolder.txtData.setText(Misc.formatDate(fList.get(position).data, "dd/MM/yyyy"));
        myViewHolder.txtDescricao.setText(p.descricao);
        myViewHolder.txtHoraInicio.setText(Misc.formatDate(fList.get(position).datainicio, "HH:mm:ss"));
        if (fList.get(position).datafim != null) {
            myViewHolder.txtHoraFim.setText(Misc.formatDate(fList.get(position).datafim, "HH:mm:ss"));
        }
        myViewHolder.txtTotal.setText("R$ "+ Misc.formatMoeda(fList.get(position).totalliquido));
    }

    @Override
    public int getItemCount() {
        if (fList != null) {
            return fList.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtCodigoParceiro;
        public TextView txtDescricao;
        public TextView txtData;
        public TextView txtHoraInicio;
        public TextView txtHoraFim;
        public TextView txtTotal;
        public FrameLayout frmStatus;
        public ItemClickListener fItemClickListener;

        public MyViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);

            txtCodigoParceiro = itemView.findViewById(R.id.txt_relatorio_pedido_codigo);
            txtDescricao = itemView.findViewById(R.id.txt_relatorio_pedido_descricao);
            txtData = itemView.findViewById(R.id.txt_relatorio_pedido_data);
            txtHoraInicio = itemView.findViewById(R.id.txt_relatorio_pedido_inicio);
            txtHoraFim = itemView.findViewById(R.id.txt_relatorio_pedido_fim);
            txtTotal = itemView.findViewById(R.id.txt_relatorio_pedido_total);
            frmStatus = itemView.findViewById(R.id.frm_status);
            fItemClickListener = itemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
