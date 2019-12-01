package br.com.informsistemas.furafila.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.informsistemas.furafila.R;

public class ResgateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtParceiro;
    public TextView txtData;
    public TextView txtNumeroMovimento;
    public TextView txtTotalLiquido;
    public TextView txtVendedor;
    public LinearLayout layoutVendedor;
    private OnResgateListener fOnResgateListener;

    public ResgateViewHolder(View itemView, OnResgateListener onResgateListener){
        super(itemView);

        txtParceiro =  itemView.findViewById(R.id.txt_resgate_descricao);
        txtVendedor = itemView.findViewById(R.id.txt_resgate_vendedor);
        txtData = itemView.findViewById(R.id.txt_resgate_data);
        txtNumeroMovimento = itemView.findViewById(R.id.txt_resgate_documento);
        txtTotalLiquido = itemView.findViewById(R.id.txt_resgate_total);
        layoutVendedor = itemView.findViewById(R.id.layout_resgate_vendedor);
        fOnResgateListener = onResgateListener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        fOnResgateListener.onResgateClick(getAdapterPosition());
    }

    public interface OnResgateListener{
        void onResgateClick(int position);
    }
}
