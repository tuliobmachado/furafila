package br.com.informsistemas.furafila.models.helper;

import android.content.Context;

import java.util.List;

import br.com.informsistemas.furafila.models.dao.MaterialEstadoDAO;
import br.com.informsistemas.furafila.models.dao.MovimentoDAO;
import br.com.informsistemas.furafila.models.dao.TabelaPrecoItemDAO;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.pojo.MaterialEstado;
import br.com.informsistemas.furafila.models.pojo.Movimento;
import br.com.informsistemas.furafila.models.pojo.MovimentoItem;
import br.com.informsistemas.furafila.models.pojo.TabelaPrecoItem;

public class CalculoClass {

    private Context context;
    private Material material;

    public CalculoClass(Context context, Material m) {
        this.context = context;
        this.material = m;
    }

    public void setPrecoVenda() {
        CalculaPrecoVenda();
    }

    public void setTributos() {
        CalculaTributos();
    }

    public void setTotal() {
        CalculaPrecoVenda();
        CalculaTributos();
    }

    public float getPrecoVenda() {
        return material.precovenda1;
    }

    public float getTotalLiquido(){
        return material.totalliquido;
    }

    private void CalculaTributos() {
        MaterialEstado materialEstado = MaterialEstadoDAO.getInstance(context).getTributacoes(Constants.MOVIMENTO.estadoParceiro, material.codigomaterial);

        material.margemsubstituicao = materialEstado.mva;
        material.pautafiscal = materialEstado.pautafiscal;
        CalculaIPI();
        CalculaICMS(materialEstado);
        CalculaFecoep(materialEstado);
        CalculaTotalLiquido();
    }

    private void CalculaIPI() {
        material.ipi = material.percipi;

        if (material.ipi > 0) {
            material.valoripi = Misc.fRound(true, material.precovenda1 * (material.percipi / 100), 2);
        }

        if (material.valoripi < 0) {
            material.valoripi = 0;
        }
    }

    private void CalculaFecoep(MaterialEstado materialEstado){
        material.icmsfecoep = materialEstado.fecoep;
        material.icmsfecoepst = materialEstado.fecoep;

        if (material.icmsfecoep > 0) {
            if (material.cst_csosn.equals("00") || material.cst_csosn.equals("10") ||
                    material.cst_csosn.equals("20") || material.cst_csosn.equals("51") ||
                    material.cst_csosn.equals("70") || material.cst_csosn.equals("90")){
                material.valoricmsfecoep = Misc.fRound(false, (material.baseicms * ( material.icmsfecoep / 100 )), 2);
            }else{
                material.valoricmsfecoep = 0;
            }
        }

        if (material.icmsfecoepst > 0) {
            if (material.cst_csosn.equals("10") || material.cst_csosn.equals("30") ||
                    material.cst_csosn.equals("70") || material.cst_csosn.equals("90") ||
                    material.cst_csosn.equals("201") || material.cst_csosn.equals("202") ||
                    material.cst_csosn.equals("203") || material.cst_csosn.equals("900"))    {
                material.valoricmsfecoepst = Misc.fRound(false, (material.baseicmssubst * ( material.icmsfecoepst / 100 ) - material.valoricmsfecoep), 2);
            }else{
                material.valoricmsfecoepst = 0;
            }
        }
    }

    private void CalculaICMS(MaterialEstado materialEstado){
        if (Constants.MOVIMENTO.estadoParceiro.equals(Constants.DTO.registro.estado)) {
            material.icms = materialEstado.icms_interno;
            material.icmssubst = materialEstado.icms_interno;
        } else {
            material.icms = materialEstado.icms_interestadual;
            material.icmssubst = materialEstado.icms_interno;
        }

        if (material.icms > 0) {
            material.valoricms = Misc.fRound(true,material.precovenda1 * (material.icms / 100), 2);
        }

        if (material.valoricms < 0){
            material.valoricms = 0;
        }

        material.baseicms = (material.precovenda1);

        if ((Constants.DTO.registro.utilizapauta) && (material.pautafiscal > 0)) {

            if (material.quantidade == 0){
                material.baseicmssubst = material.pautafiscal;
            }else {
                material.baseicmssubst = (material.pautafiscal * material.quantidade);
            }

            if (Constants.DTO.registro.utilizafatorpauta){
                material.baseicmssubst = material.baseicmssubst * material.fator;
            }

            material.margemsubstituicao = 0;
        }else {
            material.baseicmssubst = (material.baseicms + material.valoripi);
            material.baseicmssubst = material.baseicmssubst + (material.baseicmssubst * material.margemsubstituicao / 100);

            material.pautafiscal = 0;
        }

        material.valoricmssubst = Misc.fRound(false,(material.baseicmssubst * material.icmssubst), 2) / 100;
        material.valoricmssubst = (material.valoricmssubst - material.valoricms);

        if (material.valoricmssubst < 0){
            material.valoricmssubst = 0;
        }

        material.baseicmssubst = Misc.fRound(false, material.baseicmssubst, 2);
        material.valoricmssubst = Misc.fRound(false, material.valoricmssubst, 2);
    }

    private void CalculaTotalLiquido(){
        material.totalliquido = material.precovenda1 + material.valoricmsfecoepst + material.valoricmssubst + material.valoripi;
    }

    private void CalculaPrecoVenda() {
        TabelaPrecoItem tabelaPrecoItem = null;
        float precovenda1 = 0;

        if (!Constants.MOVIMENTO.codigotabelapreco.equals(Constants.DTO.registro.codigotabelapreco)) {
            tabelaPrecoItem = TabelaPrecoItemDAO.getInstance(context).getTabelaPrecoItem(Constants.MOVIMENTO.codigotabelapreco, material.codigotabelaprecoitem);

            if (tabelaPrecoItem != null) {
                precovenda1 = tabelaPrecoItem.precovenda1;
            } else {
                precovenda1 = material.precovenda1;
            }
        } else {
            precovenda1 = material.precovenda1;
        }

        material.custo = precovenda1;
        CalculaDesconto(tabelaPrecoItem);
    }

    private void CalculaDesconto(TabelaPrecoItem tbItem) {
        float value = 0;
        float percdesconto = 0;
        float valorDesconto = 0;

        if (tbItem != null) {
            if (tbItem.desconto > 0) {
                percdesconto = tbItem.desconto;
            }
        }

        if (Constants.MOVIMENTO.percdescontopadrao > 0) {
            percdesconto = Constants.MOVIMENTO.percdescontopadrao;
        }

        valorDesconto = (material.precovenda1 * (percdesconto / 100));
        value = material.precovenda1 - valorDesconto;

        material.precovenda1 = value;
    }

    public void recalcularMovimento(Movimento mov, List<MovimentoItem> listMovItem) {
        float total_fecoepst = 0;
        float total_ipi = 0;
        float total_icmssubst = 0;
        float total_material = 0;

        for (int i = 0; i < listMovItem.size(); i++) {
            total_fecoepst = total_fecoepst + listMovItem.get(i).valoricmsfecoepst;
            total_ipi = total_ipi + listMovItem.get(i).valoripi;
            total_icmssubst = total_icmssubst + listMovItem.get(i).valoricmssubst;

            total_material = total_material + (listMovItem.get(i).custo * listMovItem.get(i).quantidade);
        }

        mov.totalliquido = (total_material + total_fecoepst + total_ipi + total_icmssubst);

        MovimentoDAO.getInstance(context).createOrUpdate(mov);
    }
}
