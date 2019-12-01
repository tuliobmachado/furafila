package br.com.informsistemas.furafila.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.adapter.ParceiroSearchAdapter;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.interfaces.ItemClickListener;

public class ParceiroSearchFragment extends Fragment implements ItemClickListener {

    private List<Parceiro> listParceiro;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ParceiroSearchAdapter parceiroSearchAdapter;

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

        listParceiro = ParceiroDAO.getInstance(getActivity()).getListParceiro();

        setAdapter(listParceiro);

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
                listParceiro = ParceiroDAO.getInstance(getActivity()).pesquisaLista(newText);
                setAdapter(listParceiro);
                return false;
            }
        });
//        searchView.setFocusable(true);
//        searchView.setIconified(false);
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

    private void setAdapter(List<Parceiro> list){
        parceiroSearchAdapter = new ParceiroSearchAdapter(getActivity(), list, this);
        recyclerView.setAdapter(parceiroSearchAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Parceiro", listParceiro.get(position));
        intent.putExtras(bundle);
        searchView.clearFocus();

        if (Constants.MOVIMENTO.atual.datainicio == null){
            Constants.MOVIMENTO.atual.datainicio = new Date();
        }

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onItemClickLong(int position) {

    }
}
