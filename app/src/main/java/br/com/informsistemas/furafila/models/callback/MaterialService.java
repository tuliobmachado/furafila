package br.com.informsistemas.furafila.models.callback;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MaterialService {

    @GET(Constants.URL.MATERIAL_LISTAR)
    Call<RestResponse<Material>> getListar(@Query("Configuracao") String Configuracao, @Query("CNPJ") String CNPJ, @Query("pesquisa") String pesquisa, @Query("categoria") String categoria, @Query("offset") Integer offset);
}
