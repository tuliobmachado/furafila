package br.com.informsistemas.furafila.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.ConsumidorActivity;
import br.com.informsistemas.furafila.controller.MainActivity;
import br.com.informsistemas.furafila.controller.ResumoActivity;
import br.com.informsistemas.furafila.controller.adapter.MovimentoAdapter;
import br.com.informsistemas.furafila.models.dao.AtualizacaoDAO;
import br.com.informsistemas.furafila.models.dao.DadosImpressaoDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoItemDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoParcelaDAO;
import br.com.informsistemas.furafila.models.dao.PagamentoImpressaoDAO;
import br.com.informsistemas.furafila.models.dao.ProdutoImpressaoDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Enums;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Atualizacao;
import br.com.informsistemas.furafila.models.pojo.Categoria;
import br.com.informsistemas.furafila.models.pojo.CategoriaMaterial;
import br.com.informsistemas.furafila.models.pojo.DadosImpressao;
import br.com.informsistemas.furafila.models.pojo.FormaPagamento;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.MaterialEstado;
import br.com.informsistemas.furafila.models.pojo.MaterialSaldo;
import br.com.informsistemas.furafila.models.pojo.MetaFuncionario;
import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;
import br.com.informsistemas.furafila.models.pojo.MovimentoParcela;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.pojo.ParceiroVencimento;
import br.com.informsistemas.furafila.models.pojo.TabelaPrecoItem;
import br.com.informsistemas.furafila.tasks.DadosTabelasTask;
import br.com.informsistemas.furafila.tasks.PedidoTask;
import br.com.informsistemas.furafila.tasks.RequestSincroniaTask;
import br.com.informsistemas.furafila.tasks.ResponseSincroniaTask;
import br.com.informsistemas.furafila.tasks.SincroniaTask;
import br.com.informsistemas.furafila.models.utils.RecyclerItemClickListener;

