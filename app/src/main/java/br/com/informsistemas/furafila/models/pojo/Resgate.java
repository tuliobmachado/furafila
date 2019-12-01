package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

public class Resgate implements IEntidade {

    @Expose
    public String numeromovimento;

    @Expose
    public String descricao;

    @Expose
    public Date data;

    @Expose
    public BigDecimal totalliquido;

    @Expose
    public String ordemmovimento;

    @Expose
    public String vendedor;

    @Expose
    public Integer ordembalcao;

    @Expose
    public Integer documentodav;

    public Resgate(String ordemmovimento, String numeromovimento, String descricao, String vendedor, Integer ordembalcao, Integer documentodav, Date data, BigDecimal totalliquido){
        this.ordemmovimento = ordemmovimento;
        this.numeromovimento = numeromovimento;
        this.descricao = descricao;
        this.vendedor = vendedor;
        this.ordembalcao = ordembalcao;
        this.documentodav = documentodav;
        this.data = data;
        this.totalliquido = totalliquido;
    }

}
