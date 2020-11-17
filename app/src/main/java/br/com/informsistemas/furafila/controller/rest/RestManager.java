package br.com.informsistemas.furafila.controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import br.com.informsistemas.furafila.service.ResgateService;
import br.com.informsistemas.furafila.models.callback.CategoriaService;
import br.com.informsistemas.furafila.models.callback.DeviceService;
import br.com.informsistemas.furafila.models.callback.MaterialService;
import br.com.informsistemas.furafila.models.callback.ParceiroService;
import br.com.informsistemas.furafila.models.callback.PedidoService;
import br.com.informsistemas.furafila.models.callback.RegistroService;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.utils.DateDeserializer;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestManager {

    private long timeoutDefault = 120;
    private Boolean excludeExposeDefault = false;
    private RegistroService registroService;
    private DeviceService deviceService;
    private PedidoService pedidoService;
    private ParceiroService parceiroService;
    private MaterialService materialService;
    private CategoriaService categoriaService;
    private ResgateService resgateService;

    public RegistroService getRegistroService(){
        if (registroService == null){
            Retrofit retrofit = getRetrofit(timeoutDefault, excludeExposeDefault);

            registroService = retrofit.create(RegistroService.class);
        }

        return registroService;
    }

    public DeviceService getDeviceService(){
        if (deviceService == null){
            Retrofit retrofit = getRetrofit(timeoutDefault, excludeExposeDefault);

            deviceService = retrofit.create(DeviceService.class);
        }

        return deviceService;
    }

    public PedidoService getPedidoService(){
        if (pedidoService == null){
            Retrofit retrofit = getRetrofit(300, true);

            pedidoService = retrofit.create(PedidoService.class);
        }

        return pedidoService;
    }

    public ParceiroService getParceiroService(){
        if (parceiroService == null){
            Retrofit retrofit = getRetrofit(120, excludeExposeDefault);
            parceiroService = retrofit.create(ParceiroService.class);
        }

        return parceiroService;
    }

    public MaterialService getMaterialService(){
        if (materialService == null){
            Retrofit retrofit = getRetrofit(120, excludeExposeDefault);
            materialService = retrofit.create(MaterialService.class);
        }

        return materialService;
    }

    public CategoriaService getCategoriaService(){
        if (categoriaService == null){
            Retrofit retrofit = getRetrofit(120, excludeExposeDefault);
            categoriaService = retrofit.create(CategoriaService.class);
        }

        return categoriaService;
    }

    public ResgateService getResgateService(){
        if (resgateService == null){
            Retrofit retrofit = getRetrofit(120, excludeExposeDefault);
            resgateService = retrofit.create(ResgateService.class);
        }

        return resgateService;
    }

    private OkHttpClient getHttpClient(long timeout){
        return new OkHttpClient()
                .newBuilder()
                .addInterceptor(getInterceptor())
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
    }

    private Retrofit getRetrofit(long timeout, Boolean excludeExpose){
        Gson gson = null;

        if (excludeExpose){
            gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").registerTypeAdapter(Date.class, new DateDeserializer()).create();
        }else {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").registerTypeAdapter(Date.class, new DateDeserializer()).create();
        }

        return new Retrofit.Builder()
                .baseUrl(Constants.URL.BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(getHttpClient(timeout))
                .build();
    }

    private Retrofit getRetrofitMock(long timeout, Boolean excludeExpose){
        Gson gson = null;

        if (excludeExpose){
            gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").registerTypeAdapter(Date.class, new DateDeserializer()).create();
        }else {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").registerTypeAdapter(Date.class, new DateDeserializer()).create();
        }

        return new Retrofit.Builder()
                .baseUrl(Constants.URL.BASE_MOCK)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(getHttpClient(timeout))
                .build();
    }

    private Interceptor getInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                Response modified = response.newBuilder()
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .build();

                return modified;
            }
        };
    }

}