public class MovimentoFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private List<Movimento> listMovimento;
    private MovimentoAdapter movimentoAdapter;
    private RecyclerView.OnItemTouchListener listener;
    private boolean isMultiSelect = false;
    private ActionMode actionMode;
    private ActionMode.Callback callback;
    private List<Integer> selectedIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        getActivity().setTitle("Vendas");

        ((MainActivity) getActivity()).onSetIndexMenu(0, 0);
        ((MainActivity) getActivity()).onSetItemMenu();

        TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout_parceiro);
        tabLayout.setVisibility(View.GONE);
        FloatingActionButton btn = getActivity().findViewById(R.id.fab_adicionar_pedido);
        btn.setVisibility(View.VISIBLE);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txtHeader = headerView.findViewById(R.id.txtHeader);
        TextView txtVersao = headerView.findViewById(R.id.txtVersao);
        txtHeader.setText("Usuário Logado: " + Constants.DTO.registro.usuario);
        txtVersao.setText("v" + Constants.APP.VERSAO);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        listMovimento = MovimentoDAO.getInstance(getActivity()).getMovimentoPeriodo("", Misc.GetDateAtual(), Misc.GetDateAtual());

        setAdapter(listMovimento);
        listener = getListener();
        recyclerView.addOnItemTouchListener(listener);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Misc.verificaConexao(getActivity())) {
            if (Constants.MOVIMENTO.enviarPedido) {
                enviarPedido();
                Constants.MOVIMENTO.enviarPedido = false;
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (listMovimento.size() > 0) {
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

    private RecyclerView.OnItemTouchListener getListener() {
        callback = getCallback();
        final Intent[] intent = new Intent[1];
        return new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    if (listMovimento.get(position).sincronizado.equals("T")) {
                        Toast.makeText(getActivity(), "Não é possível selecionar um pedido sincronizado!", Toast.LENGTH_LONG).show();
                    } else {
                        multiSelect(listMovimento.get(position).id);
                    }
                } else {
                    Misc.setTabelasPadrao();
                    Constants.MOVIMENTO.atual = listMovimento.get(position);

                    if (Constants.MOVIMENTO.atual.sincronizado.equals("T") || Constants.MOVIMENTO.atual.sincronizado.equals("P")) {
                        Intent intent = new Intent(getActivity(), ResumoActivity.class);

                        DadosImpressao dadosImpressao = DadosImpressaoDAO.getInstance(getActivity()).findByIdAuxiliar("movimento_id", Constants.MOVIMENTO.atual.id);

                        if (dadosImpressao != null) {
                            dadosImpressao.ListaFormasPagamento = PagamentoImpressaoDAO.getInstance(getActivity()).findAllByIdAuxiliar("movimento_id", Constants.MOVIMENTO.atual.id);
                            dadosImpressao.ListaProdutos = ProdutoImpressaoDAO.getInstance(getActivity()).findAllByIdAuxiliar("movimento_id", Constants.MOVIMENTO.atual.id);

                            Bundle dados = new Bundle();
                            dados.putSerializable("dadosImpressao", dadosImpressao);
                            intent.putExtras(dados);
                        }

                        startActivity(intent);
                    } else {
                        intent[0] = new Intent(getActivity(), ConsumidorActivity.class);
                        getActivity().startActivityForResult(intent[0], 0);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

                if (listMovimento.get(position).sincronizado.equals("T")) {
                    Toast.makeText(getActivity(), "Não é possível selecionar um pedido sincronizado!", Toast.LENGTH_LONG).show();
                } else {

                    if (!isMultiSelect) {
                        selectedIds = new ArrayList<>();
                        isMultiSelect = true;

                        if (actionMode == null) {
                            actionMode = getActivity().startActionMode(callback);
                        }
                    }

                    multiSelect(listMovimento.get(position).id);
                }
            }
        });
    }

    private ActionMode.Callback getCallback() {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:

                        for (int i : selectedIds) {
                            for (int y = 0; y < listMovimento.size(); y++) {
                                if (listMovimento.get(y).id == i) {
                                    deleteItem(y);
                                }
                            }
                        }
                        mode.finish();
                        break;

                    case R.id.action_sync:
                        Constants.PEDIDO.listPedidos = new ArrayList<>();
                        for (int i : selectedIds) {
                            checaMovimento(i);
                        }
                        if (Constants.PEDIDO.listPedidos.size() > 0) {
                            Constants.PEDIDO.PEDIDOATUAL = 1;
                            verificaPedido(Constants.PEDIDO.PEDIDOATUAL);
                        }
                        mode.finish();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                isMultiSelect = false;
                selectedIds = new ArrayList<>();
                movimentoAdapter.setSelectedIds(selectedIds);
            }
        };
    }

    private void deleteItem(int position) {
        if (checaMovimentoPendente(position)) {
            MovimentoDAO.getInstance(getActivity()).delete(listMovimento.get(position));
            listMovimento.remove(position);
            movimentoAdapter.notifyItemRemoved(position);
            movimentoAdapter.notifyItemRangeChanged(position, movimentoAdapter.getItemCount());
            getActivity().invalidateOptionsMenu();
        } else {
            Toast.makeText(getActivity(), "Não é possível excluir um pedido pendente!", Toast.LENGTH_LONG).show();
        }
    }

    private void multiSelect(int position) {
        if (actionMode != null) {
            if (selectedIds.contains(position)) {
                for (int i = 0; i < selectedIds.size(); i++) {
                    if (selectedIds.get(i) == position)
                        selectedIds.remove(i);
                }
            } else {
                selectedIds.add(position);
            }

            if (selectedIds.size() > 0) {
                actionMode.setTitle(String.valueOf(selectedIds.size()));
            } else {
                actionMode.setTitle("");
                actionMode.finish();
            }

            movimentoAdapter.setSelectedIds(selectedIds);
        }
    }

    private void setAdapter(List<Movimento> list) {
        movimentoAdapter = new MovimentoAdapter(getActivity(), list);
        recyclerView.setAdapter(movimentoAdapter);
    }

    public void getSincronia(Boolean solicitaSincronia) {
        Constants.SINCRONIA.TABELA_ATUAL = 0;
        if (Misc.verificaConexao(getActivity())) {
            SincroniaTask sincroniaTask = new SincroniaTask(this, solicitaSincronia);
            sincroniaTask.execute();
        }
    }

    public void atualizaLista() {
        listMovimento = MovimentoDAO.getInstance(getActivity()).findAll();
        setAdapter(listMovimento);
    }

    public void VerificaSincronia(int i) {

        if (i < Constants.DTO.listAtualizacaoServidor.size()) {
            Atualizacao atualizacao = null;
            Enums.TIPO_SINCRONIA tipoSincronia = Enums.TIPO_SINCRONIA.NENHUMA;
            Date dataAtualizacao = null;
            String codigoconfiguracao = "";

            atualizacao = AtualizacaoDAO.getInstance(getActivity()).findByNomeTabela(Constants.DTO.listAtualizacaoServidor.get(i).nometabela);

            if (atualizacao == null) {
                tipoSincronia = Enums.TIPO_SINCRONIA.TOTAL;
                dataAtualizacao = Constants.DTO.listAtualizacaoServidor.get(i).datasinctotal;
            } else {
                if (atualizacao.datasinctotal.compareTo(Constants.DTO.listAtualizacaoServidor.get(i).datasinctotal) < 0) {
                    tipoSincronia = Enums.TIPO_SINCRONIA.TOTAL;
                    dataAtualizacao = Constants.DTO.listAtualizacaoServidor.get(i).datasinctotal;
                } else if (atualizacao.datasincparcial.compareTo(Constants.DTO.listAtualizacaoServidor.get(i).datasincparcial) < 0) {
                    tipoSincronia = Enums.TIPO_SINCRONIA.PARCIAL;
                    dataAtualizacao = atualizacao.datasincparcial;
                } else if (atualizacao.datasincmarcado.compareTo(Constants.DTO.listAtualizacaoServidor.get(i).datasincmarcado) < 0) {
                    tipoSincronia = Enums.TIPO_SINCRONIA.MARCADOS;
                    dataAtualizacao = atualizacao.datasincmarcado;
                }
            }

            codigoconfiguracao = Constants.DTO.listAtualizacaoServidor.get(i).codigoconfiguracao;

            if (tipoSincronia != Enums.TIPO_SINCRONIA.NENHUMA) {

                switch (Constants.DTO.listAtualizacaoServidor.get(i).nometabela) {
                    case "PVCADMATERIAL":
                        getSinc(Material.class, Constants.DTO.listAtualizacaoServidor.get(i), "Material 1/3", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                    case "PVMATERIALESTADO":
                        getSinc(MaterialEstado.class, Constants.DTO.listAtualizacaoServidor.get(i), "Material 2/3", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                    case "MATERIALSALDO":
                        getSinc(MaterialSaldo.class, Constants.DTO.listAtualizacaoServidor.get(i), "Material 3/3", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                    case "PVCADPARCEIRO":
                        getSinc(Parceiro.class, Constants.DTO.listAtualizacaoServidor.get(i), "Parceiro 1/2", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                    case "PARCEIROVENCIMENTO":
                        getSinc(ParceiroVencimento.class, Constants.DTO.listAtualizacaoServidor.get(i), "Parceiro 2/2", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                    case "PVCADFORMAPAGAMENTO":
                        getSinc(FormaPagamento.class, Constants.DTO.listAtualizacaoServidor.get(i), "Forma Pagamento", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                    case "PVTABELAPRECOITEM":
                        getSinc(TabelaPrecoItem.class, Constants.DTO.listAtualizacaoServidor.get(i), "Tabela Preço", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                    case "PVCADGRUPO":
                        getSinc(Categoria.class, Constants.DTO.listAtualizacaoServidor.get(i), "Categoria", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                    case "PVMATERIALGRUPO":
                        getSinc(CategoriaMaterial.class, Constants.DTO.listAtualizacaoServidor.get(i), "Categoria Material", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                    case "METAFUNCIONARIO":
                        getSinc(MetaFuncionario.class, Constants.DTO.listAtualizacaoServidor.get(i), "Tabela Meta", codigoconfiguracao, dataAtualizacao, tipoSincronia);
                        somaTabelaSincronia();
                        break;
                }
            } else {
                somaTabelaSincronia();
                VerificaSincronia(Constants.SINCRONIA.TABELA_ATUAL);
            }
        } else {
            if (Constants.SINCRONIA.CARREGA_TABELAS) {
                getDados();
                Constants.SINCRONIA.CARREGA_TABELAS = false;
            }

            ((MainActivity) getActivity()).onSetItemMenu();
        }
    }

    private <T> void getSinc(Class<T> type, Atualizacao atualizacao, String tabela, String codigoConfiguracao, Date dataAtualizacao, Enums.TIPO_SINCRONIA tipoSincronia) {
        RequestSincroniaTask<T> requestSincroniaTask = new RequestSincroniaTask<>(this, type, tipoSincronia, atualizacao, tabela, codigoConfiguracao, dataAtualizacao);
        requestSincroniaTask.execute();
        Constants.SINCRONIA.CARREGA_TABELAS = true;
    }

    private void somaTabelaSincronia() {
        Constants.SINCRONIA.TABELA_ATUAL = Constants.SINCRONIA.TABELA_ATUAL + 1;
    }

    public <T> int onSetDados(List<T> listDados, Class<T> type, Atualizacao atualizacao, String mensagem, Enums.TIPO_SINCRONIA tipoSincronia) {
        ResponseSincroniaTask<T> dadosTask = new ResponseSincroniaTask(this, listDados, type, atualizacao, mensagem, tipoSincronia);
        dadosTask.execute();
        return 0;
    }

    public void getDados() {
        DadosTabelasTask dadosTabelasTask = new DadosTabelasTask(this);
        dadosTabelasTask.execute();
    }

    private void enviarPedido() {
        PedidoTask pedidoTask = new PedidoTask(this);
        pedidoTask.execute();
    }

    private void checaMovimento(Integer id) {
        List<MovimentoItem> listMovimentoItem = MovimentoItemDAO.getInstance(getActivity()).findByMovimentoId(id);
        List<MovimentoParcela> lisMovimentoParcela = MovimentoParcelaDAO.getInstance(getActivity()).findByMovimentoId(id);

        if ((listMovimentoItem.size() > 0) && (lisMovimentoParcela.size() > 0)) {
            Constants.PEDIDO.listPedidos.add(id);
        } else {
            Toast.makeText(getActivity(), "Não é possível sincronizar um pedido não finalizado!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checaMovimentoPendente(Integer id) {
        boolean value = true;

        Movimento mov = listMovimento.get(id);

        if (mov.sincronizado.equals("P")) {
            value = false;
        }

        return value;
    }

    public void verificaPedido(Integer pedidoAtual) {
        if (pedidoAtual <= Constants.PEDIDO.listPedidos.size()) {
            Constants.PEDIDO.movimento = MovimentoDAO.getInstance(getActivity()).findById(Constants.PEDIDO.listPedidos.get(pedidoAtual - 1));
            Constants.PEDIDO.movimentoItems = MovimentoItemDAO.getInstance(getActivity()).findByMovimentoId(Constants.PEDIDO.movimento.id);
            Constants.PEDIDO.movimentoParcelas = MovimentoParcelaDAO.getInstance(getActivity()).findByMovimentoId(Constants.PEDIDO.movimento.id);

            enviarPedido();

            Constants.PEDIDO.PEDIDOATUAL = pedidoAtual + 1;
        } else {
            Constants.PEDIDO.PEDIDOATUAL = 0;
            Constants.PEDIDO.listPedidos = null;
            Constants.PEDIDO.listPedidos = new ArrayList<>();
        }
    }

    public void atualizaDataSincronia(Atualizacao atualizacao, Enums.TIPO_SINCRONIA tipoSincronia) {
        Atualizacao att = AtualizacaoDAO.getInstance(getActivity()).findByNomeTabela(atualizacao.nometabela);

        if (att != null) {
            switch (tipoSincronia) {
                case TOTAL:
                    if (atualizacao.nometabela.equals("METAFUNCIONARIO") || atualizacao.nometabela.equals("MATERIALSALDO") || atualizacao.nometabela.equals("PARCEIROVENCIMENTO")) {
                        att.datasinctotal = new Date();
                        att.dataultimasincronia = new Date();
                    } else {
                        att.datasinctotal = atualizacao.datasinctotal;
                    }
                    break;
                case PARCIAL:
                    att.datasincparcial = atualizacao.datasincparcial;
                    break;
                case MARCADOS:
                    att.datasincmarcado = atualizacao.datasincmarcado;
                    break;
            }
        } else {
            if (atualizacao.nometabela.equals("METAFUNCIONARIO") || atualizacao.nometabela.equals("MATERIALSALDO") || atualizacao.nometabela.equals("PARCEIROVENCIMENTO")) {
                atualizacao.datasinctotal = new Date();
                atualizacao.dataultimasincronia = new Date();
            }

        }
        att = atualizacao;

        AtualizacaoDAO.getInstance(getActivity()).createOrUpdate(att);
    }
}
