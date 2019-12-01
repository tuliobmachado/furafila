package br.com.informsistemas.furafila.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Resgate;
import br.com.informsistemas.furafila.viewholder.ResgateViewHolder;

public class ResgateAdapter extends RecyclerView.Adapter<ResgateViewHolder>  {

    private List<Resgate> resgateItems;
    private LayoutInflater fLayoutInflater;
    private Context context;
    private ResgateViewHolder.OnResgateListener fOnResgateListener;

    public ResgateAdapter(Context c, ResgateViewHolder.OnResgateListener onResgateListener){
        this.context = c;
        this.fOnResgateListener = onResgateListener;
        this.fLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resgateItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public ResgateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = fLayoutInflater.inflate(R.layout.recycler_item_resgate, viewGroup, false);
        return new ResgateViewHolder(v, fOnResgateListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ResgateViewHolder resgateViewHolder, int position) {
        if (resgateItems.get(position).ordembalcao > 0){
            resgateViewHolder.txtNumeroMovimento.setText("Dav: "+String.valueOf(resgateItems.get(position).documentodav));
        }else {
            resgateViewHolder.txtNumeroMovimento.setText("Documento: "+resgateItems.get(position).numeromovimento);
        }
        resgateViewHolder.txtParceiro.setText(resgateItems.get(position).descricao);
        resgateViewHolder.txtVendedor.setText(resgateItems.get(position).vendedor);
        resgateViewHolder.txtData.setText(Misc.formatDate(resgateItems.get(position).data, "dd/MM/yyyy"));
        resgateViewHolder.txtTotalLiquido.setText("R$ " + Misc.formatMoeda(resgateItems.get(position).totalliquido.floatValue()));

        if (!resgateItems.get(position).vendedor.equals("")){
            resgateViewHolder.layoutVendedor.setVisibility(View.VISIBLE);
        }else{
            resgateViewHolder.layoutVendedor.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return resgateItems == null ? 0 : resgateItems.size();
    }

    public void add(Resgate r) {
        resgateItems.add(r);
        notifyItemInserted(resgateItems.size() - 1);
    }

    public void addAll(List<Resgate> resgateList) {
        for (Resgate resgate : resgateList) {
            add(resgate);
        }
    }

    public void remove(Resgate r) {
        int position = resgateItems.indexOf(r);
        if (position > -1) {
            resgateItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public Resgate getItem(int position) {
        return resgateItems.get(position);
    }
}
