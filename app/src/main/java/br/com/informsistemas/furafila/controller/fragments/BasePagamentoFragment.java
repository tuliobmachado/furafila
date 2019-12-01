package br.com.informsistemas.furafila.controller.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.FormaPagamento;
import stone.application.enums.Action;
import stone.application.enums.ErrorsEnum;
import stone.application.enums.InstalmentTransactionEnum;
import stone.application.enums.TypeOfTransactionEnum;
import stone.application.interfaces.StoneActionCallback;
import stone.database.transaction.TransactionObject;
import stone.providers.BaseTransactionProvider;
import stone.user.UserModel;
import stone.utils.Stone;

public abstract class BasePagamentoFragment<T extends BaseTransactionProvider> extends Fragment implements StoneActionCallback {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private BaseTransactionProvider transactionProvider;
    protected final TransactionObject transactionObject = new TransactionObject();

    private TextView txtRetorno;
    private TextView txtValor;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public FormaPagamento mParam3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pagamento_stone, container, false);

        mParam1 = getArguments().getString(ARG_PARAM1);
        mParam2 = getArguments().getString(ARG_PARAM2);
        mParam3 = (FormaPagamento) getArguments().getSerializable(ARG_PARAM3);

        txtRetorno = view.findViewById(R.id.txt_retorno);
        txtValor = view.findViewById(R.id.txt_valor);

        float valorNormal = (Float.valueOf(mParam1)*Float.valueOf("0.01"));

        txtValor.setText("VALOR A PAGAR: R$: "+ Misc.formatMoeda(valorNormal));

        initTransaction();

        return view;
    }

    public void initTransaction(){
        // Informa a quantidade de parcelas.
        transactionObject.setInstalmentTransaction(InstalmentTransactionEnum.ONE_INSTALMENT);

        // Verifica a forma de pagamento selecionada.
        TypeOfTransactionEnum transactionType;
        switch (mParam2) {
            case "CREDIT":
                transactionType = TypeOfTransactionEnum.CREDIT;
                break;
            case "DEBIT":
                transactionType = TypeOfTransactionEnum.DEBIT;
                break;
            default:
                transactionType = TypeOfTransactionEnum.CREDIT;
        }

//        Defina o ITK da sua transação
//        transactionObject.setInitiatorTransactionKey("SEU_IDENTIFICADOR_UNICO_AQUI");

        transactionObject.setTypeOfTransaction(transactionType);
        transactionObject.setCapture(true);
        transactionObject.setAmount(mParam1);

//        transactionObject.setSubMerchantCity("Maceió"); //Cidade do sub-merchant
//        transactionObject.setSubMerchantPostalAddress("57020580"); //CEP do sub-merchant (Apenas números)
//        transactionObject.setSubMerchantRegisteredIdentifier("00000000"); // Identificador do sub-merchant
//        transactionObject.setSubMerchantTaxIdentificationNumber("35559616000181"); // CNPJ do sub-merchant (apenas números)

//        Seleciona o mcc do lojista.
//        transactionObject.setSubMerchantCategoryCode("123");

//        Seleciona o endereço do lojista.
//        transactionObject.setSubMerchantAddress("address");]

        transactionProvider = buildTransactionProvider();
        transactionProvider.useDefaultUI(true);
        transactionProvider.setDialogMessage("Aguardando Pagamento");
        transactionProvider.setConnectionCallback(this);
        transactionProvider.execute();
    }

    protected String getAuthorizationMessage() {
        return transactionProvider.getMessageFromAuthorize();
    }

    protected abstract T buildTransactionProvider();

    protected boolean providerHasErrorEnum(ErrorsEnum errorsEnum) {
        return transactionProvider.theListHasError(errorsEnum);
    }

    @Override
    public void onError() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Erro: " + transactionProvider.getListOfErrors(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStatusChanged(final Action action) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case TRANSACTION_WAITING_CARD:
                            txtRetorno.setText("AGUARDANDO O CARTÃO...");
                            break;
                        case TRANSACTION_CARD_REMOVED:
                            txtRetorno.setText("CARTÃO REMOVIDO...");
                            break;
                        case TRANSACTION_SENDING:
                            txtRetorno.setText("ENVIANDO TRANSAÇÃO, POR FAVOR, AGUARDE...");
                            break;
                        case TRANSACTION_WAITING_PASSWORD:
                            txtRetorno.setText("AGUARDANDO A SENHA...");
                            break;
                        case TRANSACTION_REMOVE_CARD:
                            txtRetorno.setText("REMOVA O CARTÃO...");
                            break;
                        default:
                            txtRetorno.setText(action.name());

                    }
                }
            });
        }
    }

    protected UserModel getSelectedUserModel() {
        return Stone.getUserModel(0);
    }

}
