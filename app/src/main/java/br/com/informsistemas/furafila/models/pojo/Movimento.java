package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "movimento")
public class Movimento implements IEntidade {

    @DatabaseField(generatedId = true)
    @Expose
    public Integer id;

    @DatabaseField
    @Expose
    public String codigoempresa;

    @DatabaseField
    @Expose
    public String codigofilialcontabil;

    @DatabaseField
    @Expose
    public String codigoalmoxarifado;

    @DatabaseField
    @Expose
    public String codigoparceiro;

    @DatabaseField
    @Expose
    public String codigooperacao;

    @DatabaseField
    @Expose
    public String codigotabelapreco;

    @DatabaseField
    @Expose
    public String observacao;

    @DatabaseField
    @Expose
    public float totalliquido;

    @DatabaseField
    @Expose
    public String sincronizado;

    @DatabaseField
    @Expose
    public Date data;

    @DatabaseField
    @Expose
    public Date datainicio;

    @DatabaseField
    @Expose
    public Date datafim;

    @DatabaseField
    @Expose
    public Date dataalteracao;

    @DatabaseField
    @Expose
    public String longitude;

    @DatabaseField
    @Expose
    public String latitude;

    @DatabaseField
    @Expose
    public String MD5;

    @DatabaseField
    @Expose
    public String cpf;

    @DatabaseField
    @Expose
    public String resgate;

    @DatabaseField
    @Expose
    public float troco;

    public Movimento(){}
    public Movimento(String codigoempresa, String codigofilialcontabil, String codigoalmoxarifado, String codigooperacao,
                     String codigotabelapreco, String codigoparceiro, String observacao, float totalliquido, String sincronizado,
                     Date data, Date datainicio, Date datafim, Date dataalteracao, String longitude, String latitude, String MD5,
                     String cpf, String resgate, float troco) {
        this.codigoempresa = codigoempresa;
        this.codigofilialcontabil = codigofilialcontabil;
        this.codigoalmoxarifado = codigoalmoxarifado;
        this.codigooperacao = codigooperacao;
        this.codigotabelapreco = codigotabelapreco;
        this.codigoparceiro = codigoparceiro;
        this.observacao = observacao;
        this.totalliquido = totalliquido;
        this.sincronizado = "F";
        this.data = data;
        this.datainicio = datainicio;
        this.datafim = datafim;
        this.dataalteracao = dataalteracao;
        this.longitude = longitude;
        this.latitude = latitude;
        this.MD5 = MD5;
        this.cpf = cpf;
        this.resgate = resgate;
        this.troco = troco;
    }
}
