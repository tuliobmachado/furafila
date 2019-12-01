package br.com.informsistemas.furafila.controller;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.activity.ResgateActivity;
import br.com.informsistemas.furafila.controller.fragments.MaterialSaldoFragment;
import br.com.informsistemas.furafila.controller.fragments.MovimentoFragment;
import br.com.informsistemas.furafila.controller.fragments.ParceiroConsultaFragment;
import br.com.informsistemas.furafila.controller.fragments.RelatorioPedidoFragment;
import br.com.informsistemas.furafila.models.dao.DatabaseManager;
import br.com.informsistemas.furafila.models.dao.FormaPagamentoDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoDAO;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.dao.RegistroDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.FormaPagamento;
import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.pojo.Registro;
import br.com.informsistemas.furafila.models.utils.IOnBackPressed;
import br.com.informsistemas.furafila.service.ClosingService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private NavigationView navigationView;
    private String tagFragment;
    private Fragment movimentoFragment;
    private int indexMenu;
    private int indexSubMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout_parceiro);
        tabLayout.setVisibility(View.GONE);
        tabLayout.addTab(tabLayout.newTab().setText("Dados"));
        tabLayout.addTab(tabLayout.newTab().setText("Títulos"));

        DatabaseManager.init(this);

        FloatingActionButton fab = findViewById(R.id.fab_adicionar_pedido);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowPedido();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        indexMenu = 0;
        indexSubMenu = 0;
        navigationView.getMenu().getItem(indexMenu).setChecked(true);

        ChecaPermissoes();

        Intent closeService = new Intent(this, ClosingService.class);
        startService(closeService);

        onShow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resgate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Back button
            case R.id.action_resgate:
                onShowResgate();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pedido) {
            onShowFragment("movimentoFragment");
            onSetIndexMenu(0, 0);
        } else if (id == R.id.nav_consulta_parceiro) {
            onShowFragment("parceiroConsultaFragment");
            onSetIndexMenu(1, 1);
        } else if (id == R.id.nav_consulta_estoque) {
            onShowFragment("materialSaldoFragment");
            onSetIndexMenu(1, 2);
        } else if (id == R.id.nav_relatorio_pedido) {
            onShowFragment("relatorioPedidoFragment");
            onSetIndexMenu(2, 0);
        } else if (id == R.id.nav_relatorio_parceiro) {
            onSetIndexMenu(2, 1);
        } else if (id == R.id.nav_relatorio_material) {
            onSetIndexMenu(2, 2);
        } else if (id == R.id.nav_configuracoes_limpeza) {
            apagarPedidos();
            onSetItemMenu();
        } else if (id == R.id.nav_configuracoes_sincronia) {
            ((MovimentoFragment) movimentoFragment).getSincronia(true);
        } else if (id == R.id.nav_configuracoes_sair) {
            logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.MAIN_REQUEST_CODE.ACESSO_PENDENTE || requestCode == Constants.MAIN_REQUEST_CODE.REGISTRO_PENDENTE) {
            Registro reg = null;
            try {
                reg = (Registro) data.getExtras().getSerializable("Registro");
            } catch (Exception e) {
                reg = null;
            }

            if (reg != null) {
                if (Constants.DTO.registro == null) {
                    if (reg.listdadosnfce.size() > 0) {
                        reg.codigoformapagamento = reg.listdadosnfce.get(0).codigoforma;

                        if (!reg.listdadosnfce.get(0).codigodinheiro.equals("")) {
                            CriaFormaPagamentoPadrao(0, reg.listdadosnfce.get(0).codigodinheiro);
                        }

                        if (!reg.listdadosnfce.get(0).codigocredito.equals("")) {
                            CriaFormaPagamentoPadrao(1, reg.listdadosnfce.get(0).codigocredito);
                        }

                        if (!reg.listdadosnfce.get(0).codigodebito.equals("")) {
                            CriaFormaPagamentoPadrao(2, reg.listdadosnfce.get(0).codigodebito);
                        }
                    }

                    if (!reg.codigoparceiropadrao.equals("")) {
                        CriaParceiroPadrao(reg.codigoparceiropadrao, reg.descricaoparceiropadrao);
                    }

                    RegistroDAO.getInstance(this).createOrUpdate(reg);
                } else {
                    Constants.DTO.registro.status = reg.status;
                    RegistroDAO.getInstance(this).createOrUpdate(Constants.DTO.registro);
                }
            }
        }else{
            Movimento movimento = (Movimento) data.getExtras().getSerializable("movimento");

            Constants.MOVIMENTO.atual = movimento;

            Intent intent = new Intent(MainActivity.this, ConsumidorActivity.class);
            startActivityForResult(intent, 0);
        }

        onShow();
    }

    private void CriaParceiroPadrao(String codigoParceiro, String descricao) {
        Parceiro p = new Parceiro(codigoParceiro, descricao, "", "", "", "", "", "", "", "", "", "",
                new Date(), new Date(), new Date(), Float.valueOf("0"), Float.valueOf("0"), Float.valueOf("0"), "", "", "", "", "", "", "", new Date(), "", "", "", "", "", Float.valueOf("0"), "", "");

        ParceiroDAO.getInstance(this).createOrUpdate(p);

    }

    private void CriaFormaPagamentoPadrao(Integer position, String codigotipoevento) {
        FormaPagamento formaPagamento = null;

        switch (position) {
            case 0:
                formaPagamento = FormaPagamentoDAO.getInstance(this).findByIdAuxiliar("codigoforma", "0001");

                if (formaPagamento == null) {
                    formaPagamento = new FormaPagamento("0001", codigotipoevento, "DINHEIRO", Misc.getDataPadrao(), Float.parseFloat("0"), Float.parseFloat("0"));
                }
                break;
            case 1:
                formaPagamento = FormaPagamentoDAO.getInstance(this).findByIdAuxiliar("codigoforma", "0002");

                if (formaPagamento == null) {
                    formaPagamento = new FormaPagamento("0002", codigotipoevento, "CARTÃO DE CRÉDITO", Misc.getDataPadrao(), Float.parseFloat("0"), Float.parseFloat("0"));
                }
                break;
            case 2:
                formaPagamento = FormaPagamentoDAO.getInstance(this).findByIdAuxiliar("codigoforma", "0003");

                if (formaPagamento == null) {
                    formaPagamento = new FormaPagamento("0003", codigotipoevento, "CARTÃO DE DÉBITO", Misc.getDataPadrao(), Float.parseFloat("0"), Float.parseFloat("0"));
                }
                break;
        }

        formaPagamento.codigotipoevento = codigotipoevento;
        FormaPagamentoDAO.getInstance(this).createOrUpdate(formaPagamento);
    }


    private void onShow() {
        Constants.DTO.registro = RegistroDAO.getInstance(this).findFirst();

        if (Constants.DTO.registro == null) {
            onShowLogin();
        } else {
            Misc.setAplicacao(Constants.DTO.registro.codigoaplicacao);
            onShowPrincipal();
        }
    }

    private void onShowLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE.REGISTRO_PENDENTE);
    }

    private void onShowResgate() {
        Intent intent = new Intent(this, ResgateActivity.class);
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE.RESGATE_IMPORTAR);
    }

    private void onShowPrincipal() {
        if (Constants.DTO.registro.status.equals("P")) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("Status", "P");
            startActivityForResult(intent, Constants.MAIN_REQUEST_CODE.ACESSO_PENDENTE);
        } else {
            Intent intent = new Intent(this, ValidationStoneActivity.class);
            startActivity(intent);

            onShowFragment("movimentoFragment");
        }
    }

    private void onShowFragment(String tag) {
        tagFragment = tag;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (tag) {
            case "movimentoFragment":
                int count = getSupportFragmentManager().getBackStackEntryCount();

                for (int i = 0; i < count; i++) {
                    onBackPressed();
                }

                movimentoFragment = getSupportFragmentManager().findFragmentByTag(tag);

                if (movimentoFragment == null) {
                    movimentoFragment = new MovimentoFragment();
                    ft.replace(R.id.fragment_container, movimentoFragment, tag);
                } else {
                    ft.detach(movimentoFragment);
                    ft.attach(movimentoFragment);
                }
                break;
            case "parceiroConsultaFragment":
                ParceiroConsultaFragment parceiroConsultaFragment = (ParceiroConsultaFragment) getSupportFragmentManager().findFragmentByTag(tag);

                if (parceiroConsultaFragment == null) {
                    parceiroConsultaFragment = new ParceiroConsultaFragment();
                }

                ft.replace(R.id.fragment_container, parceiroConsultaFragment, tag);
                ft.addToBackStack(null);

                break;

            case "materialSaldoFragment":
                MaterialSaldoFragment materialSaldoFragment = (MaterialSaldoFragment) getSupportFragmentManager().findFragmentByTag(tag);

                if (materialSaldoFragment == null) {
                    materialSaldoFragment = new MaterialSaldoFragment();
                }

                ft.replace(R.id.fragment_container, materialSaldoFragment, tag);
                ft.addToBackStack(null);

                break;

            case "relatorioPedidoFragment":
                RelatorioPedidoFragment relatorioPedidoFragment = (RelatorioPedidoFragment) getSupportFragmentManager().findFragmentByTag(tag);

                if (relatorioPedidoFragment == null) {
                    relatorioPedidoFragment = new RelatorioPedidoFragment();
                }

                ft.replace(R.id.fragment_container, relatorioPedidoFragment, tag);
                ft.addToBackStack(null);

                break;
        }

        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tagFragment);

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            if ((fragment instanceof IOnBackPressed)) {
                ((IOnBackPressed) fragment).onBackPressed();
            }

            getSupportFragmentManager().popBackStack();

            if (count == 1) {
                unCheckAllMenuItems(navigationView.getMenu());
                navigationView.getMenu().getItem(0).setChecked(true);
            }
        }
    }

    private void unCheckAllMenuItems(@NonNull final Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                // Un check sub menu items
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    public void onShowPedido() {
        Misc.setTabelasPadrao();
        Constants.MOVIMENTO.atual = new Movimento(Constants.MOVIMENTO.codigoempresa,
                Constants.MOVIMENTO.codigofilialcontabil, Constants.MOVIMENTO.codigoalmoxarifado,
                Constants.MOVIMENTO.codigooperacao, Constants.MOVIMENTO.codigotabelapreco,
                null, "", 0, "", Misc.GetDateAtual(),
                null, null, null, "", "", Misc.gerarMD5(),
                null, "", 0);
        Intent intent = new Intent(MainActivity.this, ConsumidorActivity.class);
        startActivityForResult(intent, 0);
    }

    public void onSetIndexMenu(int iMenu, int iSubMenu) {
        indexMenu = iMenu;
        indexSubMenu = iSubMenu;
    }

    public void onSetItemMenu() {
        unCheckAllMenuItems(navigationView.getMenu());

        if (indexMenu == 0) {
            navigationView.getMenu().getItem(0).setChecked(true);
        } else {
            navigationView.getMenu().getItem(indexMenu).getSubMenu().getItem(indexSubMenu).setChecked(true);
        }
    }

    private void apagarPedidos() {
        MovimentoDAO.getInstance(this).deleteAllPedidos();
        onShowFragment("movimentoFragment");
    }

    private void logout() {
//        if (MovimentoDAO.getInstance(this).pedidoPendente()) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogDefault);
//            builder.setMessage("Existem pedidos não sincronizados! Deseja realmente sair? Eles serão apagados");
//            builder.setCancelable(false);
//            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    deslogar();
//                    dialog.cancel();
//                }
//            });
//            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    indexMenu = 0;
//                    indexSubMenu = 0;
//                    navigationView.getMenu().getItem(indexMenu).setChecked(true);
//                    dialog.cancel();
//                }
//            });
//
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        }else{
        deslogar();
//        }
    }

    private void deslogar() {
        RegistroDAO.getInstance(this).deleteAll();
        indexMenu = 0;
        indexSubMenu = 0;
        navigationView.getMenu().getItem(indexMenu).setChecked(true);
        movimentoFragment.getFragmentManager().beginTransaction().remove(movimentoFragment).commit();
        onShow();
    }

    private void ChecaPermissoes() {
        Constants.PERMISSION.READ_PHONE_STATE = Misc.GetReturnPermission(this, Manifest.permission.READ_PHONE_STATE);
    }

    @Override
    public void onDestroy() {
        if (!Constants.MOVIMENTO.enviarPedido) {
            RegistroDAO.getInstance(this).deleteAll();
        }
        super.onDestroy();
    }
}
