package br.com.informsistemas.furafila.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.fragments.FormaPagamentoSearchFragment;
import br.com.informsistemas.furafila.controller.fragments.MovimentoParcelaFragment;
import br.com.informsistemas.furafila.controller.fragments.PagamentoStoneFragment;
import br.com.informsistemas.furafila.models.dao.MovimentoDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoItemDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoParcelaDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Enums;

public class MovimentoParcelaActivity extends AppCompatActivity {

    private MovimentoParcelaFragment movimentoParcelaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Pagamentos");

        Button btn = findViewById(R.id.btn_resumo_pedido);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MovimentoParcelaDAO.getInstance(MovimentoParcelaActivity.this).findByMovimentoId(Constants.MOVIMENTO.atual.id).size() > 0) {
                    if (Constants.MOVIMENTO.atual.datafim == null) {
                        Constants.MOVIMENTO.atual.datafim = new Date();
                    } else {
                        Constants.MOVIMENTO.atual.dataalteracao = new Date();
                    }

                    MovimentoDAO.getInstance(MovimentoParcelaActivity.this).createOrUpdate(Constants.MOVIMENTO.atual);

                    Constants.MOVIMENTO.enviarPedido = true;

                    Constants.PEDIDO.movimento = MovimentoDAO.getInstance(MovimentoParcelaActivity.this).findById(Constants.MOVIMENTO.atual.id);
                    Constants.PEDIDO.movimentoItems = MovimentoItemDAO.getInstance(MovimentoParcelaActivity.this).findByMovimentoId(Constants.MOVIMENTO.atual.id);
                    Constants.PEDIDO.movimentoParcelas = MovimentoParcelaDAO.getInstance(MovimentoParcelaActivity.this).findByMovimentoId(Constants.MOVIMENTO.atual.id);
                    Constants.PEDIDO.PEDIDOATUAL = 2;
                    Constants.PEDIDO.listPedidos = null;
                    Constants.PEDIDO.listPedidos = new ArrayList<>();
                    Constants.PEDIDO.listPedidos.add(Constants.PEDIDO.movimento.id);

                    Intent intent = new Intent(MovimentoParcelaActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MovimentoParcelaActivity.this, "Necessário informar uma forma de pagamento", Toast.LENGTH_LONG).show();
                }
            }
        });

        onShowFormaPagamento();

        float valorAPagar = (Constants.MOVIMENTO.atual.totalliquido - MovimentoParcelaDAO.getInstance(this).sumByMovimentoId("valor", Constants.MOVIMENTO.atual.id));

        if (valorAPagar > 0) {
            onShowPagamentoStone();
        }
        btn.setText("EMITIR NFC-e");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!Constants.MOVIMENTO.atual.resgate.equals("T")) {
            getMenuInflater().inflate(R.menu.menu_lista, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Back button
            case R.id.action_search_list:
                onShowPagamentoStone();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onShowFormaPagamento() {
        movimentoParcelaFragment = (MovimentoParcelaFragment) getSupportFragmentManager().findFragmentByTag("pagamentoFragment");

        if (movimentoParcelaFragment == null) {
            movimentoParcelaFragment = new MovimentoParcelaFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, movimentoParcelaFragment, "pagamentoFragment");
            ft.commit();
        }
    }

    private void onShowPagamentoStone() {
        PagamentoStoneFragment pagamentoStoneFragment = (PagamentoStoneFragment) getSupportFragmentManager().findFragmentByTag("pagamentoStoneFragment");

        if (pagamentoStoneFragment == null) {
            pagamentoStoneFragment = new PagamentoStoneFragment();

            pagamentoStoneFragment.setTargetFragment(movimentoParcelaFragment, 0);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, pagamentoStoneFragment, "pagamentoStoneFragment");
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
