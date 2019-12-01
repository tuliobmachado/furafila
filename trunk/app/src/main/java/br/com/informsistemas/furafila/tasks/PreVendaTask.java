package br.com.informsistemas.furafila.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.rest.RestManager;
import br.com.informsistemas.furafila.fragments.ResgateFragment;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.models.utils.DialogClass;
import br.com.informsistemas.furafila.rest.response.ResgateResponse;
import br.com.informsistemas.furafila.service.ResgateService;
import retrofit2.Call;
import retrofit2.Response;

public class PreVendaTask extends AsyncTask<String, Void, List<ResgateResponse>> {

    private Fragment fragment;
    private ProgressDialog dialog;
    private String ordemMovimento;
    private Integer ordemBalcao;

    public PreVendaTask(Fragment f, String ordemMovimento, Integer ordemBalcao) {
        this.fragment = f;
        this.ordemMovimento = ordemMovimento;
        this.ordemBalcao = ordemBalcao;

        dialog = new ProgressDialog(f.getActivity(), R.style.DialogDefault);
        dialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Resgatando dados...");
        dialog.show();
    }

    @Override
    protected List<ResgateResponse> doInBackground(String... strings) {
        ResgateService resgateService = new RestManager().getResgateService();

        Call<RestResponse<ResgateResponse>> resgateRequest = resgateService.getPreVenda(
                "'"+ Constants.DTO.registro.codigoconfiguracao+"'", "'"+ordemMovimento+"'", ordemBalcao);

        try {
            RestResponse<ResgateResponse> restResponse = null;
            Response<RestResponse<ResgateResponse>> response = resgateRequest.execute();

            if (response.errorBody() != null) {
                restResponse = new Gson().fromJson(response.errorBody().charStream(), RestResponse.class);
            } else {
                String responseString = response.body().toString();
                restResponse = response.body();
            }

            if (restResponse.meta.status.equals("OK")) {
                return restResponse.data;
            } else {
                DialogClass.showToastFragment(fragment, restResponse.meta.message);
            }
        } catch (IOException ex) {
            DialogClass.showToastFragment(fragment, ex.getMessage());
            dialog.dismiss();
        }

        return null;
    }

    @Override
    protected void onPostExecute(final List<ResgateResponse> resgateRequest) {
        if (resgateRequest != null){
            if (resgateRequest.get(0).prevendaargos != null){
                ((ResgateFragment) fragment).onResgateArgos(resgateRequest.get(0).prevendaargos);
            }else
                dialog.dismiss();
                ((ResgateFragment) fragment).onResgateBalcao(resgateRequest.get(0).prevendabalcao);
        }else{
            dialog.dismiss();
        }
    }

}
