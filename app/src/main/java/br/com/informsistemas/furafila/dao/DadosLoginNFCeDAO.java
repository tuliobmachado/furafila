package br.com.informsistemas.furafila.dao;

import android.content.Context;

import br.com.informsistemas.furafila.model.DadosLoginNFCe;
import br.com.informsistemas.furafila.models.dao.BaseDAO;

public class DadosLoginNFCeDAO extends BaseDAO<DadosLoginNFCe> {

    private static DadosLoginNFCeDAO dao;

    public static DadosLoginNFCeDAO getInstance(Context context){
        if (dao == null){
            dao = new DadosLoginNFCeDAO(context);
        }

        return dao;
    }

    private DadosLoginNFCeDAO(Context context){
        super();
        super.ctx = context;
    }
}
