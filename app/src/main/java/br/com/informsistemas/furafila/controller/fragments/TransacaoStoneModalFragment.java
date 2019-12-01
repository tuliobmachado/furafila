package br.com.informsistemas.furafila.controller.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.informsistemas.furafila.R;

public class TransacaoStoneModalFragment extends DialogFragment {

    public Boolean aprovada;

    public static TransacaoStoneModalFragment newInstance(Boolean aprovada) {
        TransacaoStoneModalFragment frag = new TransacaoStoneModalFragment();
        frag.aprovada = aprovada;
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogDefault);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_modal_transacao, null);
        TextView mensagem = view.findViewById(R.id.txt_transacao);
        ImageView imagem = view.findViewById(R.id.img_transacao);

        if (aprovada){
            mensagem.setText("Transação Autorizada!");
            imagem.setImageResource(R.drawable.ic_transacao_sucesso);
        }else{
            mensagem.setText("Transação não Autorizada!");
            imagem.setImageResource(R.drawable.ic_transacao_erro);
        }

        builder.setView(view);
        return builder.create();
    }
}
