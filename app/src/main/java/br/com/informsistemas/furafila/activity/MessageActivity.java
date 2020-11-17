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

public class MessageActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.message);
    TextView tv = (TextView) findViewById(R.id.tvTituloMsg);
    tv.setText(getIntent().getExtras().getString("message"));
    Button btn = (Button) findViewById(R.id.btDlgOk);
    btn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
      }
    });
  }
}
