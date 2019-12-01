package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import br.com.informsistemas.furafila.models.pojo.ProdutoImpressao;

public class ProdutoImpressaoDAO extends BaseDAO<ProdutoImpressao> {

    private static ProdutoImpressaoDAO dao;

    public static ProdutoImpressaoDAO getInstance(Context context){
        if (dao == null){
            dao = new ProdutoImpressaoDAO(context);
        }

        return dao;
    }

    private ProdutoImpressaoDAO(Context context){
        super();
        super.ctx = context;
    }

}
