package br.com.informsistemas.furafila.rest.request;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class ResgateRequest implements Serializable {

    @Expose
    public String cnpj;

    @Expose
    public String parceiro;

    @Expose
    public String numeromovimento;

    @Expose
    public String codigoconfiguracao;

    @Expose
    public Date data;

    @Expose
    public String codigofilial;

    @Expose
    public Boolean balcao;

    public ResgateRequest(String codigoconfiguracao, String codigofilial, String cnpj, String parceiro, String numeromovimento, Date data, Boolean balcao){
        this.cnpj = cnpj;
        this.parceiro = parceiro;
        this.codigoconfiguracao = codigoconfiguracao;
        this.numeromovimento = numeromovimento;
        this.data = data;
        this.codigofilial = codigofilial;
        this.balcao = balcao;
    }
}
