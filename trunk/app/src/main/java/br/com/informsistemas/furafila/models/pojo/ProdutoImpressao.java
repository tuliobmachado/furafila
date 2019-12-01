package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "dado_impressao_produtos")
public class ProdutoImpressao implements IEntidade {

    @DatabaseField(generatedId = true)
    @Expose
    public Integer id;

    @DatabaseField
    @Expose
    public Integer movimento_id;

    @DatabaseField
    @Expose
    public String CodigoMaterial;

    @DatabaseField
    @Expose
    public String Descricao;

    @DatabaseField
    @Expose
    public Double Custo;

    @DatabaseField
    @Expose
    public Double TotalItem;

    @DatabaseField
    @Expose
    public Double Quantidade;

    @DatabaseField
    @Expose
    public String Unidade;

    public ProdutoImpressao(){}
}
