package br.com.informsistemas.furafila.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.activity.LoginActivity;
import br.com.informsistemas.furafila.controller.rest.RestManager;
import br.com.informsistemas.furafila.models.callback.RegistroService;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Enums;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.model.Registro;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.models.utils.DialogClass;
import br.com.informsistemas.furafila.models.utils.Mask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private LoginActivity loginActivity;
    private String imei;
    private EditText edtCNPJ;
    private EditText edtUsuario;
    private EditText edtSenha;
    private Button btnRegistrar;
    private Registro registro;

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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        configViews(view);

        return view;
    }

    public void login(){
        if (!validate()) {
            onLoginFailed(null);
            return;
        }

        btnRegistrar.setEnabled(false);

        getImei();

        String cnpj = edtCNPJ.getText().toString();
        cnpj = cnpj.replaceAll("[^0-9]", "");

        registro = new Registro(edtUsuario.getText().toString(), edtSenha.getText().toString(),
                imei, cnpj, loginActivity.getToken(), "", "", "", "",
                "", "", "", "", "",
                "", "", false, false, false, false,
                Enums.TIPO_APLICACAO.NFCE.getString(), "", "", "");

        getRegistro();
    }

    public void onLoginSuccess(Intent data) {
        Log.d(TAG, "LoginSuccess");
        btnRegistrar.setEnabled(true);
        getActivity().setResult(loginActivity.RESULT_OK, data);
        getActivity().finish();
    }

    public void onLoginFailed(String msg) {
        if (msg != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        }

        btnRegistrar.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String usuario = edtUsuario.getText().toString();
        String password = edtSenha.getText().toString();
        String cnpj = edtCNPJ.getText().toString();

        if (cnpj.isEmpty()){
            edtCNPJ.setError("Informe um CNPJ!");
            valid = false;
        }else{
            edtCNPJ.setError(null);
        }

        if (usuario.isEmpty()) {
            edtUsuario.setError("Informe um usuário válido!");
            valid = false;
        } else {
            edtUsuario.setError(null);
        }

        if (password.isEmpty()) {
            edtSenha.setError("Informe uma senha!");
            valid = false;
        } else {
            edtSenha.setError(null);
        }

        return valid;
    }

    private void getRegistro(){
        RegistroService registroService = new RestManager().getRegistroService();
        Call<RestResponse<Registro>> requestRegistro = registroService.postRegistro(registro);

        final ProgressDialog progressDialog = DialogClass.showDialog(getActivity(), "Realizando Autenticação...");

        requestRegistro.enqueue(new Callback<RestResponse<Registro>>() {
            @Override
            public void onResponse(Call<RestResponse<Registro>> call, Response<RestResponse<Registro>> response) {
                RestResponse<Registro> restResponse = null;
                String erro = "";

                if (response.errorBody() != null){
                    try {
                        erro = response.errorBody().string();

                        restResponse = new Gson().fromJson(response.errorBody().charStream(), RestResponse.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        onLoginFailed(erro);
                    }

                }else{
                    restResponse = response.body();
                }

                if (restResponse.meta.status.equals("OK")){
                    onLoginSuccess(getIntent(restResponse.data.get(0)));
                }else{
                    onLoginFailed(restResponse.meta.message);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<RestResponse<Registro>> call, Throwable t) {
                Log.e(TAG, "[ERRO]: "+ t.getMessage());
                onLoginFailed(t.getMessage());

                progressDialog.dismiss();
            }
        });
    }

    public Intent getIntent(Registro responseRegistro){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        String usuario = registro.usuario;
        String senha = registro.senha;
        String cnpj = registro.cnpj;
        String imei = registro.imei;
        String token = registro.token;

        registro = responseRegistro;
        registro.usuario = usuario;
        registro.senha = senha;
        registro.cnpj = cnpj;
        registro.imei = imei;
        registro.token = token;

        bundle.putSerializable("Registro", registro);
        intent.putExtras(bundle);

        return intent;
    }

    private void configViews(View view){
        edtCNPJ = view.findViewById(R.id.edtCNPJ);
        edtUsuario = view.findViewById(R.id.edtUsuario);
        edtSenha = view.findViewById(R.id.edtSenha);
        TextWatcher maskCNPJ = Mask.insert("##.###.###/####-##", edtCNPJ);
        edtCNPJ.removeTextChangedListener(maskCNPJ);
        edtCNPJ.addTextChangedListener(maskCNPJ);

        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.PERMISSION.READ_PHONE_STATE == PackageManager.PERMISSION_DENIED){
                    Misc.SolicitaPermissao(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.PERMISSION_REQUESTCODE.READ_PHONE_STATE);
                }else {
                    login();
                }
            }
        });
    }

    public void getImei(){
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = tm.getImei();
        } else {
            imei = tm.getDeviceId();
        }
    }
}
