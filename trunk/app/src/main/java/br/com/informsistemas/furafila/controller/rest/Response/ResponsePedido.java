package br.com.informsistemas.furafila.controller.rest.Response;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.models.pojo.DadosImpressao;
import br.com.informsistemas.furafila.models.pojo.MaterialSaldo;

public class ResponsePedido implements Serializable {

    @Expose
    public Integer id;

    @Expose
    public List<MaterialSaldo> materialsaldo;

    @Expose
    public Date dataatualizacao;

    @Expose
    public String status;

    @Expose
    public DadosImpressao dadosimpressao;
}
