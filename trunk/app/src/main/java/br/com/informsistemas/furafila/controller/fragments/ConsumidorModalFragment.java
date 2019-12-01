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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.utils.Mask;

public class ConsumidorModalFragment extends DialogFragment {

    private EditText edtCPF;

    public static ConsumidorModalFragment newInstance() {
        ConsumidorModalFragment frag = new ConsumidorModalFragment();
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogDefault);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_modal_consumidor, null);
        edtCPF = view.findViewById(R.id.edt_cpf);
        TextWatcher maskCNPJ = Mask.insert("###.###.###-##", edtCPF);
        edtCPF.removeTextChangedListener(maskCNPJ);
        edtCPF.addTextChangedListener(maskCNPJ);

        builder.setView(view);

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, onGetCPF());
            }
        });

        return builder.create();
    }

    private Intent onGetCPF(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("cpf", edtCPF.getText().toString());
        intent.putExtras(bundle);
        return intent;
    }
}
