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
import br.com.informsistemas.furafila.models.dao.ParceiroVencimentoDAO;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.pojo.ParceiroVencimento;
import br.com.informsistemas.furafila.models.utils.CPFCNPJMask;
import br.com.informsistemas.furafila.interfaces.ItemClickListener;

public class ParceiroConsultaAdapter extends RecyclerView.Adapter<ParceiroConsultaAdapter.MyViewHolder>  {

    private List<Parceiro> fList;
    private LayoutInflater fLayoutInflater;
    private Context context;
    private ItemClickListener fItemClickListener;

    public ParceiroConsultaAdapter(Context c, List<Parceiro> list, ItemClickListener itemClickListener){
        this.fList = list;
        this.context = c;
        this.fLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_parceiro, viewGroup, false);
        return new MyViewHolder(v, fItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        List<ParceiroVencimento> parceiroVencimentoList = ParceiroVencimentoDAO.getInstance(context).findAllVencimentoByCodigoParceiro(fList.get(position).codigoparceiro);

        if (parceiroVencimentoList.size() == 0){
            myViewHolder.frmStatus.setBackgroundColor(context.getResources().getColor(R.color.movSincronizado));
        }else{
            if (parceiroVencimentoList.get(0).status.equals("Vencido")){
                myViewHolder.frmStatus.setBackgroundColor(context.getResources().getColor(R.color.movNaoSincronizado));
            }else{
                myViewHolder.frmStatus.setBackgroundColor(context.getResources().getColor(R.color.parceiroAVencer));
            }
        }

        myViewHolder.txtCodigo.setText(fList.get(position).codigoparceiro);
        myViewHolder.txtDescricao.setText(fList.get(position).descricao);
        myViewHolder.txtNomeFantasia.setText(fList.get(position).nomefantasia);
        myViewHolder.txtCPFCGC.setText(CPFCNPJMask.getMask(fList.get(position).cpfcgc));
    }

    @Override
    public int getItemCount() {
        if (fList != null){
            return fList.size();
        }else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtCodigo;
        public TextView txtDescricao;
        public TextView txtNomeFantasia;
        public TextView txtCPFCGC;
        public FrameLayout frmStatus;
        public ItemClickListener fItemClickListener;

        public MyViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);

            txtCodigo = itemView.findViewById(R.id.txt_codigo);
            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            txtNomeFantasia = itemView.findViewById(R.id.txt_nome_fantasia);
            txtCPFCGC = itemView.findViewById(R.id.txt_cpfcgc);
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
