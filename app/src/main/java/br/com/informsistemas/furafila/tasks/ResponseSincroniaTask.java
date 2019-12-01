package br.com.informsistemas.furafila.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.fragments.MovimentoFragment;
import br.com.informsistemas.furafila.models.dao.CategoriaDAO;
import br.com.informsistemas.furafila.models.dao.CategoriaMaterialDAO;
import br.com.informsistemas.furafila.models.dao.FormaPagamentoDAO;
import br.com.informsistemas.furafila.models.dao.MaterialDAO;
import br.com.informsistemas.furafila.models.dao.MaterialEstadoDAO;
import br.com.informsistemas.furafila.models.dao.MaterialSaldoDAO;
import br.com.informsistemas.furafila.models.dao.MetaFuncionarioDAO;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.dao.ParceiroVencimentoDAO;
import br.com.informsistemas.furafila.models.dao.TabelaPrecoItemDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Enums;
import br.com.informsistemas.furafila.models.pojo.Atualizacao;
import br.com.informsistemas.furafila.models.pojo.Categoria;
import br.com.informsistemas.furafila.models.pojo.CategoriaMaterial;
import br.com.informsistemas.furafila.models.pojo.FormaPagamento;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.MaterialEstado;
import br.com.informsistemas.furafila.models.pojo.MaterialSaldo;
import br.com.informsistemas.furafila.models.pojo.MetaFuncionario;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.pojo.ParceiroVencimento;
import br.com.informsistemas.furafila.models.pojo.TabelaPrecoItem;
import br.com.informsistemas.furafila.models.utils.DialogClass;

public class ResponseSincroniaTask<T> extends AsyncTask<String, Void, String> {

    private Fragment fragment;
    private List<T> listDados;
    private String mensagem;
    private int totalRegistros;
    private ProgressDialog dialog;
    private Class<T> type;
    private Enums.TIPO_SINCRONIA tipoSincronia;
    private Atualizacao atualizacao;

