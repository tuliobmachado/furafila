package br.com.informsistemas.furafila.controller.rest.Request;

import java.util.Date;

import br.com.informsistemas.furafila.models.pojo.DadoSincronia;

public class RequestSincParceiro extends DadoSincronia {

    public String codigofuncionario;

    public RequestSincParceiro(String codigofuncionario, String codigoconfiguracao, String cnpj, Date dataatualizacao) {
        super(codigoconfiguracao, cnpj, dataatualizacao);
        this.codigofuncionario = codigofuncionario;
    }
}
