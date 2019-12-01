package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "movimentoparcela")
public class MovimentoParcela implements IEntidade {

    @DatabaseField(generatedId = true)
    @Expose
    public Integer id;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true, columnName = "movimento_id")
    @Expose(serialize = false)
    public Movimento movimento;

    @DatabaseField
    @Expose
    public String codigoforma;

    @DatabaseField
    @Expose
    public String codigotipoevento;

    @DatabaseField
    @Expose
    public String descricaotipoevento;

    @DatabaseField
    @Expose
    public float valor;

    @DatabaseField
    @Expose
    public float troco;

    public MovimentoParcela(){}
    public MovimentoParcela(Movimento movimento, String codigoforma, String codigotipoevento, String descricaotipoevento, float valor, float troco) {
        this.movimento = movimento;
        this.codigoforma = codigoforma;
        this.codigotipoevento = codigotipoevento;
        this.descricaotipoevento = descricaotipoevento;
        this.valor = valor;
        this.troco = troco;
    }
}
