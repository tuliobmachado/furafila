package br.com.informsistemas.furafila.models.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "cadparceiro")
public class Parceiro implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String codigoparceiro;

    @DatabaseField
    public String descricao;

    @DatabaseField
    public String cpfcgc;

    @DatabaseField
    public String diavencimento;

    @DatabaseField
    public String status;

    @DatabaseField
    public String telefone;

    @DatabaseField
    public String telefone2;

    @DatabaseField
    public String endereco;

    @DatabaseField
    public String bairro;

    @DatabaseField
    public String cidade;

    @DatabaseField
    public String estado;

    @DatabaseField
    public String pontoreferencia;

    @DatabaseField
    public Date datacadastro;

    @DatabaseField
    public Date dataultimopedido;

    @DatabaseField
    public Date datanascimento;

    @DatabaseField
    public Float limitecredito;

    @DatabaseField
    public Float valoremaberto;

    @DatabaseField
    public Float taxa;

    @DatabaseField
    public String obs;

    @DatabaseField
    public String tipo;

    @DatabaseField
    public String numerologradouro;

    @DatabaseField
    public String cep;

    @DatabaseField
    public String codigomunicipio;

    @DatabaseField
    public String nomefantasia;

    @DatabaseField
    public String email;

    @DatabaseField
    public Date atualizacao;

    @DatabaseField
    public String alterado;

    @DatabaseField
    public String emailnfe;

    @DatabaseField
    public String campolivrea3;

    @DatabaseField
    public String campolivrea4;

    @DatabaseField
    public String codigotabelapreco;

    @DatabaseField
    public float percdescontopadrao;

    @DatabaseField
    public String codigoformapagamento;

    @DatabaseField
    public String formaspermitidas;

    public Parceiro(){}
    public Parceiro(String codigoparceiro, String descricao, String cpfcgc, String diavencimento, String status, String telefone, String telefone2, String endereco, String bairro, String cidade, String estado, String pontoreferencia, Date datacadastro, Date dataultimopedido, Date datanascimento, Float limitecredito, Float valoremaberto, Float taxa, String obs, String tipo, String numerologradouro, String cep, String codigomunicipio, String nomefantasia, String email, Date atualizacao, String alterado, String emailnfe, String campolivrea3, String campolivrea4, String codigotabelapreco, float percdescontopadrao, String codigoformapagamento, String formaspermitidas) {
        this.codigoparceiro = codigoparceiro;
        this.descricao = descricao;
        this.cpfcgc = cpfcgc;
        this.diavencimento = diavencimento;
        this.status = status;
        this.telefone = telefone;
        this.telefone2 = telefone2;
        this.endereco = endereco;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.pontoreferencia = pontoreferencia;
        this.datacadastro = datacadastro;
        this.dataultimopedido = dataultimopedido;
        this.datanascimento = datanascimento;
        this.limitecredito = limitecredito;
        this.valoremaberto = valoremaberto;
        this.taxa = taxa;
        this.obs = obs;
        this.tipo = tipo;
        this.numerologradouro = numerologradouro;
        this.cep = cep;
        this.codigomunicipio = codigomunicipio;
        this.nomefantasia = nomefantasia;
        this.email = email;
        this.atualizacao = atualizacao;
        this.alterado = alterado;
        this.emailnfe = emailnfe;
        this.campolivrea3 = campolivrea3;
        this.campolivrea4 = campolivrea4;
        this.codigotabelapreco = codigotabelapreco;
        this.percdescontopadrao = percdescontopadrao;
        this.codigoformapagamento = codigoformapagamento;
        this.formaspermitidas = formaspermitidas;
    }

    public String toString(){
        if (this.nomefantasia.length() == 0) {
            return this.descricao;
        }else{
            return this.nomefantasia;
        }
    }
}
