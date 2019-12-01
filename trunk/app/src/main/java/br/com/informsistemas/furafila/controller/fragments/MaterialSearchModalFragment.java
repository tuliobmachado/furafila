package br.com.informsistemas.furafila.controller.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Enums;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Material;

public class MaterialSearchModalFragment extends DialogFragment {

    private TextView txtDescricao;
    private TextView txtSaldo;
    private TextView txtPreco;
    private EditText edtQuantidade;
    private boolean possuiQuantidade;
    private int position;

    public static MaterialSearchModalFragment newInstance(){
        MaterialSearchModalFragment frag = new MaterialSearchModalFragment();
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogDefault);
        builder.setTitle("Material");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_modal_search_material, null);

        txtDescricao = view.findViewById(R.id.txt_descricao);
        txtSaldo = view.findViewById(R.id.txt_saldo);
        txtPreco = view.findViewById(R.id.txt_preco_venda);
        edtQuantidade = view.findViewById(R.id.edt_quantidade);

        position = getArguments().getInt("position");
        final Material material = (Material) getArguments().getSerializable("material");

        txtDescricao.setText(material.descricao);
        if (Constants.APP.TIPO_APLICACAO == Enums.TIPO_APLICACAO.FORCA_DE_VENDAS) {
            txtSaldo.setText(String.format("%.2f", material.saldomaterial) + " | " + material.unidadesaida);
            txtPreco.setText("R$ " + Misc.formatMoeda(material.totalliquido));
        }else{
            txtSaldo.setText("");
            txtPreco.setText("R$ " + Misc.formatMoeda(material.precovenda1));
        }

        if (material.quantidade == 0) {
            possuiQuantidade = false;
            edtQuantidade.setText("");
        }else{
            possuiQuantidade = true;
            edtQuantidade.setText(Float.toString(material.quantidade));
        }

        builder.setView(view);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edtQuantidade.getText().toString().equals("")){
                    edtQuantidade.setText("0");
                }

                if ((!possuiQuantidade && Float.parseFloat(edtQuantidade.getText().toString()) == 0) ||
                    (material.quantidade == Float.parseFloat(edtQuantidade.getText().toString()))){
                    dialog.dismiss();
                }else {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getQuantidade());
                }
            }
        });

        return builder.create();
    }

    private Intent getQuantidade(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putFloat("quantidade", Float.valueOf(edtQuantidade.getText().toString()));
        intent.putExtras(bundle);

        return intent;
    }
}
