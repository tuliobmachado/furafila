package br.com.informsistemas.furafila.fragments;

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

import br.com.informsistemas.furafila.R;

public class ResgateModalFragment extends DialogFragment {

    private EditText edtParceiro;
    private EditText edtDocumento;

    public static ResgateModalFragment newInstance(){
        ResgateModalFragment fragment = new ResgateModalFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogDefault);
        builder.setTitle("Pesquisa");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_modal_resgate, null);

        edtParceiro = view.findViewById(R.id.edt_resgate_parceiro);
        edtDocumento = view.findViewById(R.id.edt_resgate_documento);

        builder.setView(view);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("parceiro", edtParceiro.getText().toString());
                bundle.putString("documento", edtDocumento.getText().toString());
                intent.putExtras(bundle);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            }
        });

        return builder.create();
    }
}
