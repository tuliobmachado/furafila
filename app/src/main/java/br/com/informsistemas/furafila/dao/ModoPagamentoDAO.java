package br.com.informsistemas.furafila.dao;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import br.com.informsistemas.furafila.model.ModoPagamento;
import br.com.informsistemas.furafila.models.dao.BaseDAO;

public class ModoPagamentoDAO extends BaseDAO<ModoPagamento> {

    private static ModoPagamentoDAO dao;

    public static ModoPagamentoDAO getInstance(Context context){
        if (dao == null){
            dao = new ModoPagamentoDAO(context);
        }

        return dao;
    }

    private ModoPagamentoDAO(Context context){
        super();
        super.ctx = context;
    }

    public List<ModoPagamento> findAllModoPadrao(){
        List<ModoPagamento> items = null;

        QueryBuilder<ModoPagamento, Object> builder = getHelper().getDAO(ModoPagamento.class).queryBuilder();
        Where<ModoPagamento, Object> where = builder.where();

        try {
            where.eq("padrao", "T");

            items = builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public ModoPagamento findByIdModoPadrao(String campo, Object codigo){
        ModoPagamento items = null;

        QueryBuilder<ModoPagamento, Object> builder = getHelper().getDAO(ModoPagamento.class).queryBuilder();
        Where<ModoPagamento, Object> where = builder.where();

        try {
            where.eq("padrao", "T");
            where.and().eq(campo, codigo);

            items = builder.queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public ModoPagamento findByIdModoResgate(String campo, Object codigo){
        ModoPagamento items = null;

        QueryBuilder<ModoPagamento, Object> builder = getHelper().getDAO(ModoPagamento.class).queryBuilder();
        Where<ModoPagamento, Object> where = builder.where();

        try {
            where.eq("padrao", "F");
            where.and().eq(campo, codigo);

            items = builder.queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }
}
