package br.com.informsistemas.furafila.models.callback;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.Categoria;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CategoriaService {

    @GET(Constants.URL.CATEGORIA_LISTAR)
    Call<RestResponse<Categoria>> getListar(@Query("Configuracao") String Configuracao, @Query("CNPJ") String CNPJ, @Query("pesquisa") String pesquisa, @Query("offset") Integer offset);

}
