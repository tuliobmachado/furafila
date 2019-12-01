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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.FormaPagamento;
import br.com.informsistemas.furafila.models.utils.MoneyTextWatcher;

public class PagamentoStoneModalFragment extends DialogFragment {

    private FormaPagamento formaSelecionada;
    private EditText edtValor;
    private TextView txtValorPendente;

    public static PagamentoStoneModalFragment newInstance() {
        PagamentoStoneModalFragment frag = new PagamentoStoneModalFragment();
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogDefault);
        formaSelecionada = (FormaPagamento) getArguments().getSerializable("formapagamento");

        builder.setTitle("Confirma pagamento em " + formaSelecionada.descricao + " ?");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_modal_pagamento_stone, null);

        edtValor = view.findViewById(R.id.edt_valor);
        edtValor.addTextChangedListener(new MoneyTextWatcher(edtValor, new Locale("pt", "BR")));
        txtValorPendente = view.findViewById(R.id.txt_valor_pendente);
        txtValorPendente.setText("Total Pendente: R$ " + Misc.formatMoeda(Constants.MOVIMENTO.total_pendente));

        builder.setView(view);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Concluir", null);

        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float value = getValorEdit();
                        Toast toast = null;

                        if (value == 0) {
                            toast = Toast.makeText(getActivity(), "Valor deve ser maior que 0!", Toast.LENGTH_LONG);
                        } else if (value > Misc.fRound(true, Constants.MOVIMENTO.total_pendente, 2) & !formaSelecionada.codigoforma.equals("0001")){
                            toast = Toast.makeText(getActivity(), "Valor acima do máximo permitido!", Toast.LENGTH_LONG);
                        } else {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getIntent(value));
                            dialog.dismiss();
                        }

                        if (toast != null) {
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }
                });
            }
        });

        dialog.show();
        return dialog;
    }

    private Intent getIntent(Float valor){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Pagamento", formaSelecionada);
        bundle.putFloat("valorPago", valor);
        intent.putExtras(bundle);

        return intent;
    }

    private float getValorEdit() {
        String valor = edtValor.getText().toString();
        valor = valor.replace("R$", "");
        valor = valor.replace(",", ".");
        Pattern pattern = Pattern.compile("[.]");
        Matcher matcher = pattern.matcher(valor);
        int count = 0;

        while (matcher.find()) {
            count++;
        }

        for (int i = 0; i < count - 1; i++) {
            valor = valor.replaceFirst("[.]", "");
        }

        if (valor.equals("")) {
            valor = "0";
        }

        return Float.valueOf(valor);
    }
}
