package br.com.informsistemas.furafila.models.pojo;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Meta implements Serializable {

    @Expose
    public String status;

    @Expose
    public String message;

    @Expose
    public Integer quantity;

    @Expose
    public Integer rowset;
}
