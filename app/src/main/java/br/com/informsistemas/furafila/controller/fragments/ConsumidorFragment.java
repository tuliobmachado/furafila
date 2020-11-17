package br.com.informsistemas.furafila.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.adapter.ParceiroAdapter;
import br.com.informsistemas.furafila.dao.ModoPagamentoDAO;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Enums;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.utils.RecyclerItemClickListener;

public class ConsumidorFragment extends Fragment {

    private static final String TAG = "ConsumidorFragment";
    private List<Parceiro> listConsumidor;
    private RecyclerView recyclerView;
    private ParceiroAdapter consumidorAdapter;
    private RecyclerView.OnItemTouchListener listener;
    private boolean isMultiSelect = false;
    private ActionMode actionMode;
    private ActionMode.Callback callback;
    private List<Integer> selectedIds = new ArrayList<>();

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
        btn.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        getConsumidor();

        setAdapter(listConsumidor);
        listener = getListener();
        recyclerView.addOnItemTouchListener(listener);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (listConsumidor.size() > 0) {
            menu.clear();
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Parceiro p = null;
        String cpf = null;

        if (requestCode == 1) {
            cpf = data.getExtras().getString("cpf");
            cpf = Misc.ApenasNumeros(cpf);
            p = ParceiroDAO.getInstance(getActivity()).findByIdAuxiliar("cpfcgc", cpf);

            if (p == null) {
                p = new Parceiro(Constants.DTO.registro.codigoparceiropadrao, "CONSUMIDOR FINAL", cpf, "", "", "", "", "", "", "", "", "",
                            new Date(), new Date(), new Date(), Float.valueOf("0"), Float.valueOf("0"), Float.valueOf("0"), "", "", "", "", "", "", "", new Date(), "", "", "", "", "", Float.valueOf("0"), "", "");
            }

            Constants.MOVIMENTO.atual.codigoparceiro = p.codigoparceiro;
            Constants.MOVIMENTO.atual.cpf = cpf;
        }else{
            p = (Parceiro) data.getExtras().getSerializable("Parceiro");
            Constants.MOVIMENTO.atual.codigoparceiro = p.codigoparceiro;
        }

        SalvarParceiro(p);

        getTabelas(p);

        listConsumidor.add(p);
        getActivity().invalidateOptionsMenu();
    }

    public void getConsumidor(){
        if (listConsumidor == null){
            listConsumidor = new ArrayList<>();
        }

        if (Constants.MOVIMENTO.atual.id != null){
            Parceiro p = null;
            if  (Misc.isNullOrEmpty(Constants.MOVIMENTO.atual.cpf)) {
                p = ParceiroDAO.getInstance(getActivity()).findByIdAuxiliar("codigoparceiro", Constants.MOVIMENTO.atual.codigoparceiro);
            }else{
                p = ParceiroDAO.getInstance(getActivity()).findByIdAuxiliar("cpfcgc", Constants.MOVIMENTO.atual.cpf);
            }

            getTabelas(p);

            listConsumidor.add(p);
        }
    }

    private void SalvarParceiro(Parceiro p){
        Parceiro parceiro = ParceiroDAO.getInstance(getActivity()).findByIdAuxiliar("cpfcgc", p.cpfcgc);

        if (parceiro != null){
            ParceiroDAO.getInstance(getActivity()).delete(parceiro);
        }

        ParceiroDAO.getInstance(getActivity()).createOrUpdate(p);
    }

    private void setAdapter(List<Parceiro> list){
        consumidorAdapter = new ParceiroAdapter(getActivity(), list);
        recyclerView.setAdapter(consumidorAdapter);
    }

    private RecyclerView.OnItemTouchListener getListener(){
        callback = getCallback();
        return  new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect){
                    multiSelect(listConsumidor.get(position).id);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (Constants.MOVIMENTO.atual.resgate.equals("T")){
                    Toast.makeText(getActivity(), "Não é permitido selecionar parceiro de uma venda resgatada", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isMultiSelect){
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;

                    if (actionMode == null){
                        actionMode = getActivity().startActionMode(callback);
                    }
                }

                multiSelect(listConsumidor.get(position).id);
            }
        });
    }

    private ActionMode.Callback getCallback(){
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_delete:

                        for (int i : selectedIds){
                            for (int y = 0; y < listConsumidor.size(); y++) {
                                if (listConsumidor.get(y).id == i){
                                    deleteItem(y);
                                }
                            }
                        }
                        mode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                isMultiSelect = false;
                selectedIds = new ArrayList<>();
                consumidorAdapter.setSelectedIds(selectedIds);
            }
        };
    }

    private void deleteItem(int position){
        listConsumidor.remove(position);
        consumidorAdapter.notifyItemRemoved(position);
        consumidorAdapter.notifyItemRangeChanged(position, consumidorAdapter.getItemCount());
        Constants.MOVIMENTO.atual.codigoparceiro = null;
        Misc.setTabelasPadrao();
        getActivity().invalidateOptionsMenu();
    }

    private void multiSelect(int position){
        if (actionMode != null){
            if (selectedIds.contains(position)) {
                for (int i = 0; i < selectedIds.size(); i++) {
                    if (selectedIds.get(i) == position)
                        selectedIds.remove(i);
                }
            }else {
                selectedIds.add(position);
            }

            if (selectedIds.size() > 0){
                actionMode.setTitle(String.valueOf(selectedIds.size()));
            }else{
                actionMode.setTitle("");
                actionMode.finish();
            }

            consumidorAdapter.setSelectedIds(selectedIds);
        }
    }

    private void getTabelas(Parceiro p){
        if (Constants.APP.TIPO_APLICACAO == Enums.TIPO_APLICACAO.FORCA_DE_VENDAS) {
            if (!p.codigotabelapreco.equals("")) {
                Constants.MOVIMENTO.codigotabelapreco = p.codigotabelapreco;
                Constants.MOVIMENTO.atual.codigotabelapreco = p.codigotabelapreco;
            }

            if (!p.codigoformapagamento.equals("")) {
                if (ModoPagamentoDAO.getInstance(getActivity()).findByIdAuxiliar("codigoforma", p.codigoformapagamento) != null) {
                    Constants.MOVIMENTO.codigoformapagamento = p.codigoformapagamento;
                }
            }

            if (p.percdescontopadrao > 0) {
                Constants.MOVIMENTO.percdescontopadrao = p.percdescontopadrao;
            }

            Constants.MOVIMENTO.estadoParceiro = p.estado;
        }else{
            Constants.MOVIMENTO.codigoformapagamento = Constants.DTO.registro.codigoformapagamento;
        }
    }
}
