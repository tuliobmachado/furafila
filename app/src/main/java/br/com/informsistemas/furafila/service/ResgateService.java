package br.com.informsistemas.furafila.service;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.model.Resgate;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.rest.request.ResgateRequest;
import br.com.informsistemas.furafila.rest.response.ResgateResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ResgateService {

    @POST(Constants.URL.RESGATE_CONSULTAR)
    Call<RestResponse<Resgate>> postConsultar(@Body ResgateRequest resgateRequest);

    @GET(Constants.URL.RESGATE_PREVENDA)
    Call<RestResponse<ResgateResponse>> getPreVenda(@Query("CNPJ") String CNPJ, @Query("codigoConfiguracao") String codigoConfiguracao, @Query("ordemMovimento") String ordemMovimento, @Query("ordemBalcao") Integer ordemBalcao);
}
