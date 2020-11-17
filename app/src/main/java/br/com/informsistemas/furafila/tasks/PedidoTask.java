package br.com.informsistemas.furafila.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.fragments.MovimentoFragment;
import br.com.informsistemas.furafila.controller.rest.Request.RequestPedido;
import br.com.informsistemas.furafila.controller.rest.Response.ResponsePedido;
import br.com.informsistemas.furafila.controller.rest.RestManager;
import br.com.informsistemas.furafila.models.callback.PedidoService;
import br.com.informsistemas.furafila.models.dao.AtualizacaoDAO;
import br.com.informsistemas.furafila.models.dao.DadosImpressaoDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoDAO;
import br.com.informsistemas.furafila.models.dao.PagamentoImpressaoDAO;
import br.com.informsistemas.furafila.models.dao.ProdutoImpressaoDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Enums;
import br.com.informsistemas.furafila.models.helper.PrintNFCe;
import br.com.informsistemas.furafila.models.pojo.Atualizacao;
import br.com.informsistemas.furafila.models.pojo.DadosImpressao;
import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.PagamentoImpressao;
import br.com.informsistemas.furafila.models.pojo.ProdutoImpressao;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.models.utils.DialogClass;
import retrofit2.Call;
import retrofit2.Response;

public class PedidoTask extends AsyncTask<String, Void, List<ResponsePedido>> {

    private Fragment fragment;
    private ProgressDialog dialog;

    public PedidoTask(Fragment f) {
        this.fragment = f;

        dialog = new ProgressDialog(f.getActivity(), R.style.DialogDefault);
        dialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Enviando pedido...");
        dialog.show();
    }

    @Override
    protected List<ResponsePedido> doInBackground(String... strings) {
        for (int i = 0; i < Constants.PEDIDO.movimentoParcelas.size(); i++) {
            if (Constants.PEDIDO.movimentoParcelas.get(i).troco > 0) {
                Constants.PEDIDO.movimentoParcelas.get(i).valor = Constants.PEDIDO.movimentoParcelas.get(i).valor - Constants.PEDIDO.movimentoParcelas.get(i).troco;
            }
        }

        RequestPedido reqPedido = new RequestPedido(Constants.APP.VERSAO, Constants.DTO.registro.cnpj,
                Constants.DTO.registro.codigoconfiguracao, Constants.DTO.registro.codigofuncionario,
                Constants.DTO.registro.codigoalmoxarifado, new Date(), Constants.PEDIDO.movimento, Constants.PEDIDO.movimentoItems,
                Constants.PEDIDO.movimentoParcelas);

        for (int i = 0; i < reqPedido.movimentoparcela.size(); i++) {
            reqPedido.movimentoparcela.get(i).codigoforma = Constants.DTO.registro.codigoformapagamento;
        }

        if (Constants.APP.TIPO_APLICACAO == Enums.TIPO_APLICACAO.NFCE) {
            reqPedido.nfce = true;
        } else {
            reqPedido.nfce = false;
        }

        Constants.PEDIDO.movimento = null;
        Constants.PEDIDO.movimentoItems = null;
        Constants.PEDIDO.movimentoParcelas = null;

//        Log.i("JSON", Misc.getJsonString(reqPedido, true));

        PedidoService sincroniaService = new RestManager().getPedidoService();
        Call<RestResponse<ResponsePedido>> requestPedido = sincroniaService.postPedido(reqPedido);

        try {
            RestResponse<ResponsePedido> restResponse = null;
            Response<RestResponse<ResponsePedido>> response = requestPedido.execute();

            if (response.errorBody() != null) {
                restResponse = new Gson().fromJson(response.errorBody().charStream(), RestResponse.class);
            } else {
                String responseString = response.body().toString();
                restResponse = response.body();
            }

            if (restResponse.meta.status.equals("OK")) {
                return restResponse.data;
            } else {
                showToast(restResponse.meta.message);
            }
        } catch (IOException ex) {
            showToast(ex.getMessage());
            Constants.PEDIDO.PEDIDOATUAL = 0;
            Constants.PEDIDO.listPedidos = null;
            Constants.PEDIDO.listPedidos = new ArrayList<>();
            Constants.MOVIMENTO.enviarPedido = false;
            Movimento mov = getMovimentoAtual(reqPedido.movimento.id);
            atualizaStatusMovimento(mov, "P");
            dialog.dismiss();
        }

        return null;
    }

