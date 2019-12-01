package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "dado_impressao_pagamentos")
public class PagamentoImpressao implements IEntidade {

    @DatabaseField(generatedId = true)
    @Expose
    public Integer id;

    @DatabaseField
    @Expose
    public Integer movimento_id;

    @DatabaseField
    @Expose
    public String Tipo;

    @DatabaseField
    @Expose
    public Double ValorDuplicata;

    @DatabaseField
    @Expose
    public String Descricao;

    public PagamentoImpressao(){}
}
