package br.com.informsistemas.furafila.models.dao;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.informsistemas.furafila.models.utils.IEntidade;

public class BaseDAO<T extends IEntidade> {

    protected Context ctx;

    protected DatabaseHelper getHelper() {
        return DatabaseManager.getInstance().getHelper();
    }

    protected Dao<T, Object> getConnection() {
        return getHelper().getDAO(getEntityClass());
    }

    private Class getEntityClass() {
        ParameterizedType t = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class) t.getActualTypeArguments()[0];
    }

    public List<T> findAll() {
        try {
            return (List<T>) getHelper().getDAO(getEntityClass()).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    public T findById(Object id) {
        try {
            return (T) getHelper().getDAO(getEntityClass()).queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public T findByIdAuxiliar(String campo, Object codigo){
        List<T> listItem = new ArrayList<>();

        try{
            listItem = getHelper().getDAO(getEntityClass()).queryForEq(campo, codigo);

            if (listItem.size() > 0) {
                return listItem.get(0);
            }else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<T> findAllByIdAuxiliar(String campo, Object codigo){
        List<T> listItem = new ArrayList<>();

        try{
            listItem = getHelper().getDAO(getEntityClass()).queryForEq(campo, codigo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return listItem;
    }

    public T findFirst(){
        List<T> items = null;
        T item = null;

        try{
            QueryBuilder<T, Object> queryBuilder = getHelper().getDAO(getEntityClass()).queryBuilder();
            queryBuilder.limit((long) 1);

            items = queryBuilder.query();

            if (items.size() > 0){
                item = items.get(0);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return  null;
        }

        return item;
    }

    public List<T> findByNotIn(String campo, String codigo){
        List<T> items = null;
        codigo = codigo.replace("'", "");
        String[] codigoPrincipal = codigo.split(",");

        QueryBuilder<T, Object> queryBuilder = getHelper().getDAO(getEntityClass()).queryBuilder();

        try {
            queryBuilder.where().notIn(campo, codigoPrincipal);
            Log.i("FindByNotIn", queryBuilder.prepareStatementString());
            items = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public <T> void deleteAll(){
        DeleteBuilder<T, Object> deleteBuilder = getHelper().getDAO(getEntityClass()).deleteBuilder();
        try {
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createOrUpdate(T obj) {
        try {
            return getHelper().getDAO(getEntityClass()).createOrUpdate(obj).getNumLinesChanged() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    public boolean delete(T obj) {
        try {
            return getHelper().getDAO(getEntityClass()).delete(obj) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

}
