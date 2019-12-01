package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import br.com.informsistemas.furafila.models.pojo.Registro;

public class RegistroDAO extends BaseDAO<Registro> {

    private static RegistroDAO dao;

    public static RegistroDAO getInstance(Context context){
        if (dao == null){
            dao = new RegistroDAO(context);
        }

        return dao;
    }

    private RegistroDAO(Context context){
        super();
        super.ctx = context;
    }

}
