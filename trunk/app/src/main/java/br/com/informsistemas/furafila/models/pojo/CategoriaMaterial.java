package br.com.informsistemas.furafila.models.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "categoriamaterial")
public class CategoriaMaterial implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String codigogrupo;

    @DatabaseField
    public  String codigomaterial;

    @DatabaseField
    public Date atualizacao;

    public CategoriaMaterial(){}
    public CategoriaMaterial(String codigogrupo, String codigomaterial, Date atualizacao) {
        this.codigogrupo = codigogrupo;
        this.codigomaterial = codigomaterial;
        this.atualizacao = atualizacao;
    }
}
