package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "dado_impressao")
public class DadosImpressao implements IEntidade {

    @DatabaseField(generatedId = true)
    @Expose
    public Integer id;

    @DatabaseField
    @Expose
    public Integer movimento_id;

    @DatabaseField
    @Expose
    public Double Troco;

    @DatabaseField
    @Expose
    public String InformacoesComplementares;

    @DatabaseField
    @Expose
    public String QrCode;

    @DatabaseField
    @Expose
    public String ChaveNota;

    @DatabaseField
    @Expose
    public String ChaveUrl;

    @DatabaseField
    @Expose
    public Boolean AmbienteProducao;

    @DatabaseField
    @Expose
    public Integer Serie;

    @DatabaseField
    @Expose
    public Date DataAutorizacao;

    @DatabaseField
    @Expose
    public String InscricaoMunicipal;

    @DatabaseField
    @Expose
    public String EnderecoEmpresa;

    @DatabaseField
    @Expose
    public Date DataEmissao;

    @DatabaseField
    @Expose
    public String RazaoSocial;

    @DatabaseField
    @Expose
    public String CnpjEmpresa;

    @DatabaseField
    @Expose
    public String CPFCliente;

    @DatabaseField
    @Expose
    public String NomeCliente;

    @DatabaseField
    @Expose
    public String EnderecoCliente;

    @DatabaseField
    @Expose
    public String ProtocoloAutorizacao;

    @DatabaseField
    @Expose
    public Double ValorAcrescimos;

    @DatabaseField
    @Expose
    public Double ValorDescontos;

    @DatabaseField
    @Expose
    public Double ValorProdutos;

    @DatabaseField
    @Expose
    public Double ValorAPagar;

    @DatabaseField
    @Expose
    public String InscricaoEstadual;

    @DatabaseField
    @Expose
    public Integer NumeroNota;

    @Expose
    public List<PagamentoImpressao> ListaFormasPagamento;

    @Expose
    public List<ProdutoImpressao> ListaProdutos;

    public DadosImpressao(){}
}
