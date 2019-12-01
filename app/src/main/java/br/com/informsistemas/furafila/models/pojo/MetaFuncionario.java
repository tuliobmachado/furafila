package br.com.informsistemas.furafila.models.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "metafuncionario")
public class MetaFuncionario implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String codigofuncionario;

    @DatabaseField
    public String descricao;

    @DatabaseField
    public Date dia;

    @DatabaseField
    public Integer totaldiames;

    @DatabaseField
    public float metamensal;

    @DatabaseField
    public float metarealizada;

    @DatabaseField
    public float metadiaria;

    public float metaarealizar;

    public MetaFuncionario(){}
    public MetaFuncionario(String codigofuncionario, String descricao, Date dia, Integer totaldiames, float metamensal, float metarealizada, float metadiaria) {
        this.descricao = descricao;
        this.codigofuncionario = codigofuncionario;
        this.dia = dia;
        this.totaldiames = totaldiames;
        this.metamensal = metamensal;
        this.metarealizada = metarealizada;
        this.metadiaria = metadiaria;
        this.metaarealizar = 0;
    }
}