    public ResponseSincroniaTask(Fragment f, List<T> list, Class<T> tp, Atualizacao atualizacao, String msg, Enums.TIPO_SINCRONIA tipoSincronia){
        this.fragment = f;
        this.listDados = list;
        this.mensagem = msg;
        this.type = tp;
        this.tipoSincronia = tipoSincronia;
        this.atualizacao = atualizacao;

        dialog = new ProgressDialog(f.getActivity(), R.style.DialogDefault);
        dialog.setCancelable(false);

        totalRegistros = listDados.size();
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage(mensagem);
        dialog.setMax(totalRegistros);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setProgress(0);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        if (tipoSincronia == Enums.TIPO_SINCRONIA.TOTAL){
            deletarBanco();
        }
        for (int i = 0; i < totalRegistros; i++){

            switch (type.getSimpleName()){
                case "Parceiro":
                    saveParceiro((Parceiro) listDados.get(i));
                    break;
                case "ParceiroVencimento":
                    saveParceiroVencimento((ParceiroVencimento) listDados.get(i));
                    break;
                case "Material":
                    saveMaterial((Material) listDados.get(i));
                    break;
                case "MaterialEstado":
                    saveMaterialEstado((MaterialEstado) listDados.get(i));
                    break;
                case "MaterialSaldo":
                    saveMaterialSaldo((MaterialSaldo) listDados.get(i));
                    break;
                case "FormaPagamento":
                    saveFormaPagamento((FormaPagamento) listDados.get(i));
                    break;
                case "TabelaPrecoItem":
                    saveTabelaPrecoItem((TabelaPrecoItem) listDados.get(i));
                    break;
                case "Categoria":
                    if (tipoSincronia == Enums.TIPO_SINCRONIA.TOTAL) {
                        criaCategoriaInicial(i, (Categoria) listDados.get(i));
                    }
                    saveCategoria((Categoria) listDados.get(i));
                    break;
                case "CategoriaMaterial":
                    saveCategoriaMaterial((CategoriaMaterial) listDados.get(i));
                    break;
                case "MetaFuncionario":
                    saveMetaFuncionario((MetaFuncionario) listDados.get(i));
                    break;
            }

            dialog.setProgress(i+1);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        DialogClass.dialogDismiss(dialog);
        ((MovimentoFragment) fragment).atualizaDataSincronia(atualizacao, tipoSincronia);
        ((MovimentoFragment) fragment).VerificaSincronia(Constants.SINCRONIA.TABELA_ATUAL);
    }

    private void saveMaterial(Material item){
        Material m = null;

        if  (!isSincroniaCompleta()) {
            m = MaterialDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("codigomaterial", item.codigomaterial);

            if (m != null) {
                MaterialDAO.getInstance(fragment.getActivity()).delete(m);
            }
        }

        m = item;

        MaterialDAO.getInstance(fragment.getActivity()).createOrUpdate(m);
    }

    private void saveMaterialEstado(MaterialEstado item){
        MaterialEstado m = null;

        if  (!isSincroniaCompleta()) {
            m = MaterialEstadoDAO.getInstance(fragment.getActivity()).getTributacoes(item.estado, item.codigomaterial);

            if (m != null) {
                MaterialEstadoDAO.getInstance(fragment.getActivity()).delete(m);
            }
        }

        m = item;

        MaterialEstadoDAO.getInstance(fragment.getActivity()).createOrUpdate(m);
    }

    private void saveTabelaPrecoItem(TabelaPrecoItem item){
        TabelaPrecoItem t = null;

        if  (!isSincroniaCompleta()) {
            t = TabelaPrecoItemDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("codigotabelaprecoitem", item.codigotabelaprecoitem);

            if (t != null) {
                TabelaPrecoItemDAO.getInstance(fragment.getActivity()).delete(t);
            }
        }

        t = item;

        TabelaPrecoItemDAO.getInstance(fragment.getActivity()).createOrUpdate(t);
    }

    private void saveFormaPagamento(FormaPagamento item){
        FormaPagamento f = null;

        if  (!isSincroniaCompleta()) {
            f = FormaPagamentoDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("codigoforma", item.codigoforma);

            if (f != null) {
                FormaPagamentoDAO.getInstance(fragment.getActivity()).delete(f);
            }
        }

        f = item;

        FormaPagamentoDAO.getInstance(fragment.getActivity()).createOrUpdate(f);
    }

    private void saveParceiro(Parceiro item){
        Parceiro p = null;

        if (!item.codigotabelapreco.equals("")){
            if (!Constants.SINCRONIA.listTabelaPreco.contains(item.codigotabelapreco)) {
                Constants.SINCRONIA.listTabelaPreco.add(item.codigotabelapreco);
            }
        }

        if (!item.estado.equals("")){
            if (!Constants.SINCRONIA.listEstados.contains(item.estado)){
                Constants.SINCRONIA.listEstados.add(item.estado);
            }
        }

        if  (!isSincroniaCompleta()) {
            p = ParceiroDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("codigoparceiro", item.codigoparceiro);

            if (p != null) {
                ParceiroDAO.getInstance(fragment.getActivity()).delete(p);
            }
        }

        p = item;

        ParceiroDAO.getInstance(fragment.getActivity()).createOrUpdate(p);
    }

    private void saveParceiroVencimento(ParceiroVencimento item){
        ParceiroVencimento p = null;

        if  (!isSincroniaCompleta()) {
            p = ParceiroVencimentoDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("codigoparceiro", item.codigoparceiro);

            if (p != null) {
                ParceiroVencimentoDAO.getInstance(fragment.getActivity()).delete(p);
            }
        }

        p = item;

        ParceiroVencimentoDAO.getInstance(fragment.getActivity()).createOrUpdate(p);
    }

    private void saveMaterialSaldo(MaterialSaldo item){
        MaterialSaldo m = null;

        if  (!isSincroniaCompleta()) {
            m = MaterialSaldoDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("codigomaterial", item.codigomaterial);

            if (m != null) {
                MaterialSaldoDAO.getInstance(fragment.getActivity()).delete(m);
            }
        }

        m = item;

        MaterialSaldoDAO.getInstance(fragment.getActivity()).createOrUpdate(m);
    }

    private void criaCategoriaInicial(int position, Categoria categoria){
        if (position == 0){
            categoria = CategoriaDAO.getInstance(fragment.getActivity()).findFirst();

            if (categoria == null){
                CategoriaDAO.getInstance(fragment.getActivity()).createOrUpdate(new Categoria("000", "TODAS", null));
            }
        }
    }

    private void saveCategoria(Categoria item){
        Categoria c = null;

        if  (!isSincroniaCompleta()) {
            c = CategoriaDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("codigogrupo", item.codigogrupo);

            if (c != null) {
                CategoriaDAO.getInstance(fragment.getActivity()).delete(c);
            }
        }

        c = item;

        CategoriaDAO.getInstance(fragment.getActivity()).createOrUpdate(c);
    }

    private void saveCategoriaMaterial(CategoriaMaterial item){
        CategoriaMaterial c = null;

        if  (!isSincroniaCompleta()) {
            c = CategoriaMaterialDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("codigomaterial", item.codigomaterial);

            if (c != null) {
                CategoriaMaterialDAO.getInstance(fragment.getActivity()).delete(c);
            }
        }

        c = item;

        CategoriaMaterialDAO.getInstance(fragment.getActivity()).createOrUpdate(c);
    }

    private void saveMetaFuncionario(MetaFuncionario item){
        MetaFuncionario m = null;

        if  (!isSincroniaCompleta()) {
            m = MetaFuncionarioDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("dia", item.dia);

            if (m != null) {
                MetaFuncionarioDAO.getInstance(fragment.getActivity()).delete(m);
            }
        }

        m = item;

        MetaFuncionarioDAO.getInstance(fragment.getActivity()).createOrUpdate(m);
    }

    private void deletarBanco(){
        switch (type.getSimpleName()){
            case "Parceiro":
                ParceiroDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
            case "ParceiroVencimento":
                ParceiroVencimentoDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
            case "Material":
                MaterialDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
            case "MaterialEstado":
                MaterialEstadoDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
            case "MaterialSaldo":
                MaterialSaldoDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
            case "FormaPagamento":
                FormaPagamentoDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
            case "TabelaPrecoItem":
                TabelaPrecoItemDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
            case "Categoria":
                CategoriaDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
            case "CategoriaMaterial":
                CategoriaMaterialDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
            case "MetaFuncionario":
                MetaFuncionarioDAO.getInstance(fragment.getActivity()).deleteAll();
                break;
        }
    }

    private boolean isSincroniaCompleta(){
        boolean value = true;

        if ((tipoSincronia == Enums.TIPO_SINCRONIA.MARCADOS) || (tipoSincronia == Enums.TIPO_SINCRONIA.PARCIAL)){
            value = false;
        }
        return value;
    }
}
