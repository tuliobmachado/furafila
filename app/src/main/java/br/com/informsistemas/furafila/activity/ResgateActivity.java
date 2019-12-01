package br.com.informsistemas.furafila.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.fragments.ResgateFragment;
import br.com.informsistemas.furafila.models.utils.IOnBackPressed;

public class ResgateActivity extends AppCompatActivity {

    private ResgateFragment resgateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Resgate");

        onShowResgate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Back button
            case R.id.action_search_list:

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("resgateFragment");

        if (count == 0) {
            super.onBackPressed();
            setResult(Activity.RESULT_OK, new Intent());
            //additional code
        } else {
            if ((fragment instanceof IOnBackPressed)){
                ((IOnBackPressed) fragment).onBackPressed();
            }

            getSupportFragmentManager().popBackStack();

        }
    }

    private void onShowResgate(){
        resgateFragment = (ResgateFragment) getSupportFragmentManager().findFragmentByTag("resgateFragment");

        if (resgateFragment == null) {
            resgateFragment = new ResgateFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, resgateFragment, "resgateFragment");
            ft.commit();
        }
    }
}
