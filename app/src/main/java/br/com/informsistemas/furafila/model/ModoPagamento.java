package br.com.informsistemas.furafila.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "cadmodopagamento")
public class ModoPagamento implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

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

    @DatabaseField
    public String padrao;

    public ModoPagamento(){}
    public ModoPagamento(String codigotipoevento, String descricao, String padrao, Date atualizacao, Float acrescimo, float valor) {
        this.codigotipoevento = codigotipoevento;
        this.descricao = descricao;
        this.padrao = padrao;
        this.atualizacao = atualizacao;
        this.acrescimo = acrescimo;
        this.valor = valor;
    }
}
