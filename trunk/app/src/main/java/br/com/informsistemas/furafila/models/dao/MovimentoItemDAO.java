package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;

public class MovimentoItemDAO extends BaseDAO<MovimentoItem> {

    private static MovimentoItemDAO dao;

    public static MovimentoItemDAO getInstance(Context context) {
        if (dao == null) {
            dao = new MovimentoItemDAO(context);
        }

        return dao;
    }

    private MovimentoItemDAO(Context context) {
        super();
        super.ctx = context;
    }

    public List<MovimentoItem> findByMovimentoId(int id){
        List<MovimentoItem> items = null;

        try{
            QueryBuilder<Movimento, Object> movimentoQB = getHelper().getDAO(Movimento.class).queryBuilder();
            QueryBuilder<MovimentoItem, Object> movimentoItemQB = getHelper().getDAO(MovimentoItem.class).queryBuilder();
            movimentoQB.where().eq("id", id);

            items = movimentoItemQB.join(movimentoQB).query();
        } catch (SQLException e){
            e.printStackTrace();
            return items;
        }

        return items;
    }

    public MovimentoItem findByMovimentoIdItem(Integer id, String codigomaterial){
        List<MovimentoItem> items = null;
        MovimentoItem item = null;

        try{
            QueryBuilder<Movimento, Object> movimentoQB = getHelper().getDAO(Movimento.class).queryBuilder();
            QueryBuilder<MovimentoItem, Object> movimentoItemQB = getHelper().getDAO(MovimentoItem.class).queryBuilder();
            movimentoQB.where().eq("id", id);
            movimentoItemQB.where().eq("codigomaterial", codigomaterial);

            items = movimentoItemQB.join(movimentoQB).query();

            if (items.size() > 0){
                item = items.get(0);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return item;
        }

        return item;
    }

    public void deleteByMovimentoId(Integer movimentoId, String codigoMaterial){
        DeleteBuilder<MovimentoItem, Object> deleteBuilder = getHelper().getDAO(MovimentoItem.class).deleteBuilder();
        try {
            deleteBuilder.where().eq("movimento_id", movimentoId);
            deleteBuilder.where().eq("codigomaterial", codigoMaterial);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
