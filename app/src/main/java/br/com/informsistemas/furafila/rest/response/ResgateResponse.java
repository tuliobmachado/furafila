package br.com.informsistemas.furafila.rest.response;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import br.com.informsistemas.furafila.model.PreVendaArgos;
import br.com.informsistemas.furafila.model.PreVendaBalcao;

public class ResgateResponse implements Serializable {

    @Expose
    public PreVendaArgos prevendaargos;

    @Expose
    public PreVendaBalcao prevendabalcao;
}
