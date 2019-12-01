package br.com.informsistemas.furafila.controller.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.helper.PrintController;
import br.com.stone.posandroid.providers.PosTransactionProvider;
import stone.application.enums.Action;
import stone.application.enums.ErrorsEnum;
import stone.application.enums.ReceiptType;
import stone.application.enums.TransactionStatusEnum;

public class PagamentoTransactionFragment extends BasePagamentoFragment<PosTransactionProvider> {

    private DialogFragment transacaoStoneModalFragment;
    private Boolean autorizada = false;
    private float valorPago;

    @Override
    protected PosTransactionProvider buildTransactionProvider() {
        return new PosTransactionProvider(getActivity(), transactionObject, getSelectedUserModel());
    }

    @Override
    public void onSuccess() {
        if (transactionObject.getTransactionStatus() == TransactionStatusEnum.APPROVED) {

            autorizada = true;

            final PrintController printController = new PrintController(
                    getActivity(),
                    transactionObject
            );
            printController.print(ReceiptType.MERCHANT);
            valorPago = (Float.valueOf(transactionObject.getAmount()) * Float.valueOf("0.01"));

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogDefault);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.fragment_modal_transacao, null);

            TextView mensagem = view.findViewById(R.id.txt_transacao);
            ImageView imagem = view.findViewById(R.id.img_transacao);
            LinearLayout linearLayout = view.findViewById(R.id.layout_impressao);
            linearLayout.setVisibility(View.VISIBLE);

            mensagem.setText("Transação aprovada!");
            imagem.setImageResource(R.drawable.ic_transacao_sucesso);
            builder.setView(view);
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    printController.print(ReceiptType.CLIENT);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getIntent());
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });

            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getIntent());
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.show();

                }
            });


        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getActivity(),
                            "Erro na transação: \"" + getAuthorizationMessage() + "\"",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        }
    }

    @Override
    public void onError() {
        super.onError();
        if (providerHasErrorEnum(ErrorsEnum.DEVICE_NOT_COMPATIBLE)) {
            Toast.makeText(
                    getActivity(),
                    "Dispositivo não compatível ou dependência relacionada não está presente",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onStatusChanged(final Action action) {
        super.onStatusChanged(action);
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {

                        case TRANSACTION_WAITING_PASSWORD:
                            Toast.makeText(
                                    getActivity(),
                                    "Pin tries remaining to block card: ${transactionProvider?.remainingPinTries}",
                                    Toast.LENGTH_LONG
                            ).show();
                            break;
                        case TRANSACTION_CARD_REMOVED:
                            getActivity().getSupportFragmentManager().popBackStack();
                            break;
                        case REVERSING_TRANSACTION_WITH_ERROR:
                            if (!autorizada) {
                                showDialog(false);
                            }
                            break;
                    }
                }
            });
        }
    }

    private void showDialog(Boolean aprovada){
        if (transacaoStoneModalFragment == null) {
            transacaoStoneModalFragment = TransacaoStoneModalFragment.newInstance(aprovada);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            transacaoStoneModalFragment.show(ft, "transacaoStoneModalFragment");
        }

        // Hide after some seconds
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                if (transacaoStoneModalFragment != null) {
                    transacaoStoneModalFragment.dismiss();
                }

//                if (getActivity().getSupportFragmentManager() != null) {
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }
            }
        }.start();
    }

    private Intent getIntent(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Pagamento", mParam3);
        bundle.putFloat("valorPago", valorPago);
        intent.putExtras(bundle);

        return intent;
    }
}
