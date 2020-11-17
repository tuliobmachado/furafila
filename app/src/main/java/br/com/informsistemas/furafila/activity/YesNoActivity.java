package br.com.informsistemas.furafila.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import br.com.informsistemas.furafila.R;

/**
 * Controller da tela de Confirma/Cancela
 */
public class YesNoActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.yes_no);

    TextView tv = (TextView) findViewById(R.id.tvSimNaoMsg);
    tv.setText(getIntent().getExtras().getString("message"));

    Button btn = (Button) findViewById(R.id.btSNSim);
    btn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent();
        i.putExtra("input", "0");
        setResult(RESULT_OK, i);
        finish();
      }
    });

    btn = (Button) findViewById(R.id.btSNNao);
    btn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent();
        i.putExtra("input", "1");
        setResult(RESULT_OK, i);
        finish();
      }
    });

    btn = (Button) findViewById(R.id.btSNCancelar);
    btn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
      }
    });
  }
}
