package br.com.informsistemas.furafila.controller.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.LoginActivity;
import br.com.informsistemas.furafila.controller.rest.RestManager;
import br.com.informsistemas.furafila.models.callback.DeviceService;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.Registro;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.models.utils.DialogClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcessoFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private LoginActivity loginActivity;
    private int delay = 30000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {

        public void run() {
            getAcesso();
            handler.postDelayed(this, delay);
        }
    };

    @Override
    public void onResume() {
        handler.postDelayed(runnable, delay);
        super.onResume();
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            loginActivity = (LoginActivity) context;
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acesso, container, false);

        TextView txtImei = view.findViewById(R.id.txt_imei);
        txtImei.setText(Constants.DTO.registro.imei);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        final int delay = 30000;
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getAcesso();
//                handler.postDelayed(this, delay);
//            }
//        }, delay);
    }

    public void onAcessoSuccess(Intent data) {
        Log.d(TAG, "AcessoSuccess");
        getActivity().setResult(loginActivity.RESULT_OK, data);
        getActivity().finish();
    }

    public void onAcessoFailed(String msg) {
        if (msg != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        }
    }

    private void getAcesso(){
        DeviceService deviceService = new RestManager().getDeviceService();
        Call<RestResponse<Registro>> requestDevice = deviceService.postConsultar(Constants.DTO.registro);

        final ProgressDialog progressDialog = DialogClass.showDialog(getActivity(), "Realizando Consulta...");

        requestDevice.enqueue(new Callback<RestResponse<Registro>>() {
            @Override
            public void onResponse(Call<RestResponse<Registro>> call, Response<RestResponse<Registro>> response) {
                RestResponse<Registro> restResponse = null;

                if (response.errorBody() != null){
                    restResponse = new Gson().fromJson(response.errorBody().charStream(), RestResponse.class);
                }else{
                    restResponse = response.body();
                }

                if (restResponse.meta.status.equals("OK")){
                    onAcessoSuccess(getIntent(restResponse.data.get(0).status));
                }else{
                    onAcessoFailed(restResponse.meta.message);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<RestResponse<Registro>> call, Throwable t) {
                Log.e(TAG, "[ERRO]: "+ t.getMessage());
                onAcessoFailed(t.getMessage());

                progressDialog.dismiss();
            }
        });
    }

    public Intent getIntent(String status){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        Constants.DTO.registro.status = status;

        bundle.putSerializable("Registro", Constants.DTO.registro);
        intent.putExtras(bundle);

        return intent;
    }
}
