package br.com.informsistemas.furafila.controller.rest.Request;

import java.util.Date;

import br.com.informsistemas.furafila.models.pojo.DadoSincronia;

public class RequestSincParceiroVencimento extends DadoSincronia {

    public String codigoparceiro;

    public RequestSincParceiroVencimento(String codigoparceiro, String codigoconfiguracao, String cnpj, Date dataatualizacao) {
        super(codigoconfiguracao, cnpj, dataatualizacao);
        this.codigoparceiro = codigoparceiro;
    }
}
