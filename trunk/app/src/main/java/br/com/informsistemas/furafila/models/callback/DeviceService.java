package br.com.informsistemas.furafila.models.callback;

import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.Registro;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeviceService {

    @POST(Constants.URL.DEVICE_CONSULTAR)
    Call<RestResponse<Registro>> postConsultar(@Body Registro registro);

}
