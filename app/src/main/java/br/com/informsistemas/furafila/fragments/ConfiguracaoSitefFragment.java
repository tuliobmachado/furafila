package br.com.informsistemas.furafila.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.activity.MainActivity;
import br.com.informsistemas.furafila.dao.ConfiguracaoSitefDAO;
import br.com.informsistemas.furafila.model.ConfiguracaoSitef;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;

public class ConfiguracaoSitefFragment extends Fragment {

    private static final String TAG = "ConfiguracaoSitefFragment";
    private ConfiguracaoSitef configuracaoSitef;
    private EditText edtIP;
    private EditText edtEmpresa;
    private EditText edtTerminal;
    private Button btnSalvar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracao_sitef, container, false);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab_adicionar_pedido);
        fab.setVisibility(View.INVISIBLE);

        getActivity().setTitle("Configuração Sitef");

        configViews(view);

        configuracaoSitef = ConfiguracaoSitefDAO.getInstance(getActivity()).findFirst();

        if (configuracaoSitef != null){
            edtIP.setText(configuracaoSitef.sitefip);
            edtEmpresa.setText(configuracaoSitef.storeid);
            edtTerminal.setText(configuracaoSitef.terminalid);
        }

        return view;
    }

    private void configViews(View view){
        edtIP = view.findViewById(R.id.edt_IP);
        edtEmpresa = view.findViewById(R.id.edt_Empresa);
        edtTerminal = view.findViewById(R.id.edt_Terminal);
        btnSalvar = view.findViewById(R.id.btn_salvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtIP.getText().toString().equals("")){
                    Misc.alerta(getActivity(), "Necessário informar o IP");
                    return;
                }

                if (edtEmpresa.getText().toString().equals("")){
                    Misc.alerta(getActivity(), "Necessário informar a Código da Empresa");
                    return;
                }

                if (edtTerminal.getText().toString().equals("")){
                    Misc.alerta(getActivity(), "Necessário informar o Terminal");
                    return;
                }

                if (configuracaoSitef == null){
                    configuracaoSitef = new ConfiguracaoSitef(edtIP.getText().toString(), edtEmpresa.getText().toString(), edtTerminal.getText().toString());
                }else{
                    configuracaoSitef.sitefip = edtIP.getText().toString();
                    configuracaoSitef.storeid = edtEmpresa.getText().toString();
                    configuracaoSitef.terminalid = edtTerminal.getText().toString();
                }

                ConfiguracaoSitefDAO.getInstance(getActivity()).createOrUpdate(configuracaoSitef);

                Misc.alerta(getActivity(), "Configurações foram atualizadas!");

                Intent i = new Intent();
                i.putExtra("sitefIP", configuracaoSitef.sitefip);
                i.putExtra("sitefEmpresa", configuracaoSitef.storeid);
                i.putExtra("sitefTerminal", configuracaoSitef.terminalid);

                getTargetFragment().onActivityResult(Constants.MAIN_REQUEST_CODE.CONFIGURACAO_SITEF,  Activity.RESULT_OK, i);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
