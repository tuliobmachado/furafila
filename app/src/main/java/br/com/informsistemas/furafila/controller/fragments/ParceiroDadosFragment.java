package br.com.informsistemas.furafila.controller.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.activity.MainActivity;
import br.com.informsistemas.furafila.controller.adapter.ParceiroDadosAdapter;
import br.com.informsistemas.furafila.models.pojo.Parceiro;

public class ParceiroDadosFragment extends Fragment {

    private List<Parceiro> listParceiro;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private ParceiroDadosAdapter parceiroDadosAdapter;
    private ParceiroVencimentoFragment parceiroVencimentoFragment;

    @Override
    public void onDestroy() {
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

        getActivity().setTitle("Consulta Parceiro");

        ((MainActivity) getActivity()).onSetIndexMenu(1, 1);
        ((MainActivity) getActivity()).onSetItemMenu();

        tabLayout = getActivity().findViewById(R.id.tab_layout_parceiro);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.getTabAt(0).select();
        tabLayout.clearOnTabSelectedListeners();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        if (parceiroVencimentoFragment != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        break;
                    case 1:
                        parceiroVencimentoFragment = (ParceiroVencimentoFragment) getActivity().getSupportFragmentManager().findFragmentByTag("parceiroLancamentoFragment");

                        if (parceiroVencimentoFragment == null) {
                            parceiroVencimentoFragment = new ParceiroVencimentoFragment();
                        }

                        parceiroVencimentoFragment.setTargetFragment(ParceiroDadosFragment.this, 0);
                        parceiroVencimentoFragment.setArguments(getDadosArguments());
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, parceiroVencimentoFragment, "parceiroLancamentoFragment");
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
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        Parceiro parceiro = (Parceiro) getArguments().getSerializable("Parceiro");

        listParceiro = new ArrayList<>();
        listParceiro.add(parceiro);

        setAdapter(listParceiro);

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

    private void setAdapter(List<Parceiro> list) {
        parceiroDadosAdapter = new ParceiroDadosAdapter(getActivity(), list);
        recyclerView.setAdapter(parceiroDadosAdapter);
    }

    private Bundle getDadosArguments() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Parceiro", listParceiro.get(0));

        return bundle;
    }
}
