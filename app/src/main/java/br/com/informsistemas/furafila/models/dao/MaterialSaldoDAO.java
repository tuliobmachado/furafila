package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.MaterialSaldo;

public class MaterialSaldoDAO extends BaseDAO<MaterialSaldo> {

    private static MaterialSaldoDAO dao;

    public static MaterialSaldoDAO getInstance(Context context){
        if (dao == null){
            dao = new MaterialSaldoDAO(context);
        }

        return dao;
    }

    private MaterialSaldoDAO(Context context){
        super();
        super.ctx = context;
    }

    public List<MaterialSaldo> pesquisaLista(String s){
        List<MaterialSaldo> materialSaldo = new ArrayList<>();

        for (MaterialSaldo m : Constants.DTO.listMaterialSaldo){

            if (m.descricao.toUpperCase().contains(s.toUpperCase()) || m.unidade.toUpperCase().contains(s.toUpperCase()) || Float.toString(m.precovenda1).toUpperCase().contains(s.toUpperCase())){
                materialSaldo.add(m);
            }
        }

        return materialSaldo;
    }

}
