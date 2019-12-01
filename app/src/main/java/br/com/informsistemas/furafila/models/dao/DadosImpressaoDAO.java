package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import br.com.informsistemas.furafila.models.pojo.DadosImpressao;

public class DadosImpressaoDAO extends BaseDAO<DadosImpressao> {

    private static DadosImpressaoDAO dao;

    public static DadosImpressaoDAO getInstance(Context context){
        if (dao == null){
            dao = new DadosImpressaoDAO(context);
        }

        return dao;
    }

    private DadosImpressaoDAO(Context context){
        super();
        super.ctx = context;
    }
}
