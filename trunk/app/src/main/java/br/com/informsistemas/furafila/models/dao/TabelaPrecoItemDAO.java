package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import br.com.informsistemas.furafila.models.pojo.TabelaPrecoItem;

public class TabelaPrecoItemDAO extends BaseDAO<TabelaPrecoItem> {

    private static TabelaPrecoItemDAO dao;

    public static TabelaPrecoItemDAO getInstance(Context context){
        if (dao == null){
            dao = new TabelaPrecoItemDAO(context);
        }

        return dao;
    }

    private TabelaPrecoItemDAO(Context context){
        super();
        super.ctx = context;
    }

    public TabelaPrecoItem getTabelaPrecoItem(String codigotabelapreco, String codigotabelaprecoitem){
        TabelaPrecoItem item = null;
        try {
            QueryBuilder<TabelaPrecoItem, Object> qb = getHelper().getDAO(TabelaPrecoItem.class).queryBuilder();
            qb.where().eq("codigotabelapreco", codigotabelapreco).and().eq("codigotabelaprecoitem", codigotabelaprecoitem);

            item = qb.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return item;
    }
}
