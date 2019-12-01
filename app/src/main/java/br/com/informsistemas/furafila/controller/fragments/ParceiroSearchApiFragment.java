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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;

import java.util.Date;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.adapter.ParceiroSearchApiAdapter;
import br.com.informsistemas.furafila.controller.rest.RestManager;
import br.com.informsistemas.furafila.models.callback.ParceiroService;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.models.utils.PaginationScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParceiroSearchApiFragment extends Fragment implements ParceiroSearchApiAdapter.onParceiroSearchApiListener {

    ProgressBar progressBar;
    private static final String TAG = "ParceiroApiFragment";
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ParceiroService parceiroService;
    private ParceiroSearchApiAdapter parceiroAdapter;
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

        Button btn = getActivity().findViewById(R.id.btn_selecionar_produto);
        btn.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        progressBar = view.findViewById(R.id.main_progress);
        parceiroAdapter = new ParceiroSearchApiAdapter(getActivity(), this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(parceiroAdapter);
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

        parceiroService = new RestManager().getParceiroService();
        carregarPrimeiraPagina();

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
                    parceiroAdapter.clear();
                    carregarPrimeiraPagina();
                }
                return false;
            }
        });
        searchView.setQueryHint("Pesquisar Parceiro...");

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

        callParceiroListarApi().enqueue(new Callback<RestResponse<Parceiro>>() {
            @Override
            public void onResponse(Call<RestResponse<Parceiro>> call, Response<RestResponse<Parceiro>> response) {
                // Got data. Send it to adapter

                RestResponse<Parceiro> parceiros = response.body();
                progressBar.setVisibility(View.GONE);
                parceiroAdapter.addAll(parceiros.data);

                if (currentPage < TOTAL_PAGES){
                    if (currentPage == 0 && TOTAL_PAGES == 1){
                        TOTAL_PAGES = (parceiros.meta.quantity/parceiros.meta.rowset);
                    }

                    if (TOTAL_PAGES == 0){
                        isLastPage = true;
                    }else {
                        parceiroAdapter.addLoadingFooter();
                    }
                }else{
                    isLastPage = true;
                }
            }

            @Override
            public void onFailure(Call<RestResponse<Parceiro>> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });
    }

    private void carregarProximaPagina() {
        Log.d(TAG, "carregarProximaPagina: " + currentPage);

        callParceiroListarApi().enqueue(new Callback<RestResponse<Parceiro>>() {
            @Override
            public void onResponse(Call<RestResponse<Parceiro>> call, Response<RestResponse<Parceiro>> response) {
                parceiroAdapter.removeLoadingFooter();
                isLoading = false;

                RestResponse<Parceiro> parceiros = response.body();
                parceiroAdapter.addAll(parceiros.data);

                if (currentPage != TOTAL_PAGES){
                    parceiroAdapter.addLoadingFooter();
                }else{
                    isLastPage = true;
                }
            }

            @Override
            public void onFailure(Call<RestResponse<Parceiro>> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });
    }

    private Call<RestResponse<Parceiro>> callParceiroListarApi() {
        String pesquisa = null;

        if (searchView != null){
            pesquisa = "'"+searchView.getQuery().toString()+"'";
        }
        return parceiroService.getListar(
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
        bundle.putSerializable("Parceiro", parceiroAdapter.getItem(position));
        intent.putExtras(bundle);
        searchView.clearFocus();

        if (Constants.MOVIMENTO.atual.datainicio == null){
            Constants.MOVIMENTO.atual.datainicio = new Date();
        }

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
