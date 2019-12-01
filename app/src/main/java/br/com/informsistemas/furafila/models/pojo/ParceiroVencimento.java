package br.com.informsistemas.furafila.models.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "parceirolancamento")
public class ParceiroVencimento implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String codigoparceiro;

    @DatabaseField
    public Date dataemissao;

    @DatabaseField
    public Date datavencimento;

    @DatabaseField
    public float valor;

    @DatabaseField
    public String status;

    public ParceiroVencimento(){}
    public ParceiroVencimento(String codigoparceiro, Date dataemissao, Date datavencimento, float valor, String status) {
        this.codigoparceiro = codigoparceiro;
        this.dataemissao = dataemissao;
        this.datavencimento = datavencimento;
        this.valor = valor;
        this.status = status;
    }
}
