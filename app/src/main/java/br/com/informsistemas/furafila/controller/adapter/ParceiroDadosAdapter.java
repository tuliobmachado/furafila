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
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.utils.CPFCNPJMask;

public class ParceiroDadosAdapter extends RecyclerView.Adapter<ParceiroDadosAdapter.MyViewHolder>  {

    private List<Parceiro> fList;
    private LayoutInflater fLayoutInflater;
    private Context context;

    public ParceiroDadosAdapter(Context c, List<Parceiro> list){
        this.fList = list;
        this.context = c;
        this.fLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ParceiroDadosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_parceiro_dados, viewGroup, false);
        ParceiroDadosAdapter.MyViewHolder mvh = new ParceiroDadosAdapter.MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ParceiroDadosAdapter.MyViewHolder myViewHolder, int position) {
        myViewHolder.txtDescricao.setText(fList.get(position).descricao);
        myViewHolder.txtNomeFantasia.setText(fList.get(position).nomefantasia);
        myViewHolder.txtCPFCGC.setText(CPFCNPJMask.getMask(fList.get(position).cpfcgc));
        myViewHolder.txtLogradouro.setText(fList.get(position).endereco);
        myViewHolder.txtBairro.setText(fList.get(position).bairro);
        myViewHolder.txtCidade.setText(fList.get(position).cidade);
        myViewHolder.txtNumero.setText(fList.get(position).numerologradouro);
        myViewHolder.txtUF.setText(fList.get(position).estado);
        myViewHolder.txtCEP.setText(fList.get(position).cep);
        myViewHolder.txtTel1.setText(fList.get(position).telefone);
        myViewHolder.txtTel2.setText(fList.get(position).telefone2);
        myViewHolder.txtEmail.setText(fList.get(position).email);
        myViewHolder.txtCampoLivreA3.setText(fList.get(position).campolivrea3);
        myViewHolder.txtCampoLivreA4.setText(fList.get(position).campolivrea4);
        myViewHolder.txtCodigo.setText(fList.get(position).codigoparceiro);
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
        public TextView txtNomeFantasia;
        public TextView txtCPFCGC;
        public TextView txtLogradouro;
        public TextView txtBairro;
        public TextView txtCidade;
        public TextView txtNumero;
        public TextView txtUF;
        public TextView txtCEP;
        public TextView txtTel1;
        public TextView txtTel2;
        public TextView txtEmail;
        public TextView txtCampoLivreA3;
        public TextView txtCampoLivreA4;
        public TextView txtCodigo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            txtNomeFantasia = itemView.findViewById(R.id.txt_nome_fantasia);
            txtCPFCGC = itemView.findViewById(R.id.txt_cpfcgc);
            txtLogradouro = itemView.findViewById(R.id.txt_rua);
            txtBairro = itemView.findViewById(R.id.txt_bairro);
            txtCidade = itemView.findViewById(R.id.txt_cidade);
            txtNumero = itemView.findViewById(R.id.txt_numero);
            txtUF = itemView.findViewById(R.id.txt_uf);
            txtCEP = itemView.findViewById(R.id.txt_cep);
            txtTel1 = itemView.findViewById(R.id.txt_tel1);
            txtTel2 = itemView.findViewById(R.id.txt_tel2);
            txtEmail = itemView.findViewById(R.id.txt_email);
            txtCampoLivreA3 = itemView.findViewById(R.id.txt_campolivrea3);
            txtCampoLivreA4 = itemView.findViewById(R.id.txt_campolivrea4);
            txtCodigo = itemView.findViewById(R.id.txt_codigo);
        }
    }
}
