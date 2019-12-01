package br.com.informsistemas.furafila.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.math.BigDecimal;

public class Balcao implements Serializable {

    @Expose
    public Integer ordembalcao;

    @Expose
    public Integer documentopaf;

    @Expose
    public Integer documentodav;

    @Expose
    public Integer documentodavos;

    @Expose
    public String ordemmovimento;

    @Expose
    public String codigovendedor;

    @Expose
    public String codigoparceiro;

    @Expose
    public String descricaoparceiro;

    @Expose
    public String cpfparceiro;

    @Expose
    public BigDecimal percdesconto;

    @Expose
    public BigDecimal totaldescontoitens;

    @Expose
    public BigDecimal totalliquido;

}
