package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.MovimentoParcela;

public class MovimentoParcelaDAO extends BaseDAO<MovimentoParcela> {

    private static MovimentoParcelaDAO dao;

    public static MovimentoParcelaDAO getInstance(Context context){
        if (dao == null){
            dao = new MovimentoParcelaDAO(context);
        }

        return dao;
    }

    private MovimentoParcelaDAO(Context context){
        super();
        super.ctx = context;
    }

    public List<MovimentoParcela> findByMovimentoId(int id){
        List<MovimentoParcela> items = null;

        try{
            QueryBuilder<Movimento, Object> movimentoQB = getHelper().getDAO(Movimento.class).queryBuilder();
            QueryBuilder<MovimentoParcela, Object> movimentoParcelaQB = getHelper().getDAO(MovimentoParcela.class).queryBuilder();
            movimentoQB.where().eq("id", id);

            items = movimentoParcelaQB.join(movimentoQB).query();
        } catch (SQLException e){
            e.printStackTrace();
            return items;
        }

        return items;
    }

    public Float sumByMovimentoId(String campo, int id){
        GenericRawResults<String[]> rawResults = null;
        Float value = Float.valueOf(0);
        try {
            rawResults = getHelper().getDAO(MovimentoParcela.class)
                    .queryRaw("select sum("+campo+") from movimentoparcela where movimento_id ="+String.valueOf(id));

            String[] results = rawResults.getFirstResult();
            if (results[0] != null) {
                value = Float.parseFloat(results[0]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Float.valueOf(0);
        }

        return value;
    }
}
