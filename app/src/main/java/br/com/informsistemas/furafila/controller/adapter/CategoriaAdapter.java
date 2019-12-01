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
import android.widget.TextView;

import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.pojo.Categoria;
import br.com.informsistemas.furafila.interfaces.ItemClickListener;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.MyViewHolder> {

    private List<Categoria> fList;
    private LayoutInflater fLayoutInflater;
    private Context context;
    private ItemClickListener fItemClickListener;
    private Integer idSelecionado = 0;

    public CategoriaAdapter(Context context, List<Categoria> fList, ItemClickListener itemClickListener) {
        this.fList = fList;
        this.context = context;
        this.fLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_categoria, viewGroup, false);
        return new MyViewHolder(v, fItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Boolean selecionado = false;

        myViewHolder.txtDescricao.setText(fList.get(position).descricao);

        if (idSelecionado == fList.get(position).id){
            selecionado = true;
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

    public void setIdSelecionado(Integer idSelecionado) {
        this.idSelecionado = idSelecionado;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtDescricao;
        public CardView cardView;
        public ItemClickListener fItemClickListener;

        public MyViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.txt_descricao);
            cardView = itemView.findViewById(R.id.card_categoria);
            fItemClickListener = itemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
