package br.com.informsistemas.furafila.controller.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.activity.MainActivity;
import br.com.informsistemas.furafila.controller.adapter.ParceiroVencimentoAdapter;
import br.com.informsistemas.furafila.models.dao.ParceiroVencimentoDAO;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.pojo.ParceiroVencimento;

public class ParceiroVencimentoFragment extends Fragment {

    private List<ParceiroVencimento> listParceiroVencimento;
    private RecyclerView recyclerView;
    private ParceiroVencimentoAdapter parceiroVencimentoAdapter;
    private TabLayout tabLayout;

    @Override
    public void onDestroy() {
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

        getActivity().setTitle("Consulta Parceiro");
        tabLayout = getActivity().findViewById(R.id.tab_layout_parceiro);
        tabLayout.setVisibility(View.VISIBLE);

        ((MainActivity) getActivity()).onSetIndexMenu(1, 1);
        ((MainActivity) getActivity()).onSetItemMenu();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        Parceiro parceiro = (Parceiro) getArguments().getSerializable("Parceiro");

        listParceiroVencimento = ParceiroVencimentoDAO.getInstance(getActivity()).findAllByIdAuxiliar("codigoparceiro", parceiro.codigoparceiro);
        setAdapter(listParceiroVencimento);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setAdapter(List<ParceiroVencimento> list) {
        parceiroVencimentoAdapter = new ParceiroVencimentoAdapter(getActivity(), list);
        recyclerView.setAdapter(parceiroVencimentoAdapter);
    }
}
