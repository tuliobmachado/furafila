package br.com.informsistemas.furafila.dao;

import android.content.Context;

import br.com.informsistemas.furafila.model.ConfiguracaoSitef;
import br.com.informsistemas.furafila.models.dao.BaseDAO;

public class ConfiguracaoSitefDAO extends BaseDAO<ConfiguracaoSitef> {

    private static ConfiguracaoSitefDAO dao;

    public static ConfiguracaoSitefDAO getInstance(Context context){
        if (dao == null){
            dao = new ConfiguracaoSitefDAO(context);
        }

        return dao;
    }

    private ConfiguracaoSitefDAO(Context context){
        super();
        super.ctx = context;
    }
}
