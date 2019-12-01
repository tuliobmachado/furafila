package br.com.informsistemas.furafila.models.dao;

import android.content.Context;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Atualizacao;

public class AtualizacaoDAO extends BaseDAO<Atualizacao> {

    private static AtualizacaoDAO dao;

    public static AtualizacaoDAO getInstance(Context context){
        if (dao == null){
            dao = new AtualizacaoDAO(context);
        }

        return dao;
    }

    private AtualizacaoDAO(Context context){
        super();
        super.ctx = context;
    }

    public Atualizacao findByNomeTabela(String nomeTabela){
        List<Atualizacao> listAtualizacao = null;
        Atualizacao atualizacao = null;

        try {
            listAtualizacao = getHelper().getDAO(Atualizacao.class).queryForEq("nometabela", nomeTabela);

            if (listAtualizacao.size() > 0){
                atualizacao = listAtualizacao.get(0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return atualizacao;

    }

    public Boolean VerificaSincronia(){
        Boolean value = false;
        List<Atualizacao> listAtualizacao = null;

        try {
            listAtualizacao = getHelper().getDAO(Atualizacao.class).queryForAll();

            if (listAtualizacao.size() == 0){
                value = true;
            }else{
                for (int i = 0; i < listAtualizacao.size(); i++) {
                    if (listAtualizacao.get(i).nometabela.equals("PARCEIROVENCIMENTO")) {
                        if (listAtualizacao.get(i).dataultimasincronia == null) {
                            value = true;

                            break;
                        } else {
                            value = Misc.CompareDate(new Date(), listAtualizacao.get(i).dataultimasincronia);

                            if (!value){
                                value = Misc.CompareTime(new Date(), listAtualizacao.get(i).dataultimasincronia);
                            }

                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            value = true;
            e.printStackTrace();
        }

        return value;
    }
}
