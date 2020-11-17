package br.com.informsistemas.furafila.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.R;
import br.com.informsistemas.furafila.model.Balcao;
import br.com.informsistemas.furafila.model.BalcaoItem;
import br.com.informsistemas.furafila.model.BalcaoParcela;
import br.com.informsistemas.furafila.model.ModoPagamento;
import br.com.informsistemas.furafila.model.PreVendaArgos;
import br.com.informsistemas.furafila.model.PreVendaBalcao;
import br.com.informsistemas.furafila.dao.ModoPagamentoDAO;
import br.com.informsistemas.furafila.models.dao.MaterialDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoItemDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoParcelaDAO;
import br.com.informsistemas.furafila.models.dao.ParceiroDAO;
import br.com.informsistemas.furafila.models.helper.Misc;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;
import br.com.informsistemas.furafila.models.pojo.MovimentoParcela;
import br.com.informsistemas.furafila.models.pojo.Parceiro;
import br.com.informsistemas.furafila.service.ResgateService;
import br.com.informsistemas.furafila.adapter.ResgateAdapter;
import br.com.informsistemas.furafila.controller.rest.RestManager;
import br.com.informsistemas.furafila.models.helper.Constants;
import br.com.informsistemas.furafila.model.Resgate;
import br.com.informsistemas.furafila.models.pojo.RestResponse;
import br.com.informsistemas.furafila.rest.request.ResgateRequest;
import br.com.informsistemas.furafila.tasks.PreVendaTask;
import br.com.informsistemas.furafila.viewholder.ResgateViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResgateFragment extends Fragment implements ResgateViewHolder.OnResgateListener {

    private ProgressBar progressBar;
    private static final String TAG = "ResgateFragment";
    private RecyclerView recyclerView;
    private ResgateAdapter resgateAdapter;
    private ResgateService resgateService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        progressBar = view.findViewById(R.id.main_progress);
        resgateAdapter = new ResgateAdapter(getActivity(), this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(resgateAdapter);

        resgateService = new RestManager().getResgateService();

        carregarPagina("", "", null);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_lista, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.action_search_list:
                onShowResgateModal();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResgateClick(int position) {
        PreVendaTask preVendaTask = new PreVendaTask(this, resgateAdapter.getItem(position).ordemmovimento, resgateAdapter.getItem(position).ordembalcao);
        preVendaTask.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String parceiro = data.getStringExtra("parceiro");
        String documento = data.getStringExtra("documento");
        resgateAdapter.clear();
        carregarPagina(parceiro, documento, null);
    }

    private void onShowResgateModal(){
        DialogFragment fragmentModal = ResgateModalFragment.newInstance();
        fragmentModal.setTargetFragment(this, 1);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentModal.show(ft, "resgateModalFragment");
    }

    private void carregarPagina(String parceiro, String documento, Date data){
        progressBar.setVisibility(View.VISIBLE);

        callResgateConsultarApi(parceiro, documento, data).enqueue(new Callback<RestResponse<Resgate>>() {
            @Override
            public void onResponse(Call<RestResponse<Resgate>> call, Response<RestResponse<Resgate>> response) {
                // Got data. Send it to adapter

                RestResponse<Resgate> list = response.body();
                progressBar.setVisibility(View.GONE);
                resgateAdapter.addAll(list.data);
            }

            @Override
            public void onFailure(Call<RestResponse<Resgate>> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });
    }

    private Call<RestResponse<Resgate>> callResgateConsultarApi(String parceiro, String documento, Date data) {
        ResgateRequest resgateRequest = new ResgateRequest(Constants.DTO.registro.codigoconfiguracao,
                Constants.DTO.registro.codigofilialcontabil, Constants.DTO.registro.cnpj, parceiro, documento, data, Constants.DTO.registro.balcao);

        return resgateService.postConsultar(resgateRequest);
    }

    private void onSalvarParceiro(String codigoparceiro, String descricao, String cpfcgc){
        Parceiro parceiro = ParceiroDAO.getInstance(getActivity()).findByIdAuxiliar("cpfcgc", cpfcgc);

        if (parceiro == null){
            parceiro = new Parceiro(codigoparceiro, descricao, cpfcgc, "", "A",
                    "", "", "", "", "", "", "",
                    new Date(), new Date(), new Date(), Float.valueOf("0"), Float.valueOf("0"),
                    Float.valueOf("0"), "", "", "", "", "",
                    "", "", new Date(), "", "", "",
                    "", "", Float.valueOf("0"), "", "");
        }else{
            parceiro.descricao = descricao;
            parceiro.codigoparceiro = codigoparceiro;
        }

        ParceiroDAO.getInstance(getActivity()).createOrUpdate(parceiro);

    }

    private void onSalvarMaterial(String codigomaterial, String descricao, String unidadesaida, Float precovenda1){
        Material material = MaterialDAO.getInstance(getActivity()).findByIdAuxiliar("codigomaterial", codigomaterial);

        if (material == null){
            material = new Material(codigomaterial, "", descricao, unidadesaida, precovenda1, "",
                    "", Float.parseFloat("0"), "0", new Date(), "", Float.parseFloat("0"),
                    "", Float.parseFloat("0"), "", "", Float.parseFloat("0"), Float.parseFloat("0"));
        }else{
            material.descricao = descricao;
            material.unidadesaida = unidadesaida;
            material.precovenda1 = precovenda1;
        }

        MaterialDAO.getInstance(getActivity()).createOrUpdate(material);

    }

    private void onSalvarForma(String codigotipoevento, String descricaotipoevento, Float valor){
        ModoPagamento modoPagamento = ModoPagamentoDAO.getInstance(getActivity()).findByIdModoResgate("codigotipoevento", codigotipoevento);

        if (modoPagamento == null){
            modoPagamento = new ModoPagamento(codigotipoevento, descricaotipoevento, "F", new Date(), Float.parseFloat("0"), Float.parseFloat("0"));
        }else{
            modoPagamento.descricao = descricaotipoevento;
        }

        ModoPagamentoDAO.getInstance(getActivity()).createOrUpdate(modoPagamento);
    }

    public void onResgateArgos(PreVendaArgos preVendaArgos){

    }

    public void onResgateBalcao(PreVendaBalcao preVendaBalcao){
        Balcao balcao = preVendaBalcao.balcao;
        List<BalcaoItem> balcaoItemList = preVendaBalcao.balcaoitem;
        List<BalcaoParcela> balcaoParcelaList = preVendaBalcao.balcaoparcela;

        onSalvarParceiro(balcao.codigoparceiro, balcao.descricaoparceiro, balcao.cpfparceiro);

        Movimento movimento = new Movimento(Constants.DTO.registro.codigoempresa, Constants.DTO.registro.codigofilialcontabil,
                Constants.DTO.registro.codigoalmoxarifado, Constants.DTO.registro.codigooperacao,
                Constants.DTO.registro.codigotabelapreco, balcao.codigoparceiro, "", balcao.totalliquido.floatValue(),
                "", Misc.GetDateAtual(), Misc.GetDateAtual(), null, null, "", "", Misc.gerarMD5(),
                null, "T", 0);


        MovimentoDAO.getInstance(getActivity()).createOrUpdate(movimento);

        List<MovimentoItem> movimentoItemList = new ArrayList<>();

        for (int i = 0; i < balcaoItemList.size(); i++) {

            onSalvarMaterial(balcaoItemList.get(i).codigomaterial, balcaoItemList.get(i).descricao, balcaoItemList.get(i).unidade, balcaoItemList.get(i).custo.floatValue());

            MovimentoItem movimentoItem = new MovimentoItem(movimento, Constants.DTO.registro.codigotabelapreco,
                    balcaoItemList.get(i).codigomaterial, balcaoItemList.get(i).unidade, balcaoItemList.get(i).quantidade.floatValue(),
                    balcaoItemList.get(i).custo.floatValue(), balcaoItemList.get(i).custo.floatValue() * balcaoItemList.get(i).quantidade.floatValue(),
                    Float.parseFloat("0"), Float.parseFloat("0"), Float.parseFloat("0"), Float.parseFloat("0"), Float.parseFloat("0"),
                    Float.parseFloat("0"), Float.parseFloat("0"), Float.parseFloat("0"), Float.parseFloat("0"), Float.parseFloat("0"),
                    Float.parseFloat("0"), Float.parseFloat("0"), Float.parseFloat("0"), Float.parseFloat("0"), balcaoItemList.get(i).totalliquido.floatValue());

            MovimentoItemDAO.getInstance(getActivity()).createOrUpdate(movimentoItem);

            movimentoItemList.add(movimentoItem);
        }

        List<MovimentoParcela> movimentoParcelaList = new ArrayList<>();

        for (int i = 0; i < balcaoParcelaList.size(); i++) {

            onSalvarForma(balcaoParcelaList.get(i).codigotipoevento, balcaoParcelaList.get(i).descricaotipoevento,
                    balcaoParcelaList.get(i).valor.floatValue());

            MovimentoParcela movimentoParcela = new MovimentoParcela(movimento,
                    balcaoParcelaList.get(i).codigoformapagamento, balcaoParcelaList.get(i).codigotipoevento,
                    balcaoParcelaList.get(i).descricaotipoevento, balcaoParcelaList.get(i).valor.floatValue(), Float.valueOf("0"));

            MovimentoParcelaDAO.getInstance(getActivity()).createOrUpdate(movimentoParcela);

            movimentoParcelaList.add(movimentoParcela);
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("movimento", movimento);
        intent.putExtras(bundle);

        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
