package br.com.informsistemas.furafila.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.adapter.MaterialSearchApiAdapter;
import br.com.informsistemas.furafila.controller.rest.RestManager;
import br.com.informsistemas.furafila.models.callback.MaterialService;
import br.com.informsistemas.furafila.models.dao.MovimentoItemDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Categoria;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.models.utils.IOnBackPressed;
import br.com.informsistemas.furafila.models.utils.PaginationScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialSearchApiFragment extends Fragment implements IOnBackPressed, MaterialSearchApiAdapter.onMaterialSearchApiListener{

    ProgressBar progressBar;
    private static final String TAG = "MaterialApiFragment";
    private SearchView searchView;
    private RecyclerView recyclerView;
    private MaterialService materialService;
    private MaterialSearchApiAdapter materialAdapter;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;

    private Categoria categoriaFiltro;
    private TextView txtTotalItem;
    private TextView txtCategoriaSelecionada;
    private TabLayout tabLayout;
    private CategoriaApiFragment categoriaApiFragment;
    private List<Material> listMaterialMovItens;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        LinearLayout linearLayout = getActivity().findViewById(R.id.layout_categoria);
        linearLayout.setVisibility(View.VISIBLE);
        Button btn = getActivity().findViewById(R.id.btn_selecionar_pagamento);
        btn.setVisibility(View.GONE);

        txtCategoriaSelecionada = getActivity().findViewById(R.id.txt_categoria_selecionada);

        tabLayout = getActivity().findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.clearOnTabSelectedListeners();
        tabLayout.addOnTabSelectedListener(getOnTabSelectedListener());

        txtTotalItem = getActivity().findViewById(R.id.txt_total_item);
        txtTotalItem.setText("R$ " + Misc.formatMoeda(Constants.MOVIMENTO.atual.totalliquido));

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        progressBar = view.findViewById(R.id.main_progress);

        if (materialAdapter == null) {
            materialAdapter = new MaterialSearchApiAdapter(getActivity(), this);
        }

        listMaterialMovItens = (List<Material>) getArguments().getSerializable("listMaterialSelecionados");
        getArguments().clear();

        if (listMaterialMovItens != null){
            for (Material material: listMaterialMovItens){
                materialAdapter.addSelecionado(material);
            }
        }

        txtTotalItem = getActivity().findViewById(R.id.txt_total_item);
        txtTotalItem.setText("R$ " + Misc.formatMoeda(Constants.MOVIMENTO.atual.totalliquido));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(materialAdapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener(llm) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        carregarProximaPagina();
                    }
                }, 1000);
            }

            @Override
            public int getCurrentPage(){
                return currentPage;
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        if (materialService == null || categoriaFiltro != null) {
            materialService = new RestManager().getMaterialService();
            carregarPrimeiraPagina();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3 || newText.equals("")) {
                    materialAdapter.clear();
                    carregarPrimeiraPagina();
                }
                return false;
            }
        });
        searchView.setQueryHint("Pesquisar Material...");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                searchView.clearFocus();
                getActivity().onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void carregarPrimeiraPagina() {
        Log.d(TAG, "carregarPrimeiraPagina: ");
        progressBar.setVisibility(View.VISIBLE);
        currentPage = 0;
        TOTAL_PAGES = 1;
        isLastPage = false;

        callMaterialListarApi().enqueue(new Callback<RestResponse<Material>>() {
            @Override
            public void onResponse(Call<RestResponse<Material>> call, Response<RestResponse<Material>> response) {
                // Got data. Send it to adapter

                RestResponse<Material> materiais = response.body();
                progressBar.setVisibility(View.GONE);
                materialAdapter.addAll(materiais.data);

                if (currentPage < TOTAL_PAGES){
                    if (currentPage == 0 && TOTAL_PAGES == 1){
                        TOTAL_PAGES = (materiais.meta.quantity/materiais.meta.rowset);
                    }

                    if (TOTAL_PAGES == 0){
                        isLastPage = true;
                    }else {
                        materialAdapter.addLoadingFooter();
                    }
                }else{
                    isLastPage = true;
                }
            }

            @Override
            public void onFailure(Call<RestResponse<Material>> call, final Throwable t) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }

    private void carregarProximaPagina() {
        Log.d(TAG, "carregarProximaPagina: " + currentPage);

        callMaterialListarApi().enqueue(new Callback<RestResponse<Material>>() {
            @Override
            public void onResponse(Call<RestResponse<Material>> call, Response<RestResponse<Material>> response) {
                materialAdapter.removeLoadingFooter();
                isLoading = false;

                RestResponse<Material> materiais = response.body();
                materialAdapter.addAll(materiais.data);

                if (currentPage != TOTAL_PAGES){
                    materialAdapter.addLoadingFooter();
                }else{
                    isLastPage = true;
                }
            }

            @Override
            public void onFailure(Call<RestResponse<Material>> call, final Throwable t) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }

    private Call<RestResponse<Material>> callMaterialListarApi() {
        String pesquisa = null;
        String categoria = null;

        if (searchView != null){
            pesquisa = "'"+searchView.getQuery().toString()+"'";
        }

        if (categoriaFiltro != null){
            categoria = categoriaFiltro.codigogrupo;

            if (categoria.equals("000")){
                categoria = null;
            }

            categoria = (categoria == null ? null : "'"+categoria+"'");
        }

        return materialService.getListar(
                "'"+ Constants.DTO.registro.codigoconfiguracao+"'",
                "'"+ Constants.DTO.registro.cnpj+"'",
                pesquisa,
                categoria,
                currentPage
        );
    }

    @Override
    public void onBotaoExcluirClick(int position) {
        Material material = null;
        Material materialExclusao = null;
        float valorRemovido = 0;
        float valorQtdTotal = 0;
        float valorQtdReduzida = 0;

        materialAdapter.getItem(position).quantidade -= 1;

        try {
            material = Misc.cloneMaterial(materialAdapter.getItem(position));
            material.quantidade += 1;
            material.totalliquido = (material.precovenda1 * material.quantidade);
            materialExclusao = Misc.cloneMaterial(materialAdapter.getItem(position));
            materialExclusao.totalliquido = (materialExclusao.precovenda1 * materialExclusao.quantidade);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (materialAdapter.getItem(position).quantidade >= 1){
            valorQtdTotal = material.totalliquido;
            valorQtdReduzida = materialExclusao.totalliquido;
            valorRemovido = valorQtdTotal - valorQtdReduzida;
        }else{
            valorRemovido = material.totalliquido;
        }

        if (materialAdapter.getItem(position).quantidade == 0){
            removeMovimentoItem(materialAdapter.getItem(position).codigomaterial);
            materialAdapter.removeSelecionado(materialExclusao);
        }else{
            materialAdapter.addSelecionado(materialExclusao);
        }

        setTotal(-valorRemovido);

        materialAdapter.notifyItemChanged(position);
    }

    @Override
    public void onMaterialClick(int position) {
        Material material = null;
        Material materialExclusao = null;

        materialAdapter.getItem(position).quantidade += 1;

        try {
            material = Misc.cloneMaterial(materialAdapter.getItem(position));
            materialExclusao = Misc.cloneMaterial(materialAdapter.getItem(position));
            materialExclusao.quantidade -= 1;
            materialExclusao.totalliquido = (materialExclusao.precovenda1 * materialExclusao.quantidade);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (materialAdapter.getItem(position).quantidade > 1){
            Constants.MOVIMENTO.atual.totalliquido = Constants.MOVIMENTO.atual.totalliquido - materialExclusao.totalliquido;
        }

        material.totalliquido = (material.precovenda1 * material.quantidade);
        materialAdapter.addSelecionado(material);

        setTotal(material.totalliquido);

        materialAdapter.notifyItemChanged(position);
    }

    @Override
    public void onMaterialLongClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putSerializable("material", materialAdapter.getItem(position));

        DialogFragment fragmentModal = MaterialSearchModalFragment.newInstance();
        fragmentModal.setTargetFragment(this, 1);
        fragmentModal.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentModal.show(ft, "materialSearchModalFragment");
    }

    private void setTotal(float valor) {
        float valorAtual = Constants.MOVIMENTO.atual.totalliquido;

        valorAtual = valorAtual + valor;

        if (valorAtual < 0){
            valorAtual = 0;
        }

        txtTotalItem.setText("R$ " + Misc.formatMoeda(valorAtual));

        Constants.MOVIMENTO.atual.totalliquido = valorAtual;
    }

    private void AdicionarMaterialQuantidade(Intent data){
        int position = data.getExtras().getInt("position");
        float qtdNova = data.getExtras().getFloat("quantidade");
        float qtdAtual = materialAdapter.getItem(position).quantidade;
        float vezes = 0;
        boolean excluir = false;

        if (qtdNova > qtdAtual){
            vezes = qtdNova - qtdAtual;
        }else{
            excluir = true;
            vezes = qtdAtual - qtdNova;
        }

        for (int i = 0; i < vezes; i++) {
            if (excluir){
                onBotaoExcluirClick(position);
            }else{
                onMaterialClick(position);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                categoriaFiltro = (Categoria) data.getExtras().getSerializable("Categoria");
                materialAdapter.clear();
                tabLayout.getTabAt(0).select();
                break;
            case 1:
                AdicionarMaterialQuantidade(data);
                break;
        }
    }

    private TabLayout.OnTabSelectedListener getOnTabSelectedListener(){
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        if (categoriaApiFragment != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        break;
                    case 1:
                        categoriaApiFragment = (CategoriaApiFragment) getActivity().getSupportFragmentManager().findFragmentByTag("categoriaApiFragment");

                        if (categoriaApiFragment == null) {
                            categoriaApiFragment = new CategoriaApiFragment();
                        }

                        categoriaApiFragment.setTargetFragment(MaterialSearchApiFragment.this, 0);
                        categoriaApiFragment.setArguments(getCategoriaFiltro());
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, categoriaApiFragment, "categoriaApiFragment");
                        ft.addToBackStack(null);
                        ft.commit();

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    private Bundle getCategoriaFiltro() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Categoria", categoriaFiltro);

        return bundle;
    }

    @Override
    public boolean onBackPressed() {
        searchView.clearFocus();
        tabLayout.setVisibility(View.GONE);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getIntent());
        return false;
    }

    private Intent getIntent() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("listMaterial", (Serializable) materialAdapter.getMaterialSelecionados());
        intent.putExtras(bundle);

        return intent;
    }

    private void removeMovimentoItem(String codigomaterial){
        if (Constants.MOVIMENTO.atual.id != null) {
            MovimentoItem movimentoItem = MovimentoItemDAO.getInstance(getActivity()).findByMovimentoIdItem(Constants.MOVIMENTO.atual.id, codigomaterial);

            if (movimentoItem != null){
                MovimentoItemDAO.getInstance(getActivity()).deleteByMovimentoId(Constants.MOVIMENTO.atual.id, codigomaterial);
            }
        }
    }
}
