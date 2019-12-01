package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;
import br.com.informsistemas.furafila.models.pojo.MovimentoParcela;

public class MovimentoDAO extends BaseDAO<Movimento> {

    private static MovimentoDAO dao;

    public static MovimentoDAO getInstance(Context context) {
        if (dao == null) {
            dao = new MovimentoDAO(context);
        }

        return dao;
    }

    private MovimentoDAO(Context context) {
        super();
        super.ctx = context;
    }

    public void deleteAllPedidos(){
        List<Movimento> items = null;

        try{
            items = getHelper().getDAO(Movimento.class).queryForAll();

            for (int i = 0; i < items.size(); i++) {
                List<MovimentoParcela> listMovimentoParcela = MovimentoParcelaDAO.getInstance(ctx).findByMovimentoId(items.get(i).id);

                for (int x = 0; x < listMovimentoParcela.size(); x++) {
                    getHelper().getDAO(MovimentoParcela.class).delete(listMovimentoParcela.get(x));
                }

                List<MovimentoItem> listMovimentoItem = MovimentoItemDAO.getInstance(ctx).findByMovimentoId(items.get(i).id);

                for (int x = 0; x > listMovimentoItem.size(); x++){
                    getHelper().getDAO(MovimentoItem.class).delete(listMovimentoItem.get(x));
                }

                getHelper().getDAO(Movimento.class).delete(items.get(i));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean pedidoPendente(){
        Boolean value = false;
        Movimento movimento = null;

        movimento = findByIdAuxiliar("sincronizado", "F");

        if (movimento != null){
            value = true;
        }

        return value;
    }

    public List<Movimento> getMovimentoPeriodo(String codigoParceiro, Date dataInicio, Date dataFim){
        List<Movimento> items = null;

        QueryBuilder<Movimento, Object> builder = getHelper().getDAO(Movimento.class).queryBuilder();
        Where<Movimento, Object> where = builder.where();

        try {
            where.between("data", dataInicio, dataFim);

            if (codigoParceiro.length() > 0){
                where.and().eq("codigoparceiro", codigoParceiro);
            }
            items = builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }
}
