package br.com.informsistemas.furafila.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.fragments.AcessoFragment;
import br.com.informsistemas.furafila.controller.fragments.LoginFragment;
import br.com.informsistemas.furafila.models.helper.Constants;

public class LoginActivity extends AppCompatActivity {

    private String token;
    private Fragment fragmentLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });

        String status = getIntent().getStringExtra("Status");

        if (status == null) {
            onShowLogin();
        }else{
            onShowAcesso();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            // Disable going back to the MainActivity
            moveTaskToBack(true);
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void onShowLogin(){
        fragmentLogin = getSupportFragmentManager().findFragmentByTag("loginFragment");

        if (fragmentLogin == null){
            fragmentLogin = new LoginFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragmentLogin, "loginFragment");
            ft.commit();
        }
    }

    private void onShowAcesso(){
        Fragment fragmentAcesso = getSupportFragmentManager().findFragmentByTag("acessoFragment");

        if (fragmentAcesso == null){
            fragmentAcesso = new AcessoFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragmentAcesso, "AcessoFragment");
            ft.commit();
        }
    }

    public String getToken(){
        return token;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSION_REQUESTCODE.READ_PHONE_STATE){
            Constants.PERMISSION.READ_PHONE_STATE = grantResults[0];
            ((LoginFragment) fragmentLogin).login();
        }
    }

}
