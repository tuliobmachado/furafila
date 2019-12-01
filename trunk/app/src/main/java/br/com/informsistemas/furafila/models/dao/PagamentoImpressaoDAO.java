package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import br.com.informsistemas.furafila.models.pojo.PagamentoImpressao;

public class PagamentoImpressaoDAO extends BaseDAO<PagamentoImpressao> {

    private static PagamentoImpressaoDAO dao;

    public static PagamentoImpressaoDAO getInstance(Context context){
        if (dao == null){
            dao = new PagamentoImpressaoDAO(context);
        }

        return dao;
    }

    private PagamentoImpressaoDAO(Context context){
        super();
        super.ctx = context;
    }
}
