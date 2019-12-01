package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "materialsaldo")
public class MaterialSaldo implements IEntidade {

    @DatabaseField(generatedId = true)
    @Expose
    public Integer id;

    @DatabaseField
    @Expose
    public String codigomaterial;

    @DatabaseField
    @Expose
    public String descricao;

    @DatabaseField
    @Expose
    public String unidade;

    @DatabaseField
    @Expose
    public float saldo;

    @DatabaseField
    @Expose
    public float precovenda1;

    public MaterialSaldo(){}
    public MaterialSaldo(String codigomaterial, String descricao, String unidade, float saldo, float precovenda1) {
        this.codigomaterial = codigomaterial;
        this.descricao = descricao;
        this.unidade = unidade;
        this.saldo = saldo;
        this.precovenda1 = precovenda1;
    }
}
