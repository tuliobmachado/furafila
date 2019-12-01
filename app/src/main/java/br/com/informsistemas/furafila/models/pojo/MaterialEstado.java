package br.com.informsistemas.furafila.models.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "materialestado")
public class MaterialEstado implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String codigomaterial;

    @DatabaseField
    public String estado;

    @DatabaseField
    public Date atualizacao;

    @DatabaseField
    public float mva;

    @DatabaseField
    public float icms_interestadual;

    @DatabaseField
    public float icms_interno;

    @DatabaseField
    public float fecoep;

    @DatabaseField
    public float pautafiscal;

    public MaterialEstado(){}
    public MaterialEstado(String codigomaterial, String estado, Date atualizacao, float mva, float icms_interestadual, float icms_interno, float fecoep, float pautafiscal) {
        this.codigomaterial = codigomaterial;
        this.estado = estado;
        this.atualizacao = atualizacao;
        this.mva = mva;
        this.icms_interestadual = icms_interestadual;
        this.icms_interno = icms_interno;
        this.fecoep = fecoep;
        this.pautafiscal = pautafiscal;
    }
}
