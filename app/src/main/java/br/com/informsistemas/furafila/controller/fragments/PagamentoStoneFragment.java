package br.com.informsistemas.furafila.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.adapter.PagamentoStoneAdapter;
import br.com.informsistemas.furafila.models.dao.FormaPagamentoDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.FormaPagamento;
import stone.application.enums.TypeOfTransactionEnum;

public class PagamentoStoneFragment extends Fragment implements PagamentoStoneAdapter.onPagamentoStoneListener {

    private RecyclerView recyclerView;
    private Button btn;
    private TextView txtTotalPago;
    private TextView txtTotalTroco;
    private PagamentoStoneAdapter pagamentoStoneAdapter;
    private LinearLayout layoutTotalPagamento;
    private android.support.v7.widget.Toolbar layoutToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        btn = getActivity().findViewById(R.id.btn_resumo_pedido);
        btn.setVisibility(View.GONE);

        layoutToolbar = getActivity().findViewById(R.id.toolbar);
        layoutToolbar.setVisibility(View.VISIBLE);

        layoutTotalPagamento = getActivity().findViewById(R.id.layout_total_pagamento);
        layoutTotalPagamento.setVisibility(View.VISIBLE);

        txtTotalPago = getActivity().findViewById(R.id.txt_total_pagamento);
        txtTotalPago.setText("R$ " + Misc.formatMoeda(Constants.MOVIMENTO.total_pendente));

        txtTotalTroco = getActivity().findViewById(R.id.txt_pagamento_total_troco);
        txtTotalTroco.setText("R$ " + Misc.formatMoeda(Constants.MOVIMENTO.total_troco));

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        pagamentoStoneAdapter = new PagamentoStoneAdapter(getActivity(), this);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(pagamentoStoneAdapter);
        recyclerView.setLayoutManager(llm);

        pagamentoStoneAdapter.addAll(FormaPagamentoDAO.getInstance(getActivity()).findAll());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FormaPagamento formaPagamento = (FormaPagamento) data.getExtras().getSerializable("Pagamento");
        Float valorPago = data.getFloatExtra("valorPago", 0);
        Float valorTroco = Float.parseFloat("0");


        switch (formaPagamento.codigoforma) {
            case "0001":
                if (valorPago > Constants.MOVIMENTO.total_pendente){
                    valorTroco = valorPago - Constants.MOVIMENTO.total_pendente;
                    txtTotalTroco.setText("R$ " + Misc.formatMoeda(Constants.MOVIMENTO.total_troco));
                }
                setValorPago(valorPago);
                data.putExtra("valorTroco", valorTroco);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
                break;
            default:
                if (requestCode == 100) {
                    layoutTotalPagamento.setVisibility(View.VISIBLE);
                    setValorPago(valorPago);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
                } else {

                    layoutToolbar.setVisibility(View.GONE);
                    layoutTotalPagamento.setVisibility(View.GONE);

                    PagamentoTransactionFragment pagamentoTransactionActivity = (PagamentoTransactionFragment) getActivity().getSupportFragmentManager().findFragmentByTag("pagamentoTransactionActivity");

                    if (pagamentoTransactionActivity == null) {
                        pagamentoTransactionActivity = new PagamentoTransactionFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("param1", String.valueOf(Math.round(valorPago * 100)));
                        bundle.putSerializable("param3", formaPagamento);

                        if (formaPagamento.codigoforma.equals("0003")) {
                            bundle.putString("param2", TypeOfTransactionEnum.DEBIT.toString());
                        } else {
                            bundle.putString("param2", TypeOfTransactionEnum.CREDIT.toString());
                        }

                        pagamentoTransactionActivity.setArguments(bundle);
                        pagamentoTransactionActivity.setTargetFragment(this, 100);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, pagamentoTransactionActivity, "pagamentoTransactionActivity");
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
                break;
        }

        if (Misc.fRound(true, Constants.MOVIMENTO.total_pendente, 2) == 0){
            layoutToolbar.setVisibility(View.VISIBLE);
            layoutTotalPagamento.setVisibility(View.VISIBLE);
            getActivity().getSupportFragmentManager().popBackStack();
        }

    }

    @Override
    public void onClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("formapagamento", pagamentoStoneAdapter.getItem(position));

        DialogFragment fragmentModal = PagamentoStoneModalFragment.newInstance();
        fragmentModal.setTargetFragment(this, 1);
        fragmentModal.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentModal.show(ft, "pagamentoStoneModalFragment");
    }

    private void setValorPago(Float value) {
        Constants.MOVIMENTO.total_pendente = Constants.MOVIMENTO.total_pendente - value;

        if (Constants.MOVIMENTO.total_pendente < 0){
            Constants.MOVIMENTO.total_pendente = 0;
        }

        txtTotalPago.setText("R$ " + Misc.formatMoeda(Constants.MOVIMENTO.total_pendente));
    }
}
