package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.Parceiro;

public class ParceiroDAO extends BaseDAO<Parceiro> {

    private static ParceiroDAO dao;

    public static ParceiroDAO getInstance(Context context){
        if (dao == null){
            dao = new ParceiroDAO(context);
        }

        return dao;
    }

    private ParceiroDAO(Context context){
        super();
        super.ctx = context;
    }

    public String retornaParceiros(){
        String parceiros = "";
        List<Parceiro> parceiroList = new ArrayList<>();

        try {
            parceiroList = getHelper().getDAO(Parceiro.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < parceiroList.size(); i++) {
            if (parceiros.equals("")){
                parceiros = parceiroList.get(i).codigoparceiro;
            }else{
                parceiros = parceiros+";"+parceiroList.get(i).codigoparceiro;
            }
        }

        return parceiros;
    }

    public List<Parceiro> pesquisaLista(String s){
        List<Parceiro> parceiroList = new ArrayList<>();

        for (Parceiro p : Constants.DTO.listPesquisaParceiro){
            if (p.codigoparceiro.toUpperCase().contains(s.toUpperCase()) || p.descricao.toUpperCase().contains(s.toUpperCase()) || p.nomefantasia.toUpperCase().contains(s.toUpperCase()) || p.cpfcgc.toUpperCase().contains(s.toUpperCase())){
                parceiroList.add(p);
            }
        }

        return parceiroList;
    }

    public List<Parceiro> getListParceiro() {
        return Constants.DTO.listPesquisaParceiro;
    }

    public void getEstados(){
        List<Parceiro> parceiroList = null;

        try {
            parceiroList = getHelper().getDAO(Parceiro.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < parceiroList.size(); i++) {
            if (!Constants.SINCRONIA.listEstados.contains(parceiroList.get(i).estado)) {
                Constants.SINCRONIA.listEstados.add(parceiroList.get(i).estado);
            }
        }
    }

    public void getTabelaPreco(){
        List<Parceiro> parceiroList = null;

        try {
            parceiroList = getHelper().getDAO(Parceiro.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < parceiroList.size(); i++) {
            if (!Constants.SINCRONIA.listTabelaPreco.contains(parceiroList.get(i).codigotabelapreco)) {
                Constants.SINCRONIA.listTabelaPreco.add(parceiroList.get(i).codigotabelapreco);
            }
        }
    }
}
