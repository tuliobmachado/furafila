package br.com.informsistemas.furafila.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.com.informsistemas.furafila.R;
import br.com.softwareexpress.sitef.android.CliSiTef;
import br.com.softwareexpress.sitef.android.ICliSiTefListener;

public class PagamentoTransactionActivity extends Activity implements ICliSiTefListener {
    private static final int CAMPO_COMPROVANTE_CLIENTE = 121;
    private static final int CAMPO_COMPROVANTE_ESTAB = 122;

    private class RequestCode {
        private static final int GET_DATA = 1;
        private static final int END_STAGE_1_MSG = 2;
        private static final int END_STAGE_2_MSG = 3;
    }

    // Variaveis estaticas para nao serem reinicializadas ao rodar o display
    // Para tanto, vamos assumir que esta atividade nunca será executada em
    // paralelo com outra igual (singleton)

    private int trnResultCode;
    private static String title;
    private static CliSiTef clisitef;
    private static PagamentoTransactionActivity instance = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.transaction);

        Button btn = (Button) findViewById(R.id.btCfgCancela);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (clisitef.abortTransaction(-1) != 0) {
                    // Se não há o que abortar, encerra a Activity
                    finish();
                }
            }
        });

        instance = this;
        clisitef = CliSiTef.getInstance();
        clisitef.setMessageHandler(hndMessage);
        trnResultCode = -1; // undefined
        title = "";
        setStatus("");
        clisitef.setActivity(this);
//        int modalidade = 0;
        int modalidade = 110;
        Float valorPago = getIntent().getExtras().getFloat("valorPago");
//        getIntent().getExtras().getInt("modalidade");
        // A definição do valor da transação depende da aplicação.
        // No caso deste exemplo , o valor é sorteado.
        String trnAmount = String.valueOf(Math.round(valorPago * 100));
