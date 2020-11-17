package br.com.informsistemas.furafila.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "registro")
public class Registro implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String usuario;

    @DatabaseField
    public String senha;

    @DatabaseField
    public String imei;

    @DatabaseField
    public String cnpj;

    @DatabaseField
    public String token;

    @DatabaseField
    public String codigousuario;

    @DatabaseField
    public String nome;

    @DatabaseField
    public String status;

    @DatabaseField
    public String codigofilialcontabil;

    @DatabaseField
    public String codigofuncionario;

    @DatabaseField
    public String codigotabelapreco;

    @DatabaseField
    public String codigoempresa;

    @DatabaseField
    public String codigoalmoxarifado;

    @DatabaseField
    public String codigooperacao;

    @DatabaseField
    public String estado;

    @DatabaseField
    public String codigoconfiguracao;

    @DatabaseField
    public boolean utilizapauta;

    @DatabaseField
    public boolean utilizafatorpauta;

    @DatabaseField
    public String codigoaplicacao;

    @DatabaseField
    public String codigoformapagamento;

    @DatabaseField
    public String codigoparceiropadrao;

    @DatabaseField
    public String descricaoparceiropadrao;

    @DatabaseField
    public Boolean importamovimento;

    @DatabaseField
    public Boolean balcao;

    public List<DadosLoginNFCe> listdadosnfce;

    public Registro(){}
    public Registro(String usuario, String senha, String imei, String cnpj, String token, String codigousuario,
                    String nome, String status, String codigofilialcontabil, String codigofuncionario,
                    String codigotabelapreco, String codigoempresa, String codigoalmoxarifado,
                    String codigooperacao, String estado, String codigoconfiguracao, Boolean utilizapauta,
                    Boolean utilizafatorpauta, Boolean importamovimento, Boolean balcao, String codigoaplicacao, String codigoformapagamento,
                    String codigoparceiropadrao, String descricaoparceiropadrao) {
        this.usuario = usuario;
        this.senha = senha;
        this.imei = imei;
        this.cnpj = cnpj;
        this.token = token;
        this.nome = nome;
        this.codigousuario = codigousuario;
        this.status = status;
        this.codigofilialcontabil = codigofilialcontabil;
        this.codigofuncionario = codigofuncionario;
        this.codigotabelapreco = codigotabelapreco;
        this.codigoempresa = codigoempresa;
        this.codigoalmoxarifado = codigoalmoxarifado;
        this.codigooperacao = codigooperacao;
        this.estado = estado;
        this.codigoconfiguracao = codigoconfiguracao;
        this.utilizapauta = utilizapauta;
        this.utilizafatorpauta = utilizafatorpauta;
        this.importamovimento = importamovimento;
        this.balcao = balcao;
        this.codigoaplicacao = codigoaplicacao;
        this.codigoformapagamento = codigoformapagamento;
        this.codigoparceiropadrao = codigoparceiropadrao;
        this.descricaoparceiropadrao = descricaoparceiropadrao;
    }
}
