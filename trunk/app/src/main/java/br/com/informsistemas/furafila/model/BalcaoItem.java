package br.com.informsistemas.furafila.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.math.BigDecimal;

public class BalcaoItem implements Serializable {

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
    public BigDecimal percdescontosubtotal;

    @Expose
    public BigDecimal totaldesconto;

    @Expose
    public BigDecimal percdesconto;

    @Expose
    public BigDecimal totaldescontoitens;

    @Expose
    public BigDecimal totalliquido;

    @Expose
    public BigDecimal valor;

    @Expose
    public BigDecimal custo;

    @Expose
    public String codigoparceiro;

    @Expose
    public String unidade;

    @Expose
    public BigDecimal fator;

    @Expose
    public BigDecimal quantidade;

    @Expose
    public String codigomaterial;

    @Expose
    public BigDecimal valordesconto;

    @Expose
    public BigDecimal desconto;

    @Expose
    public String cancelado;
    //    public Aliquota: Currency read FAliquota write FAliquota;
    //    public CodigoGrade: string read FCodigoGrade write FCodigoGrade;
    @Expose
    public String tipotributacao;
    //    public CodigoItem: Integer read FCodigoItem write FCodigoItem;
    @Expose
    public String codigoauxiliar;

    @Expose
    public String descricao;
    //    public Complemento: string read FComplemento write FComplemento;

    @Expose
    public String tipo;

    @Expose
    public String fatorunidade;

}
