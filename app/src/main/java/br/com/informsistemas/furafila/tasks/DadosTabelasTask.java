package br.com.informsistemas.furafila.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.models.dao.CategoriaDAO;
import br.com.informsistemas.furafila.models.dao.FormaPagamentoDAO;
import br.com.informsistemas.furafila.models.dao.MaterialDAO;
import br.com.informsistemas.furafila.models.dao.MaterialSaldoDAO;
import br.com.informsistemas.furafila.models.dao.MetaFuncionarioDAO;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.MaterialSaldo;
import br.com.informsistemas.furafila.models.pojo.MetaFuncionario;
import br.com.informsistemas.furafila.models.utils.DialogClass;

public class DadosTabelasTask extends AsyncTask<String, Void, String> {

    private Fragment fragment;
    private ProgressDialog dialog;

    public DadosTabelasTask(Fragment f){
        this.fragment = f;

        dialog = new ProgressDialog(f.getActivity(), R.style.DialogDefault);
        dialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Carregando tabelas...");
        dialog.setMax(6);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setProgress(0);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        //Resgata as listas para serem utilizadas nas pesquisas
        resgataParceiros();
        dialog.setProgress(1);
        resgataMaterial();
        resgataMaterialSaldo();
        dialog.setProgress(2);
        resgataPesquisaPagamento();
        dialog.setProgress(3);
        resgataPesquisaCategoria();
        dialog.setProgress(4);
        resgataListaMaterial();
        dialog.setProgress(5);
        resgataMetaFuncionario();
        dialog.setProgress(6);

        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        DialogClass.dialogDismiss(dialog);
    }

    private List<MaterialSaldo> getListPesquisa(){
        List<MaterialSaldo>  materialSaldoList = MaterialSaldoDAO.getInstance(fragment.getActivity()).findAll();

        for (int i = 0; i < materialSaldoList.size(); i++) {
            Material m = MaterialDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("codigomaterial", materialSaldoList.get(i).codigomaterial);

            materialSaldoList.get(i).descricao = m.descricao;
            materialSaldoList.get(i).unidade = m.unidadesaida;
            materialSaldoList.get(i).precovenda1 = m.precovenda1;

        }

        return materialSaldoList;
    }

    private void resgataParceiros(){
        try{
            Constants.DTO.listPesquisaParceiro = ParceiroDAO.getInstance(fragment.getActivity()).findAll();
        }catch (Exception e){
            DialogClass.showToastFragment(fragment, e.getMessage());
        }
    }

    private void resgataMaterial(){
        try{
            Constants.DTO.listPesquisaMaterial = MaterialDAO.getInstance(fragment.getActivity()).findAll();
        }catch (Exception e){
            DialogClass.showToastFragment(fragment, e.getMessage());
        }
    }

    private void resgataMaterialSaldo(){
        try{
            checarMaterialSaldo(Constants.DTO.listPesquisaMaterial);
        }catch (Exception e){
            DialogClass.showToastFragment(fragment, e.getMessage());
        }
    }

    private void resgataPesquisaPagamento(){
        try{
            Constants.DTO.listPesquisaPagamento = FormaPagamentoDAO.getInstance(fragment.getActivity()).findAll();
        }catch (Exception e){
            DialogClass.showToastFragment(fragment, e.getMessage());
        }
    }

    private void resgataPesquisaCategoria(){
        try{
            Constants.DTO.listPesquisaCategoria = CategoriaDAO.getInstance(fragment.getActivity()).findAll();
        }catch (Exception e){
            DialogClass.showToastFragment(fragment, e.getMessage());
        }
    }

    private void resgataListaMaterial(){
        try{
            Constants.DTO.listMaterialSaldo = getListPesquisa();
        }catch (Exception e){
            DialogClass.showToastFragment(fragment, e.getMessage());
        }
    }

    private void resgataMetaFuncionario(){
        try{
            Constants.DTO.metaFuncionario = MetaFuncionarioDAO.getInstance(fragment.getActivity()).findByIdAuxiliar("dia", Misc.GetDateAtual());

            if (Constants.DTO.metaFuncionario == null){
                Constants.DTO.metaFuncionario = CriarMetaPadrao(Misc.GetDateAtual());
            }

            Constants.DTO.metaFuncionario.metaarealizar = MetaFuncionarioDAO.getInstance(fragment.getActivity()).GetMetaRealizada(Misc.GetDateAtual());
        }catch (Exception e){
            DialogClass.showToastFragment(fragment, e.getMessage());
        }
    }

    private void checarMaterialSaldo(List<Material> listMaterial){
        List<MaterialSaldo> listMaterialSaldo = MaterialSaldoDAO.getInstance(fragment.getActivity()).findAll();
        String codigoMaterialSaldo = "";

        if (listMaterialSaldo.size() != listMaterial.size()) {
            for (int i = 0; i < listMaterialSaldo.size(); i++) {
                if (codigoMaterialSaldo.equals("")){
                    codigoMaterialSaldo = listMaterialSaldo.get(i).codigomaterial;
                }else{
                    codigoMaterialSaldo = codigoMaterialSaldo + "," + listMaterialSaldo.get(i).codigomaterial;
                }
            }

            List<Material> listMaterialAuxiliar = MaterialDAO.getInstance(fragment.getActivity()).findByNotIn("codigomaterial", codigoMaterialSaldo);

            for (int i = 0; i < listMaterialAuxiliar.size(); i++) {
                MaterialSaldo materialSaldo = new MaterialSaldo(listMaterialAuxiliar.get(i).codigomaterial, null, null, 0, 0);

                MaterialSaldoDAO.getInstance(fragment.getActivity()).createOrUpdate(materialSaldo);
            }
        }

    }

    public MetaFuncionario CriarMetaPadrao(Date data){
        MetaFuncionario metaFuncionario = MetaFuncionarioDAO.getInstance(fragment.getActivity()).findFirst();
        MetaFuncionario objMeta = null;

        if (metaFuncionario == null){
            objMeta = new MetaFuncionario(Constants.DTO.registro.codigofuncionario, Constants.DTO.registro.nome, data, 30, 0, 0, 0);
        }else{
            objMeta = new MetaFuncionario(metaFuncionario.codigofuncionario, metaFuncionario.descricao, data, metaFuncionario.totaldiames,
                    metaFuncionario.metamensal, 0, metaFuncionario.metadiaria);
        }

        MetaFuncionarioDAO.getInstance(fragment.getActivity()).createOrUpdate(objMeta);

        return objMeta;
    }
}
