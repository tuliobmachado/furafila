package br.com.informsistemas.furafila.controller;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.dao.FormaPagamentoDAO;
import br.com.informsistemas.furafila.models.dao.MaterialDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoItemDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoParcelaDAO;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.helper.PrintNFCe;
import br.com.informsistemas.furafila.models.pojo.DadosImpressao;
import br.com.informsistemas.furafila.models.pojo.FormaPagamento;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.utils.CPFCNPJMask;

public class ResumoActivity extends AppCompatActivity {

    private LinearLayout linearLayoutParceiro;
    private LinearLayout linearLayoutMaterial;
    private LinearLayout linearLayoutPagamento;
    private LinearLayout linearLayoutBottomEnviar;
    private EditText edtTxtObservacao;
    private Button btnSalvarPedido;
    private Button btnEnviarPedido;
    private float total_ipi;
    private float total_icmssubst;
    private float total_fecoepst;
    private float total_material;
    private DadosImpressao dadosImpressao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            dadosImpressao = (DadosImpressao) getIntent().getExtras().getSerializable("dadosImpressao");
        }

        linearLayoutParceiro = findViewById(R.id.layout_resumo_parceiro);
        linearLayoutMaterial = findViewById(R.id.layout_resumo_material);
        linearLayoutPagamento = findViewById(R.id.layout_resumo_pagamento);
        linearLayoutBottomEnviar = findViewById(R.id.layout_bottom_enviar);
        btnEnviarPedido = findViewById(R.id.btn_enviar_pedido);
        btnSalvarPedido = findViewById(R.id.btn_salvar_pedido);
        edtTxtObservacao = findViewById(R.id.edtObservacao);

        if (!Constants.MOVIMENTO.atual.observacao.equals("")) {
            edtTxtObservacao.setText(Constants.MOVIMENTO.atual.observacao);
        }

        if (Constants.MOVIMENTO.atual.sincronizado.equals("T") || Constants.MOVIMENTO.atual.sincronizado.equals("P")) {
            if (Constants.MOVIMENTO.atual.sincronizado.equals("T")) {
                linearLayoutBottomEnviar.setVisibility(View.GONE);
            } else {
                linearLayoutBottomEnviar.setVisibility(View.VISIBLE);
                btnSalvarPedido.setVisibility(View.GONE);
            }
            edtTxtObservacao.setEnabled(false);
        }

        setTitle("Resumo Pedido");

        Button btnEnviarPedido = findViewById(R.id.btn_enviar_pedido);
        btnEnviarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSalvarPedido(true);
            }
        });

        Button btnSalvarPedido = findViewById(R.id.btn_salvar_pedido);
        btnSalvarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSalvarPedido(false);
            }
        });

        getDadosParceiro();
        getDadosItens();
        getDadosFormaPagamentos();
        getDadosTotais();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_print_nfce, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            //Back button
            case R.id.action_print_nfce:
                PrintNFCe.execute(ResumoActivity.this, dadosImpressao);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enviarSalvarPedido(Boolean enviar) {
        if (!edtTxtObservacao.getText().toString().equals("")) {
            Constants.MOVIMENTO.atual.observacao = edtTxtObservacao.getText().toString();
        }

        if (Constants.MOVIMENTO.atual.datafim == null) {
            Constants.MOVIMENTO.atual.datafim = new Date();
        } else {
            Constants.MOVIMENTO.atual.dataalteracao = new Date();
        }

        MovimentoDAO.getInstance(ResumoActivity.this).createOrUpdate(Constants.MOVIMENTO.atual);

        Constants.MOVIMENTO.enviarPedido = enviar;
        if (enviar) {
            Constants.PEDIDO.movimento = MovimentoDAO.getInstance(ResumoActivity.this).findById(Constants.MOVIMENTO.atual.id);
            Constants.PEDIDO.PEDIDOATUAL = 2;
            Constants.PEDIDO.listPedidos = null;
            Constants.PEDIDO.listPedidos = new ArrayList<>();
            Constants.PEDIDO.listPedidos.add(Constants.PEDIDO.movimento.id);
        }
        Intent intent = new Intent(ResumoActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void getDadosParceiro() {
        Parceiro p = ParceiroDAO.getInstance(this).findByIdAuxiliar("codigoparceiro", Constants.MOVIMENTO.atual.codigoparceiro);
        TextView txtDescricao = new TextView(this);
        TextView txtCPFCGC = new TextView(this);

        txtDescricao.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        txtDescricao.setText(p.descricao);
        txtDescricao.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        txtDescricao.setTypeface(null, Typeface.BOLD);

        txtCPFCGC.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        txtCPFCGC.setText(CPFCNPJMask.getMask(p.cpfcgc));
        txtCPFCGC.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        linearLayoutParceiro.addView(txtDescricao);
        linearLayoutParceiro.addView(txtCPFCGC);
    }

    private void getDadosItens() {
        Constants.PEDIDO.movimentoItems = MovimentoItemDAO.getInstance(this).findByMovimentoId(Constants.MOVIMENTO.atual.id);

        TextView txtTitleDescricao = findViewById(R.id.txt_resumo_title_material_descricao);
        TextView txtTitleTotal = findViewById(R.id.txt_resumo_title_material_total);

        txtTitleDescricao.setText(Constants.PEDIDO.movimentoItems.size() + "x PRODUTOS");
        txtTitleTotal.setText("R$ " + Misc.formatMoeda(Constants.MOVIMENTO.atual.totalliquido));

        for (int i = 0; i < Constants.PEDIDO.movimentoItems.size(); i++) {
            Material m = MaterialDAO.getInstance(this).findByIdAuxiliar("codigomaterial", Constants.PEDIDO.movimentoItems.get(i).codigomaterial);

            linearLayoutMaterial.addView(newTextView(Constants.PEDIDO.movimentoItems.get(i).quantidade + "x" + m.descricao));

            total_fecoepst = total_fecoepst + Constants.PEDIDO.movimentoItems.get(i).valoricmsfecoepst;
            total_ipi = total_ipi + Constants.PEDIDO.movimentoItems.get(i).valoripi;
            total_icmssubst = total_icmssubst + Constants.PEDIDO.movimentoItems.get(i).valoricmssubst;

            total_material = total_material + (Constants.PEDIDO.movimentoItems.get(i).custo * Constants.PEDIDO.movimentoItems.get(i).quantidade);
        }
    }

    private void getDadosFormaPagamentos() {
        Constants.PEDIDO.movimentoParcelas = MovimentoParcelaDAO.getInstance(this).findByMovimentoId(Constants.MOVIMENTO.atual.id);

        FormaPagamento p = FormaPagamentoDAO.getInstance(this).findByIdAuxiliar("codigoforma", Constants.PEDIDO.movimentoParcelas.get(0).codigoforma);
        linearLayoutPagamento.addView(newTextView(p.descricao));
    }

    private void getDadosTotais() {
        TextView txtTitleDescricao = findViewById(R.id.txt_resumo_title_total);
        TextView txtTitleTotal = findViewById(R.id.txt_resumo_title_valor_material);
        TextView txttitleTotalIPI = findViewById(R.id.txt_resumo_title_total_ipi);
        TextView txtTitleTotalICMSSubst = findViewById(R.id.txt_resumo_title_total_icmssubst);
        TextView txtTitleTotalFecoepST = findViewById(R.id.txt_resumo_title_total_fecoepst);

        txtTitleDescricao.setText("Total Material");
        txtTitleTotal.setText("R$ " + Misc.formatMoeda(total_material));

        txttitleTotalIPI.setText("R$ " + Misc.formatMoeda(total_ipi));
        txtTitleTotalICMSSubst.setText("R$ " + Misc.formatMoeda(total_icmssubst));
        txtTitleTotalFecoepST.setText("R$ " + Misc.formatMoeda(total_fecoepst));
    }

    private TextView newTextView(String descricao) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(descricao);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        return textView;
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            Constants.PEDIDO.movimentoParcelas = null;
            Constants.PEDIDO.movimentoItems = null;
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
