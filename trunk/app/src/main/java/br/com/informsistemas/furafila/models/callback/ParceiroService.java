package br.com.informsistemas.furafila.models.callback;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ParceiroService {

    @GET(Constants.URL.PARCEIRO_LISTAR)
    Call<RestResponse<Parceiro>> getListar(@Query("Configuracao") String Configuracao, @Query("CNPJ") String CNPJ, @Query("pesquisa") String pesquisa, @Query("offset") Integer offset);
}
