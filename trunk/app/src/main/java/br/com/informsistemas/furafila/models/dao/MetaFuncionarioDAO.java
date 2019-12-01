package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.MetaFuncionario;

public class MetaFuncionarioDAO extends BaseDAO<MetaFuncionario> {

    private static MetaFuncionarioDAO dao;

    public static MetaFuncionarioDAO getInstance(Context context){
        if (dao == null){
            dao = new MetaFuncionarioDAO(context);
        }

        return dao;
    }

    private MetaFuncionarioDAO(Context context){
        super();
        super.ctx = context;
    }

    public float GetMetaRealizada(Date data){
        List<MetaFuncionario> items = null;
        float value = 0;

        QueryBuilder<MetaFuncionario, Object> queryBuilder = getHelper().getDAO(MetaFuncionario.class).queryBuilder();
        try {
            queryBuilder.where().lt("dia", data);
            items = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < items.size(); i++) {
            value = value + items.get(i).metarealizada;
        }

        value = value + Constants.DTO.metaFuncionario.metarealizada;
        value = Constants.DTO.metaFuncionario.metamensal - value;

        if (value < 0) {
            value = Constants.DTO.metaFuncionario.metarealizada;
        }

        return value;
    }

}
