package br.com.informsistemas.furafila.models.callback;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.model.Registro;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistroService {

    @POST(Constants.URL.LOGIN_REGISTRAR)
    Call<RestResponse<Registro>> postRegistro(@Body Registro registro);
}
