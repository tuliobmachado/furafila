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
import android.widget.SearchView;

import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.adapter.CategoriaAdapter;
import br.com.informsistemas.furafila.models.dao.CategoriaDAO;
import br.com.informsistemas.furafila.models.pojo.Categoria;
import br.com.informsistemas.furafila.interfaces.ItemClickListener;

public class CategoriaFragment extends Fragment implements ItemClickListener {

    private List<Categoria> listCategoria;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private CategoriaAdapter categoriaAdapter;
    private Categoria categoriaSelecionada;

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

        listCategoria = CategoriaDAO.getInstance(getActivity()).getListCategoria();

        setAdapter(listCategoria);

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
                listCategoria = CategoriaDAO.getInstance(getActivity()).pesquisaLista(newText);
                setAdapter(listCategoria);
                return false;
            }
        });
//        searchView.setFocusable(true);
//        searchView.setIconified(false);
        searchView.setQueryHint("Pesquisar Categorias...");

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

    private void setAdapter(List<Categoria> list){
        categoriaAdapter = new CategoriaAdapter(getActivity(), list, this);
        if (categoriaSelecionada != null){
            categoriaAdapter.setIdSelecionado(categoriaSelecionada.id);
        }else{
            categoriaAdapter.setIdSelecionado(1);
        }
        recyclerView.setAdapter(categoriaAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Categoria", listCategoria.get(position));
        intent.putExtras(bundle);
        searchView.clearFocus();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    @Override
    public void onItemClickLong(int position) {

    }
}
