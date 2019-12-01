package br.com.informsistemas.furafila.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.utils.CPFCNPJMask;

public class ParceiroAutoCompleteAdapter extends ArrayAdapter<Parceiro> implements Filterable {

    private Context context;
    private List<Parceiro> listParceiro;
    private List<Parceiro> listParceiroAux;
    private Filter filter;
    private LayoutInflater inflater;

    public ParceiroAutoCompleteAdapter(Context context, List<Parceiro> listParceiro) {
        super(context, 0, 0, listParceiro);
        this.context = context;
        this.listParceiro = listParceiro;
        this.listParceiroAux = new ArrayList<Parceiro>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return (listParceiroAux.size());
    }

    @Override
    public Parceiro getItem(int position){
        return (listParceiroAux.get(position));
    }

    @Override
    public View getView(int position, View view, ViewGroup group){
        ViewHolder viewHolder;

        if (view == null){
            view = inflater.inflate(R.layout.auto_complete_item_parceiro, null);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);

            viewHolder.txtCodigoParceiro = view.findViewById(R.id.txt_codigo_parceiro);
            viewHolder.txtDescricao = view.findViewById(R.id.txt_descricao);
            viewHolder.txtNomeFantasia = view.findViewById(R.id.txt_nome_fantasia);
            viewHolder.txtCPFCGC = view.findViewById(R.id.txt_cpfcgc);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.txtCodigoParceiro.setText(listParceiroAux.get(position).codigoparceiro);
        viewHolder.txtDescricao.setText(listParceiroAux.get(position).descricao);
        viewHolder.txtNomeFantasia.setText(listParceiroAux.get(position).nomefantasia);
        viewHolder.txtCPFCGC.setText(CPFCNPJMask.getMask(listParceiroAux.get(position).cpfcgc));

        return view;
    }

    static class ViewHolder{
        TextView txtCodigoParceiro;
        TextView txtDescricao;
        TextView txtNomeFantasia;
        TextView txtCPFCGC;
    }

    //FILTER
    @Override
    public Filter getFilter(){
        if (filter == null){
            filter = new ArrayFilter();
        }

        return filter;
    }

    private class ArrayFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String constraintString = (constraint+"").toLowerCase();

            if (constraint == null || constraint.length() == 0){
                List<Parceiro> list = new ArrayList<Parceiro>(listParceiro);
                results.count = list.size();
                results.values = list;
            }else{
                int qtdConstraint = constraintString.length();

                ArrayList<Parceiro> newValues = new ArrayList<Parceiro>(listParceiro.size());

                for (int i = 0; i < listParceiro.size(); i++) {

//                    if (listParceiro.get(i).descricao.substring(0, qtdConstraint).equalsIgnoreCase(constraintString) ||
//                        listParceiro.get(i).nomefantasia.substring(0, qtdConstraint).equalsIgnoreCase(constraintString) ||
//                        listParceiro.get(i).cpfcgc.substring(0, qtdConstraint).equalsIgnoreCase(constraintString)){

                    if (listParceiro.get(i).codigoparceiro.toLowerCase().contains(constraintString) ||
                        listParceiro.get(i).descricao.toLowerCase().contains(constraintString) ||
                        listParceiro.get(i).nomefantasia.toLowerCase().contains(constraintString) ||
                        listParceiro.get(i).cpfcgc.toLowerCase().contains(constraintString)){
                        newValues.add(listParceiro.get(i));
                    }
                }

                results.count = newValues.size();
                results.values = newValues;
            }


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null){
                listParceiroAux = (ArrayList<Parceiro>) results.values;
            }else{
                listParceiroAux = new ArrayList<Parceiro>();
            }

            if (results.count == 0){
                notifyDataSetInvalidated();
            }else{
                notifyDataSetChanged();
            }
        }
    }
}
