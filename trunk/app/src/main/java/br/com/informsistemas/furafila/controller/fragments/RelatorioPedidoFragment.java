package br.com.informsistemas.furafila.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.MainActivity;
import br.com.informsistemas.furafila.controller.ResumoActivity;
import br.com.informsistemas.furafila.controller.adapter.RelatorioPedidoAdapter;
import br.com.informsistemas.furafila.models.dao.MovimentoDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.interfaces.ItemClickListener;

public class RelatorioPedidoFragment extends Fragment implements ItemClickListener {

    private List<Movimento> listMovimento;
    private RecyclerView recyclerView;
    private RelatorioPedidoAdapter relatorioPedidoAdapter;
    private TabLayout tabLayout;
    private TextView edtQuantidadePedido;
    private TextView edtTotalPedido;
    private LinearLayout layoutTotalPedido;

    @Override
    public void onDestroyView() {
        layoutTotalPedido.setVisibility(View.GONE);
        super.onDestroyView();
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

        getActivity().setTitle("Relatório de Vendas");

        layoutTotalPedido = getActivity().findViewById(R.id.layout_relatorio_pedido);
        layoutTotalPedido.setVisibility(View.VISIBLE);
        edtQuantidadePedido = getActivity().findViewById(R.id.txt_relatorio_pedido_quantidade);
        edtTotalPedido = getActivity().findViewById(R.id.txt_relatorio_pedido_valor_total);
        zeraDados();
        FloatingActionButton btn = getActivity().findViewById(R.id.fab_adicionar_pedido);
        btn.setVisibility(View.GONE);

        ((MainActivity) getActivity()).onSetIndexMenu(2, 0);
        ((MainActivity) getActivity()).onSetItemMenu();

        tabLayout = getActivity().findViewById(R.id.tab_layout_parceiro);
        tabLayout.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        listMovimento = new ArrayList<>();
        setAdapter(listMovimento);

        recyclerView.setLayoutManager(llm);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_relatorio, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
            case R.id.action_search_relatorio:
                DialogFragment fragmentModal = RelatorioPedidoModalFragment.newInstance();
                fragmentModal.setTargetFragment(this, 1);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentModal.show(ft, "relatorioPedidoModalFragment");
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setAdapter(List<Movimento> list) {
        relatorioPedidoAdapter = new RelatorioPedidoAdapter(getActivity(), list, this);
        recyclerView.setAdapter(relatorioPedidoAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Parceiro parceiro = (Parceiro) data.getExtras().getSerializable("parceiro");
        String dataInicio = data.getStringExtra("dataInicio");
        String dataFim = data.getStringExtra("dataFim");

        if (!dataInicio.equals("") && !dataFim.equals("")) {
            listMovimento = MovimentoDAO.getInstance(getActivity()).getMovimentoPeriodo((parceiro == null) ? "" : parceiro.codigoparceiro ,
                    Misc.getStringToDate(dataInicio, "dd/MM/yyyy"), Misc.getStringToDate(dataFim, "dd/MM/yyyy"));
            setAdapter(listMovimento);

            setQuantidade(listMovimento.size());
            setTotal(listMovimento);
        }

    }

    private void zeraDados() {
        edtQuantidadePedido.setText("0 Pedidos");
        edtTotalPedido.setText("R$ " + Misc.formatMoeda(0));
    }

    private void setQuantidade(Integer quantidade) {
        if (quantidade > 1) {
            edtQuantidadePedido.setText(String.valueOf(quantidade) + " Pedidos");
        } else {
            edtQuantidadePedido.setText(String.valueOf(quantidade) + " Pedido");
        }

    }

    private void setTotal(List<Movimento> list) {

        float value = 0;

        for (int i = 0; i < list.size(); i++) {
            value = value + list.get(i).totalliquido;
        }

        edtTotalPedido.setText("R$ " + Misc.formatMoeda(value));
    }

    @Override
    public void onItemClick(int position) {
        Misc.setTabelasPadrao();
        Constants.MOVIMENTO.atual = listMovimento.get(position);

        Intent intent = new Intent(getActivity(), ResumoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClickLong(int position) {

    }
}
