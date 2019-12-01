package br.com.informsistemas.furafila.models.callback;

import br.com.informsistemas.furafila.controller.rest.Request.RequestPedido;
import br.com.informsistemas.furafila.controller.rest.Response.ResponsePedido;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PedidoService {

    @POST(Constants.URL.PEDIDO_GERAR)
    Call<RestResponse<ResponsePedido>> postPedido(@Body RequestPedido requestPedido);
}
