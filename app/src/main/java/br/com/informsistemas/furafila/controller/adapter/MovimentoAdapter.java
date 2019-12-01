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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.utils.CPFCNPJMask;

public class MovimentoAdapter extends RecyclerView.Adapter<MovimentoAdapter.MyViewHolder> {

    private List<Movimento> fList;
    private List<Integer> selectedIds = new ArrayList<>();
    private LayoutInflater fLayoutInflater;
    private Context context;

    public MovimentoAdapter(Context context, List<Movimento> fList) {
        this.fList = fList;
        this.context = context;
        this.fLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_movimento, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Boolean selecionado = false;

        if (fList.get(position).sincronizado.equals("T")){
            myViewHolder.frmStatus.setBackgroundColor(context.getResources().getColor(R.color.movSincronizado));
        }else if (fList.get(position).sincronizado.equals("P")){
            myViewHolder.frmStatus.setBackgroundColor(context.getResources().getColor(R.color.parceiroAVencer));
        }else {
            myViewHolder.frmStatus.setBackgroundColor(context.getResources().getColor(R.color.movNaoSincronizado));
        }

        Parceiro p = null;

        if (Misc.isNullOrEmpty(fList.get(position).cpf)){
            p = ParceiroDAO.getInstance(context).findByIdAuxiliar("codigoparceiro", fList.get(position).codigoparceiro);
            myViewHolder.layoutCPF.setVisibility(View.GONE);
        }else{
            p = ParceiroDAO.getInstance(context).findByIdAuxiliar("cpfcgc", fList.get(position).cpf);
            myViewHolder.txtCPF.setText(CPFCNPJMask.getMask(fList.get(position).cpf));
        }

        myViewHolder.txtCodigoParceiro.setText(p.codigoparceiro);
        myViewHolder.txtDescricao.setText(p.descricao);
        myViewHolder.txtTotalLiquido.setText(Misc.formatMoeda(fList.get(position).totalliquido));

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

        public TextView txtCodigoParceiro;
        public TextView txtDescricao;
        public TextView txtTotalLiquido;
        public TextView txtCPF;
        public CardView cardView;
        public LinearLayout layoutCPF;
        public FrameLayout frmStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCodigoParceiro = itemView.findViewById(R.id.txt_codigo_parceiro);
            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            txtTotalLiquido = itemView.findViewById(R.id.txt_total_liquido);
            txtCPF = itemView.findViewById(R.id.txt_cpf);
            layoutCPF = itemView.findViewById(R.id.layout_cpf);
            cardView = itemView.findViewById(R.id.card_movimento);
            frmStatus = itemView.findViewById(R.id.frm_status);
        }
    }
}