    @Override
    protected void onPostExecute(final List<ResponsePedido> requestPedido) {
        if (requestPedido != null) {
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Movimento mov = getMovimentoAtual(requestPedido.get(0).id);
                    String status = "";

                    if (requestPedido.get(0).status.equals("G")) {
                        status = "T";
                    } else {
                        status = "P";
                    }

                    atualizaStatusMovimento(mov, status);

                    DialogClass.dialogDismiss(dialog);

                    if (Constants.PEDIDO.PEDIDOATUAL > Constants.PEDIDO.listPedidos.size()) {

                        if (status.equals("T")) {
                            SalvarImpressao(requestPedido.get(0).dadosimpressao, mov.id);
                            PrintNFCe.execute(fragment, requestPedido.get(0).dadosimpressao);
                        }


                        ((MovimentoFragment) fragment).atualizaLista();

                    } else {
                        ((MovimentoFragment) fragment).verificaPedido(Constants.PEDIDO.PEDIDOATUAL);
                    }
                }
            });
        } else {
            DialogClass.dialogDismiss(dialog);
            ((MovimentoFragment) fragment).atualizaLista();
        }
    }

    private void showToast(final String msg) {
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(fragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataParcialAtualizacao(Date data) {
        Atualizacao atualizacao = AtualizacaoDAO.getInstance(fragment.getActivity()).findByNomeTabela("MATERIALSALDO");
        atualizacao.datasincparcial = data;

        AtualizacaoDAO.getInstance(fragment.getActivity()).createOrUpdate(atualizacao);
    }

    private Movimento getMovimentoAtual(Integer id) {
        return MovimentoDAO.getInstance(fragment.getActivity()).findById(id);
    }

    private void atualizaStatusMovimento(Movimento mov, String status) {
        mov.sincronizado = status;
        MovimentoDAO.getInstance(fragment.getActivity()).createOrUpdate(mov);
    }

    private void SalvarImpressao(DadosImpressao dadosImpressao, Integer movimento_id) {
        DadosImpressao dados = DadosImpressaoDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("movimento_id", movimento_id);
        List<PagamentoImpressao> listPagamentos = PagamentoImpressaoDAO.getInstance(fragment.getActivity()).findAllByIdAuxiliar("movimento_id", movimento_id);
        List<ProdutoImpressao> listProdutos = ProdutoImpressaoDAO.getInstance(fragment.getActivity()).findAllByIdAuxiliar("movimento_id", movimento_id);

        if (dados == null) {
            dadosImpressao.movimento_id = movimento_id;

            for (int i = 0; i < dadosImpressao.ListaProdutos.size(); i++) {
                dadosImpressao.ListaProdutos.get(i).movimento_id = movimento_id;
            }

            for (int i = 0; i < dadosImpressao.ListaFormasPagamento.size(); i++) {
                dadosImpressao.ListaFormasPagamento.get(i).movimento_id = movimento_id;
            }

            dados = dadosImpressao;
            listPagamentos = dadosImpressao.ListaFormasPagamento;
            listProdutos = dadosImpressao.ListaProdutos;
        } else {
            dados.Troco = dadosImpressao.Troco;
            dados.InformacoesComplementares = dadosImpressao.InformacoesComplementares;
            dados.QrCode = dadosImpressao.QrCode;
            dados.ChaveNota = dadosImpressao.ChaveNota;
            dados.ChaveUrl = dadosImpressao.ChaveUrl;
            dados.AmbienteProducao = dadosImpressao.AmbienteProducao;
            dados.Serie = dadosImpressao.Serie;
            dados.DataAutorizacao = dadosImpressao.DataAutorizacao;
            dados.InscricaoMunicipal = dadosImpressao.InscricaoMunicipal;
            dados.EnderecoEmpresa = dadosImpressao.EnderecoEmpresa;
            dados.DataEmissao = dadosImpressao.DataEmissao;
            dados.RazaoSocial = dadosImpressao.RazaoSocial;
            dados.CnpjEmpresa = dadosImpressao.CnpjEmpresa;
            dados.CPFCliente = dadosImpressao.CPFCliente;
            dados.NomeCliente = dadosImpressao.NomeCliente;
            dados.EnderecoCliente = dadosImpressao.EnderecoCliente;
            dados.ProtocoloAutorizacao = dadosImpressao.ProtocoloAutorizacao;
            dados.ValorAcrescimos = dadosImpressao.ValorAcrescimos;
            dados.ValorDescontos = dadosImpressao.ValorDescontos;
            dados.ValorProdutos = dadosImpressao.ValorProdutos;
            dados.ValorAPagar = dadosImpressao.ValorAPagar;
            dados.InscricaoEstadual = dadosImpressao.InscricaoEstadual;
            dados.NumeroNota = dadosImpressao.NumeroNota;

            for (int i = 0; i < listPagamentos.size(); i++) {
                listPagamentos.get(i).Descricao = dadosImpressao.ListaFormasPagamento.get(i).Descricao;
                listPagamentos.get(i).Tipo = dadosImpressao.ListaFormasPagamento.get(i).Tipo;
                listPagamentos.get(i).ValorDuplicata = dadosImpressao.ListaFormasPagamento.get(i).ValorDuplicata;
            }

            for (int i = 0; i < listProdutos.size(); i++) {
                listProdutos.get(i).CodigoMaterial = dadosImpressao.ListaProdutos.get(i).CodigoMaterial;
                listProdutos.get(i).Custo = dadosImpressao.ListaProdutos.get(i).Custo;
                listProdutos.get(i).Descricao = dadosImpressao.ListaProdutos.get(i).Descricao;
                listProdutos.get(i).Quantidade = dadosImpressao.ListaProdutos.get(i).Quantidade;
                listProdutos.get(i).TotalItem = dadosImpressao.ListaProdutos.get(i).TotalItem;
                listProdutos.get(i).Unidade = dadosImpressao.ListaProdutos.get(i).Unidade;
            }
        }

        DadosImpressaoDAO.getInstance(fragment.getActivity()).createOrUpdate(dados);

        for (int i = 0; i < listPagamentos.size(); i++) {
            PagamentoImpressaoDAO.getInstance(fragment.getActivity()).createOrUpdate(listPagamentos.get(i));
        }

        for (int i = 0; i < listProdutos.size(); i++) {
            ProdutoImpressaoDAO.getInstance(fragment.getActivity()).createOrUpdate(listProdutos.get(i));
        }

    }
}
