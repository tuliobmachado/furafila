package br.com.informsistemas.furafila.controller.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.controller.adapter.ParceiroAutoCompleteAdapter;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Parceiro;

public class RelatorioPedidoModalFragment extends DialogFragment {

    private Parceiro parceiroSelecionado;
    private Calendar myCalendar = Calendar.getInstance();
    private AutoCompleteTextView edtParceiro;
    private EditText edtDataInicio;
    private EditText edtDataFim;
    DatePickerDialog.OnDateSetListener dateInicio = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInicio();
        }
    };
    DatePickerDialog.OnDateSetListener dateFim = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateFim();
        }
    };

    public static RelatorioPedidoModalFragment newInstance(){
           RelatorioPedidoModalFragment frag = new RelatorioPedidoModalFragment();
           return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogDefault);
        builder.setTitle("Pesquisa");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_modal_relatorio_pedido, null);

        edtParceiro = view.findViewById(R.id.edtParceiro);
        edtParceiro.addTextChangedListener(onGetTextWatcher());
        edtParceiro.setAdapter(new ParceiroAutoCompleteAdapter(getActivity(), Constants.DTO.listPesquisaParceiro));
        edtParceiro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);

                if (item instanceof Parceiro){
                    parceiroSelecionado = (Parceiro) item;
                }
            }
        });

        edtDataInicio = view.findViewById(R.id.edtDataInicio);
        edtDataInicio.setOnClickListener(onGetClickListener(dateInicio));

        edtDataFim = view.findViewById(R.id.edtDataFim);
        edtDataFim.setOnClickListener(onGetClickListener(dateFim));
        builder.setView(view);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("parceiro", parceiroSelecionado);
                bundle.putString("dataInicio", edtDataInicio.getText().toString());
                bundle.putString("dataFim", edtDataFim.getText().toString());
                intent.putExtras(bundle);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            }
        });

        return builder.create();
    }

    private View.OnClickListener onGetClickListener(final DatePickerDialog.OnDateSetListener date){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), R.style.DialogDatePicker, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        };
    }

    private TextWatcher onGetTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void updateDateInicio(){
        edtDataInicio.setText(Misc.formatDate(myCalendar.getTime(), "dd/MM/yyyy"));
    }

    private void updateDateFim(){
        edtDataFim.setText(Misc.formatDate(myCalendar.getTime(), "dd/MM/yyyy"));
    }
}
