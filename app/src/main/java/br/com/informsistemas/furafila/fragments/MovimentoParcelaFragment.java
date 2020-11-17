package br.com.informsistemas.furafila.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.adapter.MovimentoParcelaAdapter;
import br.com.informsistemas.furafila.model.ModoPagamento;
import br.com.informsistemas.furafila.models.dao.MovimentoItemDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoParcelaDAO;
import br.com.informsistemas.furafila.models.helper.CalculoClass;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;
import br.com.informsistemas.furafila.models.pojo.MovimentoParcela;
import br.com.informsistemas.furafila.models.utils.RecyclerItemClickListener;

public class MovimentoParcelaFragment extends Fragment {

    private List<MovimentoParcela> listMovimentoParcela;
    private RecyclerView recyclerView;
    private MovimentoParcelaAdapter movimentoParcelaAdapter;
    private RecyclerView.OnItemTouchListener listener;
    private boolean isMultiSelect = false;
    private ActionMode actionMode;
    private ActionMode.Callback callback;
    private List<Integer> selectedIds = new ArrayList<>();
    private Button btn;
    private TextView txtTotalItem;
    private TextView txtTotalTroco;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        btn = getActivity().findViewById(R.id.btn_resumo_pedido);
        btn.setVisibility(View.VISIBLE);

        txtTotalItem = getActivity().findViewById(R.id.txt_total_pagamento);
        txtTotalTroco = getActivity().findViewById(R.id.txt_pagamento_total_troco);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        calculaTotal();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        getMovimentoParcela();

        setAdapter(listMovimentoParcela);
        listener = getListener();
        recyclerView.addOnItemTouchListener(listener);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (listMovimentoParcela.size() > 0) {
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
        MovimentoParcela movimentoParcela = null;
        ModoPagamento m = (ModoPagamento) data.getExtras().getSerializable("modoPagamento");
        Float valorPago = data.getFloatExtra("valorPago", 0);
        Float valorTroco = data.getFloatExtra("valorTroco", 0);

        movimentoParcela = new MovimentoParcela(Constants.MOVIMENTO.atual,
                Constants.DTO.registro.listdadosnfce.get(0).codigoforma, m.codigotipoevento, m.descricao, valorPago, valorTroco);

        if (movimentoParcela != null) {
            MovimentoParcelaDAO.getInstance(getActivity()).createOrUpdate(movimentoParcela);
        }
    }

    private void getMovimentoParcela(){
        RecalcularTotalMovimento();

        listMovimentoParcela = MovimentoParcelaDAO.getInstance(getActivity()).findByMovimentoId(Constants.MOVIMENTO.atual.id);
    }

    private void setAdapter(List<MovimentoParcela> list){
        movimentoParcelaAdapter = new MovimentoParcelaAdapter(getActivity(), list);
        recyclerView.setAdapter(movimentoParcelaAdapter);
    }

    private RecyclerView.OnItemTouchListener getListener(){
        callback = getCallback();
        return  new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect){
                    multiSelect(listMovimentoParcela.get(position).id);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (Constants.MOVIMENTO.atual.resgate.equals("T")){
                    Toast.makeText(getActivity(), "Não é permitido selecionar um pagamento de uma venda resgatada!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isMultiSelect){
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;

                    if (actionMode == null){
                        actionMode = getActivity().startActionMode(callback);
                    }
                }

                multiSelect(listMovimentoParcela.get(position).id);
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
                            for (int y = 0; y < listMovimentoParcela.size(); y++) {
                                if (listMovimentoParcela.get(y).id == i){
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
                movimentoParcelaAdapter.setSelectedIds(selectedIds);
            }
        };
    }

    private void deleteItem(int position){
        MovimentoParcelaDAO.getInstance(getActivity()).delete(listMovimentoParcela.get(position));
        listMovimentoParcela.remove(position);
        movimentoParcelaAdapter.notifyItemRemoved(position);
        movimentoParcelaAdapter.notifyItemRangeChanged(position, movimentoParcelaAdapter.getItemCount());
        getActivity().invalidateOptionsMenu();
        calculaTotal();
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

            movimentoParcelaAdapter.setSelectedIds(selectedIds);
        }
    }

    private void RecalcularTotalMovimento(){
        List<MovimentoItem> listMovItens = MovimentoItemDAO.getInstance(getActivity()).findByMovimentoId(Constants.MOVIMENTO.atual.id);

        if (listMovItens.size() > 0) {
            CalculoClass calculoClass = new CalculoClass(getActivity(), null);
            calculoClass.recalcularMovimento(Constants.MOVIMENTO.atual, listMovItens);
        }
    }

    private void calculaTotal(){
        Constants.MOVIMENTO.total_pendente = (Constants.MOVIMENTO.atual.totalliquido - MovimentoParcelaDAO.getInstance(getActivity()).sumByMovimentoId("valor", Constants.MOVIMENTO.atual.id));

        if (Constants.MOVIMENTO.total_pendente < 0){
            Constants.MOVIMENTO.total_pendente = 0;
        }

        Constants.MOVIMENTO.total_troco = MovimentoParcelaDAO.getInstance(getActivity()).sumByMovimentoId("troco", Constants.MOVIMENTO.atual.id);

        txtTotalItem.setText("R$ " + Misc.formatMoeda(Constants.MOVIMENTO.total_pendente));
        txtTotalTroco.setText("R$ " + Misc.formatMoeda(Constants.MOVIMENTO.total_troco));
    }
}
