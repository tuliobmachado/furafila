package br.com.informsistemas.furafila.models.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "atualizacao")
public class Atualizacao implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String codigoconfiguracao;

    @DatabaseField
    public String nometabela;

    @DatabaseField
    public Date datasincparcial;

    @DatabaseField
    public Date datasinctotal;

    @DatabaseField
    public String situacao;

    @DatabaseField
    public Date datasincmarcado;

    @DatabaseField
    public Date dataultimasincronia;

    public Atualizacao(){}
    public Atualizacao(String codigoconfiguracao, String nometabela, Date datasincparcial, Date datasinctotal, String situacao, Date datasincmarcado) {
        this.codigoconfiguracao = codigoconfiguracao;
        this.nometabela = nometabela;
        this.datasincparcial = datasincparcial;
        this.datasinctotal = datasinctotal;
        this.situacao = situacao;
        this.datasincmarcado = datasincmarcado;
        this.dataultimasincronia = null;
    }
}
