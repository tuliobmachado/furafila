package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Categoria;

public class CategoriaDAO extends BaseDAO<Categoria> {

    private static CategoriaDAO dao;

    public static CategoriaDAO getInstance(Context context){
        if (dao == null){
            dao = new CategoriaDAO(context);
        }

        return dao;
    }

    private CategoriaDAO(Context context){
        super();
        super.ctx = context;
    }

    public List<Categoria> getListCategoria() {

        List<Categoria> listCategoria = null;
        try {
            listCategoria = new Misc().cloneCategoriaPesquisa(Constants.DTO.listPesquisaCategoria);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return listCategoria;
    }

    public List<Categoria> pesquisaLista(String s){
        List<Categoria> parceiroList = new ArrayList<>();

        for (Categoria p : Constants.DTO.listPesquisaCategoria){
            if (p.descricao.toUpperCase().contains(s.toUpperCase())){
                parceiroList.add(p);
            }
        }

        return parceiroList;
    }
}
