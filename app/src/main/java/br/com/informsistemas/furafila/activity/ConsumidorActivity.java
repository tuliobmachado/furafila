package br.com.informsistemas.furafila.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.fragments.ConsumidorFragment;
import br.com.informsistemas.furafila.controller.fragments.ConsumidorModalFragment;
import br.com.informsistemas.furafila.controller.fragments.ParceiroSearchApiFragment;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.utils.IOnBackPressed;

public class ConsumidorActivity extends AppCompatActivity {

    private ConsumidorFragment consumidorFragment;
    private ParceiroSearchApiFragment consumidorApiFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parceiro);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Consumidor");

        Button btnSelecionarProduto = findViewById(R.id.btn_selecionar_produto);
        btnSelecionarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowProdutos();
            }
        });

        if (Constants.MOVIMENTO.atual.codigoparceiro != null){
            Constants.MOVIMENTO.parceiro = ParceiroDAO.getInstance(this).findByIdAuxiliar("codigoparceiro", Constants.MOVIMENTO.atual.codigoparceiro);
        }

        onShowParceiro();

        if ((Constants.MOVIMENTO.atual.cpf == null) || Constants.MOVIMENTO.atual.resgate.equals("T")) {
            perguntaCPF();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Back button
            case R.id.action_search_list:
                onShowParceiroApi();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onShowParceiro(){
        consumidorFragment = (ConsumidorFragment) getSupportFragmentManager().findFragmentByTag("consumidorFragment");

        if (consumidorFragment == null){
            consumidorFragment = new ConsumidorFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, consumidorFragment, "consumidorFragment");
            ft.commit();
        }
    }

    private void onShowParceiroApi(){
        consumidorApiFragment = (ParceiroSearchApiFragment) getSupportFragmentManager().findFragmentByTag("consumidorApiFragment");

        if (consumidorApiFragment == null){
            consumidorApiFragment = new ParceiroSearchApiFragment();

            consumidorApiFragment.setTargetFragment(consumidorFragment, 0);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, consumidorApiFragment, "consumidorApiFragment");
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void onShowModalCPF(){
        DialogFragment fragmentModal = ConsumidorModalFragment.newInstance();
        fragmentModal.setTargetFragment(consumidorFragment, 1);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragmentModal.show(ft, "pagamentoStoneModalFragment");
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("parceiroFragment");

        if (count == 0) {
            super.onBackPressed();
            setResult(Activity.RESULT_OK, new Intent());
            //additional code
        } else {
            if ((fragment instanceof IOnBackPressed)){
                ((IOnBackPressed) fragment).onBackPressed();
            }

            getSupportFragmentManager().popBackStack();

        }
    }

    private void perguntaCPF(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogDefault);
        builder.setMessage("Deseja informar CPF na nota?");
        builder.setCancelable(false);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Constants.MOVIMENTO.atual.resgate.equals("T")){
                    Constants.MOVIMENTO.atual.cpf = Constants.MOVIMENTO.parceiro.cpfcgc;
                    onShowProdutos();
                }else {
                    onShowModalCPF();
                }
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Constants.MOVIMENTO.atual.cpf = "";
                onShowProdutos();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void onShowProdutos(){
        Intent intent = new Intent(ConsumidorActivity.this, MovimentoItemActivity.class);
        startActivity(intent);
    }
}
