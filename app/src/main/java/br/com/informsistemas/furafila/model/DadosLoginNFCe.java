package br.com.informsistemas.furafila.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import br.com.informsistemas.furafila.models.utils.IEntidade;

@DatabaseTable(tableName = "dadosloginnfce")
public class DadosLoginNFCe implements IEntidade {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public String codigoforma;

    @DatabaseField
    public String codigodinheiro;

    @DatabaseField
    public String codigocredito;

    @DatabaseField
    public String codigodebito;

    DadosLoginNFCe(){}
    DadosLoginNFCe(String codigoforma, String codigodinheiro, String codigocredito, String codigodebito){
        this.codigoforma = codigoforma;
        this.codigodinheiro = codigodinheiro;
        this.codigocredito = codigocredito;
        this.codigodebito = codigodebito;
    }

}
