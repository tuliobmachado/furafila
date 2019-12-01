package br.com.informsistemas.furafila.controller.rest.Request;

import java.io.Serializable;

public class RequestSincAtualizacao implements Serializable {

    public String cnpj;
    public String codigoconfiguracao;

    public RequestSincAtualizacao(String cnpj, String codigoconfiguracao) {
        this.cnpj = cnpj;
        this.codigoconfiguracao = codigoconfiguracao;
    }
}
