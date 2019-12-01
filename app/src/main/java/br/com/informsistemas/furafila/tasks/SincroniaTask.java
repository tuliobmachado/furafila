package br.com.informsistemas.furafila.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.MainActivity;
import br.com.informsistemas.furafila.controller.fragments.MovimentoFragment;
import br.com.informsistemas.furafila.controller.rest.Request.RequestSincAtualizacao;
import br.com.informsistemas.furafila.controller.rest.RestManager;
import br.com.informsistemas.furafila.models.callback.SincroniaService;
import br.com.informsistemas.furafila.models.dao.AtualizacaoDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Atualizacao;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.models.utils.DialogClass;
import retrofit2.Call;
import retrofit2.Response;

public class SincroniaTask extends AsyncTask<String, Void, List<Atualizacao>> {

    private Fragment fragment;
    private ProgressDialog dialog;
    private Boolean erro;
    private Boolean sincroniaSolicitada;

    public SincroniaTask(Fragment f, Boolean sincroniaSolicitada) {
        this.fragment = f;
        this.erro = false;
        this.sincroniaSolicitada = sincroniaSolicitada;

        dialog = new ProgressDialog(f.getActivity(), R.style.DialogDefault);
        dialog.setCancelable(false);

        Constants.SINCRONIA.listEstados = new ArrayList<>();
        Constants.SINCRONIA.listTabelaPreco = new ArrayList<>();
        Constants.SINCRONIA.listTabelaPreco.add(Constants.DTO.registro.codigotabelapreco);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Verificando sincronia...");
        dialog.show();
    }

    @Override
    protected List<Atualizacao> doInBackground(String... strings) {
        RequestSincAtualizacao requestSincAtualizacao = new RequestSincAtualizacao(Constants.DTO.registro.cnpj, Constants.DTO.registro.codigoconfiguracao);

        SincroniaService sincroniaService = new RestManager().getSincroniaService();
        Call<RestResponse<Atualizacao>> requestRegistro = sincroniaService.postAtualizacao(requestSincAtualizacao);

        try {
            RestResponse<Atualizacao> restResponse = null;
            Response<RestResponse<Atualizacao>> response = requestRegistro.execute();

            if (response.errorBody() != null) {
                restResponse = new Gson().fromJson(response.errorBody().charStream(), RestResponse.class);
            } else {
                restResponse = response.body();
            }

            if (restResponse.meta.status.equals("OK")) {
                return restResponse.data;
            } else {
                showToast(restResponse.meta.message);
            }
        } catch (IOException ex) {
            showToast(ex.getMessage());
            Log.i("Sincronia", ex.getMessage());
            erro = true;
        }

        return null;
    }

    @Override
    protected void onPostExecute(final List<Atualizacao> listAtualizacao) {
        if (listAtualizacao != null) {
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Constants.DTO.listAtualizacaoServidor = listAtualizacao;
                    onSetTabelasFixas();
                    DialogClass.dialogDismiss(dialog);
                    ((MovimentoFragment) fragment).VerificaSincronia(0);
                }
            });
        } else {
            DialogClass.dialogDismiss(dialog);
            if (erro) {
                ((MovimentoFragment) fragment).getSincronia(false);
            } else {
                ((MovimentoFragment) fragment).getDados();
                ((MainActivity) fragment.getActivity()).onSetItemMenu();
            }
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

    private void onSetTabelasFixas() {
        Atualizacao atualizacao = null;

        atualizacao = getTableAtualizacao("PARCEIROVENCIMENTO");
        saveAtualizacao(atualizacao);

        atualizacao = getTableAtualizacao("MATERIALSALDO");
        saveAtualizacao(atualizacao);

        atualizacao = getTableAtualizacao("METAFUNCIONARIO");
        saveAtualizacao(atualizacao);
    }

    private Atualizacao getTableAtualizacao(String nometabela) {

        Atualizacao item = AtualizacaoDAO.getInstance(fragment.getActivity()).findByNomeTabela(nometabela);

        if (item == null) {
            item = new Atualizacao(Constants.DTO.registro.codigoconfiguracao, nometabela, Misc.getDataPadrao(), Misc.getDataPadrao(), "DISPONIVEL", Misc.getDataPadrao());
        } else {
            if (nometabela.equals("PARCEIROVENCIMENTO") || nometabela.equals("METAFUNCIONARIO") || nometabela.equals("MATERIALSALDO")) {

                if (nometabela.equals("PARCEIROVENCIMENTO")) {
                    if (sincroniaSolicitada) {
                        item.datasinctotal = new Date();
                    }
                } else if (nometabela.equals("MATERIALSALDO")) {
                    if (sincroniaSolicitada) {
                        item.datasinctotal = new Date();
                    }
                } else if (nometabela.equals("METAFUNCIONARIO")) {
                    if (sincroniaSolicitada) {
                        item.datasinctotal = new Date();
                    }
                }
            }
        }

        return item;
    }

    private void saveAtualizacao(Atualizacao item) {
        if (item == null) {
            AtualizacaoDAO.getInstance(fragment.getActivity()).createOrUpdate(item);
        }

        Constants.DTO.listAtualizacaoServidor.add(item);
    }
}
