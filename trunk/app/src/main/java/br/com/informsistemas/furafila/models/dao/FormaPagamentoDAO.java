package br.com.informsistemas.furafila.models.dao;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.FormaPagamento;

public class FormaPagamentoDAO extends BaseDAO<FormaPagamento> {

    private static FormaPagamentoDAO dao;

    public static FormaPagamentoDAO getInstance(Context context){
        if (dao == null){
            dao = new FormaPagamentoDAO(context);
        }

        return dao;
    }

    private FormaPagamentoDAO(Context context){
        super();
        super.ctx = context;
    }

    public List<FormaPagamento> pesquisaLista(String s){
        List<FormaPagamento> pagamentoList = new ArrayList<>();

        for (FormaPagamento p : Constants.DTO.listPesquisaPagamento){
            if (p.descricao.toUpperCase().contains(s.toUpperCase())){
                pagamentoList.add(p);
            }
        }

        return pagamentoList;
    }

    public List<FormaPagamento> findByFormasPermitidas(String codigoformapagamento) {
        List<FormaPagamento> items = null;
        codigoformapagamento = codigoformapagamento.replace("'", "");
        String[] codigosFormaPagamento = codigoformapagamento.split(",");
        try{
            QueryBuilder<FormaPagamento, Object> pagamentoQB = getHelper().getDAO(FormaPagamento.class).queryBuilder();
            pagamentoQB.where().in("codigoforma", codigosFormaPagamento);

            String str = pagamentoQB.prepareStatementString();
            Log.i("FormaPagamentoDAO", str);
            items = pagamentoQB.query();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return items;
    }
}
