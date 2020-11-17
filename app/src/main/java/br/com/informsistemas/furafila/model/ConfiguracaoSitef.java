package br.com.informsistemas.furafila.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "configuracao_clisitef")
public class ConfiguracaoSitef implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String sitefip;

    @DatabaseField
    public String storeid;

    @DatabaseField
    public String terminalid;

    public ConfiguracaoSitef(){}
    public ConfiguracaoSitef(String sitefip, String storeid, String terminalid){
        this.sitefip = sitefip;
        this.storeid = storeid;
        this.terminalid = terminalid;
    }
}
