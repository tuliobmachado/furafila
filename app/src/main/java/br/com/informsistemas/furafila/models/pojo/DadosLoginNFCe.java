package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;

import br.com.informsistemas.furafila.models.utils.IEntidade;

public class DadosLoginNFCe implements IEntidade {

    @Expose
    public String codigoforma;

    @Expose
    public String codigodinheiro;

    @Expose
    public String codigocredito;

    @Expose
    public String codigodebito;
}
