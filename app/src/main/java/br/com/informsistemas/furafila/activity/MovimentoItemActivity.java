package br.com.informsistemas.furafila.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.fragments.MaterialSearchApiFragment;
import br.com.informsistemas.furafila.controller.fragments.MovimentoItemFragment;
import br.com.informsistemas.furafila.models.dao.MovimentoItemDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.utils.IOnBackPressed;

public class MovimentoItemActivity extends AppCompatActivity {

    private MovimentoItemFragment movimentoItemFragment;
    private MaterialSearchApiFragment materialSearchApiFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimento_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.GONE);
        tabLayout.addTab(tabLayout.newTab().setText("Produtos"));
        tabLayout.addTab(tabLayout.newTab().setText("Categorias"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Materiais");

        Button btn = findViewById(R.id.btn_selecionar_pagamento);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.MOVIMENTO.atual.id != null) {
                    if (MovimentoItemDAO.getInstance(MovimentoItemActivity.this).findByMovimentoId(Constants.MOVIMENTO.atual.id).size() > 0) {
                        Intent intent = new Intent(MovimentoItemActivity.this, MovimentoParcelaActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MovimentoItemActivity.this, "Necessário informar um material", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MovimentoItemActivity.this, "Necessário informar um material", Toast.LENGTH_LONG).show();
                }
            }
        });

        onShowMovimentoItem();

        if (Constants.MOVIMENTO.atual.id == null) {
            onShowSearchApiMaterial();
        }
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
                onShowSearchApiMaterial();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onShowMovimentoItem() {
        movimentoItemFragment = (MovimentoItemFragment) getSupportFragmentManager().findFragmentByTag("movimentoItemFragment");

        if (movimentoItemFragment == null) {
            movimentoItemFragment = new MovimentoItemFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, movimentoItemFragment, "movimentoItemFragment");
            ft.commit();
        }
    }

    private void onShowSearchApiMaterial() {
        materialSearchApiFragment = (MaterialSearchApiFragment) getSupportFragmentManager().findFragmentByTag("materialSearchApiFragment");

        if (materialSearchApiFragment == null) {
            materialSearchApiFragment = new MaterialSearchApiFragment();

            materialSearchApiFragment.setTargetFragment(movimentoItemFragment, 0);
            materialSearchApiFragment.setArguments(getListMovimentoItem());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, materialSearchApiFragment, "materialSearchApiFragment");
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("materialSearchFragment");
        Fragment fragmentApi = getSupportFragmentManager().findFragmentByTag("materialSearchApiFragment");

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            if ((fragment instanceof IOnBackPressed)) {
                ((IOnBackPressed) fragment).onBackPressed();
            }

            if ((fragmentApi instanceof IOnBackPressed)) {
                ((IOnBackPressed) fragmentApi).onBackPressed();
            }

            getSupportFragmentManager().popBackStack();

        }
    }

    private Bundle getListMovimentoItem() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("listMaterialSelecionados", (Serializable) movimentoItemFragment.getListMaterialSelecionados());

        return bundle;
    }
}
