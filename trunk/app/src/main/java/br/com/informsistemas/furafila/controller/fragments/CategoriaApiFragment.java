package br.com.informsistemas.furafila.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.SearchView;

import java.util.Date;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.adapter.CategoriaApiAdapter;
import br.com.informsistemas.furafila.controller.rest.RestManager;
import br.com.informsistemas.furafila.models.callback.CategoriaService;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.Categoria;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.models.utils.PaginationScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaApiFragment extends Fragment implements CategoriaApiAdapter.onCategoriaApiListener {

    ProgressBar progressBar;
    private static final String TAG = "CategoriaApiFragment";
    private SearchView searchView;
    private RecyclerView recyclerView;
    private CategoriaService categoriaService;
    private CategoriaApiAdapter categoriaAdapter;
    private Categoria categoriaSelecionada;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        categoriaSelecionada = (Categoria) getArguments().getSerializable("Categoria");

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        progressBar = view.findViewById(R.id.main_progress);

        if (categoriaAdapter == null) {
            categoriaAdapter = new CategoriaApiAdapter(getActivity(), this);
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(categoriaAdapter);
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

        if (categoriaService == null) {
            categoriaService = new RestManager().getCategoriaService();
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
                if (newText.length() >= 3 || newText.equals("")){
                    categoriaAdapter.clear();
                    carregarPrimeiraPagina();
                }
                return false;
            }
        });
        searchView.setQueryHint("Pesquisar Categoria...");

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

        callCategoriaListarApi().enqueue(new Callback<RestResponse<Categoria>>() {
            @Override
            public void onResponse(Call<RestResponse<Categoria>> call, Response<RestResponse<Categoria>> response) {
                // Got data. Send it to adapter

                RestResponse<Categoria> categorias = response.body();
                progressBar.setVisibility(View.GONE);
                categoriaAdapter.add(new Categoria("000", "TODOS", new Date()));
                categoriaAdapter.addAll(categorias.data);

                if (categoriaSelecionada != null){
                    categoriaAdapter.setCategoriaSelecionada(categoriaSelecionada);
                }else {
                    categoriaAdapter.setCategoriaSelecionada(categoriaAdapter.getItem(0));
                }

                if (currentPage < TOTAL_PAGES){
                    if (currentPage == 0 && TOTAL_PAGES == 1){
                        categorias.meta.quantity += 1;
                        TOTAL_PAGES = (categorias.meta.quantity/categorias.meta.rowset);
                    }

                    if (TOTAL_PAGES == 0){
                        isLastPage = true;
                    }else {
                        categoriaAdapter.addLoadingFooter();
                    }
                }else{
                    isLastPage = true;
                }
            }

            @Override
            public void onFailure(Call<RestResponse<Categoria>> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });
    }

    private void carregarProximaPagina() {
        Log.d(TAG, "carregarProximaPagina: " + currentPage);

        callCategoriaListarApi().enqueue(new Callback<RestResponse<Categoria>>() {
            @Override
            public void onResponse(Call<RestResponse<Categoria>> call, Response<RestResponse<Categoria>> response) {
                categoriaAdapter.removeLoadingFooter();
                isLoading = false;

                RestResponse<Categoria> categorias = response.body();
                categoriaAdapter.addAll(categorias.data);

                if (currentPage != TOTAL_PAGES){
                    categoriaAdapter.addLoadingFooter();
                }else{
                    isLastPage = true;
                }
            }

            @Override
            public void onFailure(Call<RestResponse<Categoria>> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });
    }

    private Call<RestResponse<Categoria>> callCategoriaListarApi() {
        String pesquisa = null;

        if (searchView != null){
            pesquisa = "'"+searchView.getQuery().toString()+"'";
        }
        return categoriaService.getListar(
                "'"+ Constants.DTO.registro.codigoconfiguracao+"'",
                "'"+ Constants.DTO.registro.cnpj+"'",
                pesquisa,
                currentPage
        );
    }

    @Override
    public void onClick(int position) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Categoria", categoriaAdapter.getItem(position));
        intent.putExtras(bundle);
        searchView.clearFocus();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
