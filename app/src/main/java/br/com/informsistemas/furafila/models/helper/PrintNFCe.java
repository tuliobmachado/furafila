package br.com.informsistemas.furafila.models.helper;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.nexgo.oaf.apiv3.APIProxy;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.SdkResult;
import com.nexgo.oaf.apiv3.device.printer.AlignEnum;
import com.nexgo.oaf.apiv3.device.printer.GrayLevelEnum;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;
import com.nexgo.oaf.apiv3.device.printer.Printer;

import br.com.informsistemas.furafila.models.pojo.DadosImpressao;
import br.com.informsistemas.furafila.models.utils.CPFCNPJMask;

public class PrintNFCe {

    private static void onMontarDados(Printer printer, DadosImpressao dadosImpressao){
        // HEADER
        printer.appendPrnStr(dadosImpressao.RazaoSocial, Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, false);
        printer.appendPrnStr("CNPJ:"+dadosImpressao.CnpjEmpresa + " - IE: "+dadosImpressao.InscricaoEstadual+ " - IM: "+dadosImpressao.InscricaoMunicipal, Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr("DANFE NFC-e - Documento Auxiliar da Nota Fiscal de Consumidor Eletrônica", Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        printer.appendPrnStr("Não permite aproveitamento de crédito do ICMS", Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        // HEADER

        // PRODUTOS
        printer.appendPrnStr("CODIGO    DESCRICAO", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE, "QTD|UND|VL UNI|VL TOTAL"), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);

        for (int i = 0; i < dadosImpressao.ListaProdutos.size(); i++) {
            printer.appendPrnStr(dadosImpressao.ListaProdutos.get(i).CodigoMaterial+" "+dadosImpressao.ListaProdutos.get(i).Descricao, Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
            printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE, Misc.formatMoeda(dadosImpressao.ListaProdutos.get(i).Quantidade.floatValue())+"|"+dadosImpressao.ListaProdutos.get(i).Unidade+"|"+ Misc.formatMoeda(dadosImpressao.ListaProdutos.get(i).Custo.floatValue())+"|"+ Misc.formatMoeda(dadosImpressao.ListaProdutos.get(i).TotalItem.floatValue())), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        }
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        // PRODUTOS

        //TOTAIS
        printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE, "QTD. TOTAL DE ITENS|"+ Misc.formatMoeda(dadosImpressao.ListaProdutos.size())), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE,"Valor Produtos|"+ Misc.formatMoeda(dadosImpressao.ValorProdutos.floatValue())), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE,"Acréscimos|"+ Misc.formatMoeda(dadosImpressao.ValorAcrescimos.floatValue())), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE,"Descontos|"+ Misc.formatMoeda(dadosImpressao.ValorDescontos.floatValue())), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE,"VALOR À PAGAR|"+ Misc.formatMoeda(dadosImpressao.ValorAPagar.floatValue())), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        //TOTAIS

        //PAGAMENTOS
        printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE, "FORMA DE PAGAMENTO|VALOR"), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);

        for (int i = 0; i < dadosImpressao.ListaFormasPagamento.size(); i++) {
            printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE, dadosImpressao.ListaFormasPagamento.get(i).Descricao+"|"+ Misc.formatMoeda(dadosImpressao.ListaFormasPagamento.get(i).ValorDuplicata.floatValue())), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        }
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        //PAGAMENTOS

        //TROCO
        printer.appendPrnStr(Misc.onImprimeLinha(Enums.ALINHAMENTO.SPACE, "Troco R$|"+ Misc.formatMoeda(dadosImpressao.Troco.floatValue())), Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        //

        //COMPLEMENTOS
        printer.appendPrnStr(dadosImpressao.InformacoesComplementares, Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        //COMPLEMENTOS

        //DADOS EMISSÃO
        if (!dadosImpressao.AmbienteProducao){
            printer.appendPrnStr("EMITIDA EM AMBIENTE DE HOMOLOGAÇÃO - SEM VALOR FISCAL", Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        }

        printer.appendPrnStr("Número: "+dadosImpressao.NumeroNota+" - Série: "+dadosImpressao.Serie, Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        printer.appendPrnStr("Emissão: "+ Misc.formatDate(dadosImpressao.DataEmissao, "dd/MM/yyyy HH:mm:ss"), Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        //DADOS EMISSÃO

        //DADOS DE ACESSO
        printer.appendPrnStr("Consulte pela chave de acesso em: "+dadosImpressao.ChaveUrl, Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        printer.appendPrnStr("CHAVE DE ACESSO", Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        printer.appendPrnStr(dadosImpressao.ChaveNota, Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        //DADOS DE ACESSO

        //DADOS DO CLIENTE
        printer.appendPrnStr("CONSUMIDOR", Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);

        if (!dadosImpressao.CPFCliente.equals("")){
            printer.appendPrnStr("CPF: "+ CPFCNPJMask.getMask(dadosImpressao.CPFCliente), Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);

            if (!dadosImpressao.EnderecoCliente.equals("")){
                printer.appendPrnStr(dadosImpressao.EnderecoCliente, Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
            }
        }else{
            printer.appendPrnStr("CONSUMIDOR NÃO IDENTIFICADO", Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        }
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        //DADOS DO CLIENTE

        //QRCODE
        printer.appendPrnStr("Consulta via leitor de QR Code", Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendQRcode(dadosImpressao.QrCode, 250, AlignEnum.CENTER);
        printer.appendPrnStr("_____________________________", Constants.FONT.SIZE_NORMAL, AlignEnum.LEFT, true);
        printer.appendPrnStr("Protocolo de Autorização", Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        printer.appendPrnStr(dadosImpressao.ProtocoloAutorizacao+" - "+ Misc.formatDate(dadosImpressao.DataAutorizacao, "dd/MM/yyyy HH:mm:ss"), Constants.FONT.SIZE_NORMAL, AlignEnum.CENTER, true);
        //QRCODE
    }

    public static void execute (final Activity activity, DadosImpressao dadosImpressao){
        Printer printer;
        DeviceEngine deviceEngine;

        deviceEngine = APIProxy.getDeviceEngine(activity);
        printer = deviceEngine.getPrinter();
        printer.setTypeface(Typeface.DEFAULT);

        printer.initPrinter();
        printer.setLetterSpacing(5);
        printer.setGray(GrayLevelEnum.LEVEL_2);

        onMontarDados(printer, dadosImpressao);

        printer.startPrint(true, new OnPrintListener() {
            @Override
            public void onPrintResult(final int retCode) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (retCode) {
                            case SdkResult.Success:
                                Toast.makeText(activity, "Impresso com sucesso", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Impresso com sucesso");
                                break;
                            case SdkResult.Printer_Print_Fail:
                                Toast.makeText(activity, "Falha na impressão", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Falha na impressão");
                                break;
                            case SdkResult.Printer_PaperLack:
                                Toast.makeText(activity, "Sem Papel", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Sem Papel");
                                break;
                            case SdkResult.Printer_UnFinished:
                                Toast.makeText(activity, "Impressão não Terminou", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Impressão não terminou");
                                break;
                            case SdkResult.Printer_TooHot:
                                Toast.makeText(activity, "Superaquecimento da impressora", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Superaquecimento da impressora");
                                break;
                            default:
                                Toast.makeText(activity, "Erro desconhecido", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Erro desconhecido");
                        }

                    }
                });
            }
        });
    }

    public static void execute(final Fragment fragment, DadosImpressao dadosImpressao){
        Printer printer;
        DeviceEngine deviceEngine;

        deviceEngine = APIProxy.getDeviceEngine(fragment.getActivity());
        printer = deviceEngine.getPrinter();
        printer.setTypeface(Typeface.DEFAULT);

        printer.initPrinter();
        printer.setLetterSpacing(5);
        printer.setGray(GrayLevelEnum.LEVEL_2);

        onMontarDados(printer, dadosImpressao);

        printer.startPrint(true, new OnPrintListener() {
            @Override
            public void onPrintResult(final int retCode) {
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (retCode) {
                            case SdkResult.Success:
                                Toast.makeText(fragment.getActivity(), "Impresso com sucesso", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Impresso com sucesso");
                                break;
                            case SdkResult.Printer_Print_Fail:
                                Toast.makeText(fragment.getActivity(), "Falha na impressão", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Falha na impressão");
                                break;
                            case SdkResult.Printer_PaperLack:
                                Toast.makeText(fragment.getActivity(), "Sem Papel", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Sem Papel");
                                break;
                            case SdkResult.Printer_UnFinished:
                                Toast.makeText(fragment.getActivity(), "Impressão não Terminou", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Impressão não terminou");
                                break;
                            case SdkResult.Printer_TooHot:
                                Toast.makeText(fragment.getActivity(), "Superaquecimento da impressora", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Superaquecimento da impressora");
                                break;
                            default:
                                Toast.makeText(fragment.getActivity(), "Erro desconhecido", Toast.LENGTH_SHORT).show();
//                                retornoStatus.setText("Erro desconhecido");
                        }

                    }
                });
            }
        });

    }
}
