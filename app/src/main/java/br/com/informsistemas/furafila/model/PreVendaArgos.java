package br.com.informsistemas.furafila.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;

public class PreVendaArgos implements Serializable {

    @Expose
    public Movimento movimento;

    @Expose
    public List<MovimentoItem> movimentoitem;

}
