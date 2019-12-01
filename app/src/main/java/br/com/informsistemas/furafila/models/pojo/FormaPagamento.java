package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "cadformapagamento")
public class FormaPagamento implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String codigoforma;

    @DatabaseField
    public String codigotipoevento;

    @DatabaseField
    @Expose
    public String descricao;

    @DatabaseField
    public Date atualizacao;

    @DatabaseField
    public Float acrescimo;

    @DatabaseField
    public float valor;

    public FormaPagamento(){}
    public FormaPagamento(String codigoforma, String codigotipoevento, String descricao, Date atualizacao, Float acrescimo, float valor) {
        this.codigoforma = codigoforma;
        this.codigotipoevento = codigotipoevento;
        this.descricao = descricao;
        this.atualizacao = atualizacao;
        this.acrescimo = acrescimo;
        this.valor = valor;
    }
}
