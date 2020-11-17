package br.com.informsistemas.furafila.models.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.informsistemas.furafila.model.ConfiguracaoSitef;
import br.com.informsistemas.furafila.model.DadosLoginNFCe;
import br.com.informsistemas.furafila.models.pojo.Atualizacao;
import br.com.informsistemas.furafila.models.pojo.Categoria;
import br.com.informsistemas.furafila.models.pojo.CategoriaMaterial;
import br.com.informsistemas.furafila.models.pojo.DadosImpressao;
import br.com.informsistemas.furafila.model.ModoPagamento;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.MaterialEstado;
import br.com.informsistemas.furafila.models.pojo.MaterialSaldo;
import br.com.informsistemas.furafila.models.pojo.MetaFuncionario;
import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;
import br.com.informsistemas.furafila.models.pojo.MovimentoParcela;
import br.com.informsistemas.furafila.models.pojo.PagamentoImpressao;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.pojo.ParceiroVencimento;
import br.com.informsistemas.furafila.models.pojo.ProdutoImpressao;
import br.com.informsistemas.furafila.model.Registro;
import br.com.informsistemas.furafila.models.pojo.TabelaPrecoItem;
import br.com.informsistemas.furafila.models.utils.IEntidade;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String databaseName = "furafila.db";
    private static final int databaseVersion = 1;

    private Map<Class, Dao<IEntidade, Object>> daos = new HashMap<Class, Dao<IEntidade, Object>>();

    public DatabaseHelper(Context context){
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource cs) {
        try {
            TableUtils.createTable(cs, Atualizacao.class);
            TableUtils.createTable(cs, Registro.class);
            TableUtils.createTable(cs, DadosLoginNFCe.class);
            TableUtils.createTable(cs, Parceiro.class);
            TableUtils.createTable(cs, ParceiroVencimento.class);
            TableUtils.createTable(cs, Categoria.class);
            TableUtils.createTable(cs, CategoriaMaterial.class);
            TableUtils.createTable(cs, Material.class);
            TableUtils.createTable(cs, MaterialEstado.class);
            TableUtils.createTable(cs, MaterialSaldo.class);
            TableUtils.createTable(cs, TabelaPrecoItem.class);
            TableUtils.createTable(cs, ModoPagamento.class);
            TableUtils.createTable(cs, Movimento.class);
            TableUtils.createTable(cs, MovimentoItem.class);
            TableUtils.createTable(cs, MovimentoParcela.class);
            TableUtils.createTable(cs, MetaFuncionario.class);
            TableUtils.createTable(cs, DadosImpressao.class);
            TableUtils.createTable(cs, ProdutoImpressao.class);
            TableUtils.createTable(cs, PagamentoImpressao.class);
            TableUtils.createTable(cs, ConfiguracaoSitef.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> Dao<T, Object> getDAO(Class<T> entidadeClass) {
        Dao<IEntidade, Object> dao = null;
        if (daos.get(entidadeClass) == null) {
            try {
                dao = getDao((Class) entidadeClass);
            } catch (SQLException e) {
                Log.e(DatabaseHelper.class.getName(), "exception during getDAO", e);
                throw new RuntimeException(e);
            }
            daos.put(entidadeClass, dao);
        }

        return (Dao<T, Object>) daos.get(entidadeClass);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        List<String> allSQL = new ArrayList<>();

        switch (oldVersion){
            case 1:
        }

        for (String sql: allSQL){
            db.execSQL(sql);
        }
    }

    public void onDeleteAllTable(){
        ConnectionSource cs = this.getConnectionSource();

        try {
            TableUtils.dropTable(cs, MetaFuncionario.class, true);
            TableUtils.dropTable(cs, MovimentoParcela.class, true);
            TableUtils.dropTable(cs, MovimentoItem.class, true);
            TableUtils.dropTable(cs, Movimento.class, true);
            TableUtils.dropTable(cs, ModoPagamento.class, true);
            TableUtils.dropTable(cs, TabelaPrecoItem.class, true);
            TableUtils.dropTable(cs, MaterialSaldo.class, true);
            TableUtils.dropTable(cs, MaterialEstado.class, true);
            TableUtils.dropTable(cs, Material.class, true);
            TableUtils.dropTable(cs, CategoriaMaterial.class, true);
            TableUtils.dropTable(cs, Categoria.class, true);
            TableUtils.dropTable(cs, ParceiroVencimento.class, true);
            TableUtils.dropTable(cs, Parceiro.class, true);
            TableUtils.dropTable(cs, DadosLoginNFCe.class, true);
            TableUtils.dropTable(cs, Registro.class, true);
            TableUtils.dropTable(cs, Atualizacao.class, true);
            TableUtils.dropTable(cs, ConfiguracaoSitef.class, true);

            onCreate(this.getWritableDatabase(), cs);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
