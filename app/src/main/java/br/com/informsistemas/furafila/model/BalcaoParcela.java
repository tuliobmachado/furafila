package br.com.informsistemas.furafila.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.math.BigDecimal;

public class BalcaoParcela implements Serializable {

    @Expose
    public String codigoformapagamento;

    @Expose
    public String codigotipoevento;

    @Expose
    public String descricaotipoevento;

    @Expose
    public BigDecimal valor;

}
