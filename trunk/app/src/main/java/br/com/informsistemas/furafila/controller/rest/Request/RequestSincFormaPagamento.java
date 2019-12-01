package br.com.informsistemas.furafila.controller.rest.Request;

import java.util.Date;

import br.com.informsistemas.furafila.models.pojo.DadoSincronia;

public class RequestSincFormaPagamento extends DadoSincronia {

    public RequestSincFormaPagamento(String codigoconfiguracao, String cnpj, Date dataatualizacao) {
        super(codigoconfiguracao, cnpj, dataatualizacao);
    }
}