//                "" + (100 + System.currentTimeMillis() % 100);
        clisitef.startTransaction(this, modalidade, trnAmount, "123456", "20120514", "120000", "Cashier1", "");
    }

    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    private void setStatus(String s) {
        ((TextView) findViewById(R.id.tvStatusTrn)).setText(s);
    }

    private void alert(String message) {
        Toast.makeText(PagamentoTransactionActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void onData(int stage, int command, int fieldId, int minLength, int maxLength, byte[] input) {
        String data = "";

        setProgressBarIndeterminateVisibility(false);

        if (stage == 1) {
            // Evento onData recebido em uma startTransaction
        } else if (stage == 2) {
            // Evento onData recebido em uma finishTransaction
        }

        switch (command) {
            case CliSiTef.CMD_RESULT_DATA:
                switch (fieldId) {
                    case CAMPO_COMPROVANTE_CLIENTE:
                    case CAMPO_COMPROVANTE_ESTAB:
                        alert(clisitef.getBuffer());
                }
                break;
            case CliSiTef.CMD_SHOW_MSG_CASHIER:
            case CliSiTef.CMD_SHOW_MSG_CUSTOMER:
            case CliSiTef.CMD_SHOW_MSG_CASHIER_CUSTOMER:
                setStatus(clisitef.getBuffer());
                break;
            case CliSiTef.CMD_SHOW_MENU_TITLE:
            case CliSiTef.CMD_SHOW_HEADER:
                title = clisitef.getBuffer();
                break;
            case CliSiTef.CMD_CLEAR_MSG_CASHIER:
            case CliSiTef.CMD_CLEAR_MSG_CUSTOMER:
            case CliSiTef.CMD_CLEAR_MSG_CASHIER_CUSTOMER:
            case CliSiTef.CMD_CLEAR_MENU_TITLE:
            case CliSiTef.CMD_CLEAR_HEADER:
                title = "";
                setStatus("");
                break;
            case CliSiTef.CMD_CONFIRM_GO_BACK:
            case CliSiTef.CMD_CONFIRMATION: {
                Intent i = new Intent(getApplicationContext(), YesNoActivity.class);
                i.putExtra("title", title);
                i.putExtra("message", clisitef.getBuffer());
                startActivityForResult(i, RequestCode.GET_DATA);
                return;
            }
            case CliSiTef.CMD_GET_FIELD_CURRENCY:
            case CliSiTef.CMD_GET_FIELD_BARCODE:
            case CliSiTef.CMD_GET_FIELD: {
                Intent i = new Intent(getApplicationContext(), DialogActivity.class);
                i.putExtra("title", title);
                i.putExtra("message", clisitef.getBuffer());
                startActivityForResult(i, RequestCode.GET_DATA);
                return;
            }
            case CliSiTef.CMD_GET_MENU_OPTION: {
                Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                i.putExtra("title", title);
                i.putExtra("message", clisitef.getBuffer());
                startActivityForResult(i, RequestCode.GET_DATA);
                return;
            }
            case CliSiTef.CMD_PRESS_ANY_KEY: {
                Intent i = new Intent(getApplicationContext(), MessageActivity.class);
                i.putExtra("message", clisitef.getBuffer());
                startActivityForResult(i, RequestCode.GET_DATA);
                return;
            }
            case CliSiTef.CMD_ABORT_REQUEST:
                break;
            default:
                break;
        }

        setProgressBarIndeterminateVisibility(true);
        clisitef.continueTransaction(data);
    }

    private String getMessageDescription(int stage, int sts) {
        switch (sts) {
            case -1:
                return getString(R.string.msgModuloNaoConfigurado);
            case -2:
                return getString(R.string.msgCanceladoOperador);
            case -3:
                return getString(R.string.msgFuncaoInvalida);
            case -4:
                return getString(R.string.msgFaltaMemoria);
            case -5:
                return getString(R.string.msgFalhaComunicacao);
            case -6:
                return getString(R.string.msgCanceladoPortador);
            case -40:
                return getString(R.string.msgNegadaSiTef);
            case -43:
                return getString(R.string.msgErroPinPad);
            case -100:
                return getString(R.string.msgOutrosErros);
            default:
                return "Stage " + stage + getString(R.string.msgReturned) + " " + sts;
        }
    }

    public void onTransactionResult(int stage, int resultCode) {
        setProgressBarIndeterminateVisibility(false);
        trnResultCode = resultCode;
        //alert ("Fim do estágio " + stage + ", retorno " + resultCode);
        if (stage == 1 && resultCode == 0) { // Confirm the transaction
            try {
                clisitef.finishTransaction(1);
            } catch (Exception e) {
                //alert(e.getMessage());
            }
        } else {

            if (resultCode == 0) {
                finish();
            } else {
                Intent i = new Intent(getApplicationContext(), MessageActivity.class);
                i.putExtra("message", getMessageDescription(stage, resultCode));
                startActivityForResult(i, stage == 1 ? RequestCode.END_STAGE_1_MSG : RequestCode.END_STAGE_2_MSG);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.GET_DATA) {
            if (resultCode == RESULT_OK) {
                String in = "";
                if (data.getExtras() != null) {
                    in = data.getExtras().getString("input");
                }
                clisitef.continueTransaction(in);
            } else if (resultCode == RESULT_CANCELED) {
                clisitef.abortTransaction(-1);
            }
        } else if (requestCode == RequestCode.END_STAGE_1_MSG && trnResultCode != 0) {
            finish();
        } else if (requestCode == RequestCode.END_STAGE_2_MSG) {
            finish();
        }
    }

    private void setMessageTitle(int what) {
        setTitle(getString(what));
    }

    private static Handler hndMessage = new Handler() {
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case CliSiTef.EVT_BEGIN_PP_CONNECT:
                    instance.setProgressBarIndeterminateVisibility(true);
                    instance.setMessageTitle(R.string.msgPPSearching);
                    break;
                case CliSiTef.EVT_END_PP_CONNECT:
                    instance.setProgressBarIndeterminateVisibility(false);
                    instance.setTitle(R.string.app_name);
                    break;
                case CliSiTef.EVT_BEGIN_PP_CONFIG:
                    instance.setProgressBarIndeterminateVisibility(true);
                    instance.setMessageTitle(R.string.msgPPConfiguring);
                    break;
                case CliSiTef.EVT_END_PP_CONFIG:
                    instance.setProgressBarIndeterminateVisibility(false);
                    instance.setMessageTitle(R.string.msgPPConfigured);
                    break;
                case CliSiTef.EVT_BT_PP_DISCONNECT:
                    instance.setProgressBarIndeterminateVisibility(false);
                    instance.setMessageTitle(R.string.msgPPDisconnected);
                    break;
            }
        }
    };

}
