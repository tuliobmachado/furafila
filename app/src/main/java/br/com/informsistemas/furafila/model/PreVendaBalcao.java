package br.com.informsistemas.furafila.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class PreVendaBalcao implements Serializable {

    @Expose
    public Balcao balcao;

    @Expose
    public List<BalcaoItem> balcaoitem;

    @Expose
    public List<BalcaoParcela> balcaoparcela;

}
