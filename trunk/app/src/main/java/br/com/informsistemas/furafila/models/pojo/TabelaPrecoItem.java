package br.com.informsistemas.furafila.models.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "tabelaprecoitem")
public class TabelaPrecoItem implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String codigotabelapreco;

    @DatabaseField
    public String codigotabelaprecoitem;

    @DatabaseField
    public float precovenda1;

    @DatabaseField
    public Date atualizacao;

    @DatabaseField
    public float desconto;

    public TabelaPrecoItem(){}
    public TabelaPrecoItem(String codigotabelapreco, String codigotabelaprecoitem, float precovenda1, Date atualizacao, float desconto) {
        this.codigotabelapreco = codigotabelapreco;
        this.codigotabelaprecoitem = codigotabelaprecoitem;
        this.precovenda1 = precovenda1;
        this.atualizacao = atualizacao;
        this.desconto = desconto;
    }
}
