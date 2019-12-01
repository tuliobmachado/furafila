package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;

import br.com.informsistemas.furafila.models.utils.IEntidade;

public class Produtos implements IEntidade {

    @Expose
    public String CodigoMaterial;

    @Expose
    public String Descricao;

    @Expose
    public Double Custo;

    @Expose
    public Double TotalItem;

    @Expose
    public Double Quantidade;

    @Expose
    public String Unidade;
}
