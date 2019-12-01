package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.models.pojo.ParceiroVencimento;

public class ParceiroVencimentoDAO extends BaseDAO<ParceiroVencimento> {

    private static ParceiroVencimentoDAO dao;

    public static ParceiroVencimentoDAO getInstance(Context context){
        if (dao == null){
            dao = new ParceiroVencimentoDAO(context);
        }

        return dao;
    }

    private ParceiroVencimentoDAO(Context context){
        super();
        super.ctx = context;
    }

    public List<ParceiroVencimento> findAllVencimentoByCodigoParceiro(String codigoparceiro){
        List<ParceiroVencimento> items = new ArrayList<>();

        QueryBuilder<ParceiroVencimento, Object> queryBuilder = getHelper().getDAO(ParceiroVencimento.class).queryBuilder();
        try {
            queryBuilder.where().eq("codigoparceiro", codigoparceiro);
            queryBuilder.orderBy("status", false);

            items = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }
}
