package br.com.informsistemas.furafila.models.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import br.com.informsistemas.furafila.R;

public class DialogClass {

    public static ProgressDialog showDialog(Context c, String message){
        ProgressDialog progressDialog = new ProgressDialog(c, R.style.DialogDefault);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();

        return progressDialog;
    }

    public static void dialogDismiss(ProgressDialog dialog){
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void showToastFragment(final Fragment fragment, final String msg){
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(fragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
