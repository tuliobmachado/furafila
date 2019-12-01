package br.com.informsistemas.furafila.controller.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
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
import android.widget.SearchView;

import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.MainActivity;
import br.com.informsistemas.furafila.controller.adapter.ParceiroConsultaAdapter;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.interfaces.ItemClickListener;

public class ParceiroConsultaFragment extends Fragment implements ItemClickListener {

    private List<Parceiro> listParceiro;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ParceiroConsultaAdapter parceiroConsultaAdapter;
    private TabLayout tabLayout;

    @Override
    public void onDestroy() {
        searchView.clearFocus();
        tabLayout.setVisibility(View.GONE);
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        tabLayout = getActivity().findViewById(R.id.tab_layout_parceiro);
        tabLayout.setVisibility(View.GONE);

        getActivity().setTitle("Consulta Parceiro");
        ((MainActivity) getActivity()).onSetIndexMenu(1, 1);
        ((MainActivity) getActivity()).onSetItemMenu();

        FloatingActionButton btn = getActivity().findViewById(R.id.fab_adicionar_pedido);
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

    private void setAdapter(List<Parceiro> list) {
        parceiroConsultaAdapter = new ParceiroConsultaAdapter(getActivity(), list, this);
        recyclerView.setAdapter(parceiroConsultaAdapter);
    }

    private Bundle getDadosArguments(int position){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Parceiro", listParceiro.get(position));

        return bundle;
    }

    @Override
    public void onItemClick(int position) {
        searchView.clearFocus();

        ParceiroDadosFragment parceiroDadosFragment = (ParceiroDadosFragment) getActivity().getSupportFragmentManager().findFragmentByTag("parceiroDadosFragment");

        if (parceiroDadosFragment == null) {
            parceiroDadosFragment = new ParceiroDadosFragment();
        }

        parceiroDadosFragment.setTargetFragment(ParceiroConsultaFragment.this, 0);
        parceiroDadosFragment.setArguments(getDadosArguments(position));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, parceiroDadosFragment, "parceiroDadosFragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onItemClickLong(int position) {

    }
}
