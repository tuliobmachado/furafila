package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import br.com.informsistemas.furafila.models.pojo.MaterialEstado;

public class MaterialEstadoDAO extends BaseDAO<MaterialEstado> {

    private static MaterialEstadoDAO dao;

    public static MaterialEstadoDAO getInstance(Context context){
        if (dao == null){
            dao = new MaterialEstadoDAO(context);
        }

        return dao;
    }

    private MaterialEstadoDAO(Context context){
        super();
        super.ctx = context;
    }

    public MaterialEstado getTributacoes(String estado, String codigomaterial){
        MaterialEstado item = null;

        try{
            QueryBuilder<MaterialEstado, Object> queryBuilder = getHelper().getDAO(MaterialEstado.class).queryBuilder();
            queryBuilder.where().eq("estado", estado).and().eq("codigomaterial", codigomaterial);

            item = queryBuilder.queryForFirst();
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        return item;
    }
}
