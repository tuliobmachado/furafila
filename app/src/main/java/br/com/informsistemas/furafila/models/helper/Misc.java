package br.com.informsistemas.furafila.models.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.informsistemas.furafila.models.dao.AtualizacaoDAO;
import br.com.informsistemas.furafila.models.pojo.Atualizacao;
import br.com.informsistemas.furafila.models.pojo.Categoria;
import br.com.informsistemas.furafila.models.pojo.Material;
import br.com.informsistemas.furafila.models.utils.DateDeserializer;

public class Misc {

    public static void setAplicacao(String codigoAplicacao){
        Constants.APP.TIPO_APLICACAO = Enums.TIPO_APLICACAO.valueOfLabel(codigoAplicacao);
    }

    public List<Material> cloneMaterialPesquisa(List<Material> original) throws CloneNotSupportedException {
        List<Material> result = new ArrayList<>(original.size());
        for (Material o : original) {
            result.add((Material) o.clone());
        }
        return result;
    }

    public List<Categoria> cloneCategoriaPesquisa(List<Categoria> original) throws CloneNotSupportedException {
        List<Categoria> result = new ArrayList<>(original.size());
        for (Categoria o : original) {
            result.add((Categoria) o.clone());
        }
        return result;
    }

    public static Material cloneMaterial(Material original) throws CloneNotSupportedException {
        Material m = null;
        m = (Material) original.clone();

        return m;
    }

    public static RotateAnimation getRotateAnimation(boolean excluindo){
        int fromDregress = 0;
        int toDegress = 0;

        if (excluindo){
            fromDregress = 180;
            toDegress = 0;
        }else {
            fromDregress = 0;
            toDegress = 180;
        }

        RotateAnimation animation = new RotateAnimation(fromDregress, toDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);

        return animation;
    }

    public static void setTabelasPadrao() {
        Constants.MOVIMENTO.percdescontopadrao = 0;
        Constants.MOVIMENTO.codigotabelapreco = Constants.DTO.registro.codigotabelapreco;
        Constants.MOVIMENTO.codigoformapagamento = "";
        Constants.MOVIMENTO.codigoalmoxarifado = Constants.DTO.registro.codigoalmoxarifado;
        Constants.MOVIMENTO.codigoempresa = Constants.DTO.registro.codigoempresa;
        Constants.MOVIMENTO.codigofilialcontabil = Constants.DTO.registro.codigofilialcontabil;
        Constants.MOVIMENTO.codigooperacao = Constants.DTO.registro.codigooperacao;
        Constants.MOVIMENTO.estadoParceiro = "";
        Constants.MOVIMENTO.parceiro = null;

    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.equals(""))
            return false;
        return true;
    }

    public static <T> String getJsonString(T object, Boolean excludeExpose) {
        Gson gson = null;

        if (excludeExpose) {
            gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").registerTypeAdapter(Date.class, new DateDeserializer()).create();
        } else {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").registerTypeAdapter(Date.class, new DateDeserializer()).create();
        }

        return gson.toJson(object);
    }

    public static boolean verificaConexao(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conectivtyManager.getActiveNetworkInfo() != null && conectivtyManager.getActiveNetworkInfo().isAvailable() && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }

        return conectado;
    }

    public static float fRound(boolean truncar, float value, int casasDecimais) {
        float result = value;

        if (truncar) {
            String arg = "" + value;
            int idx = arg.indexOf('.');
            if (idx != -1) {
                if (arg.length() > idx + casasDecimais) {
                    arg = arg.substring(0, idx + casasDecimais + 1);
                    result = Float.parseFloat(arg);
                }
            }
        } else {
            BigDecimal valorArredondado = new BigDecimal(value).setScale(casasDecimais, RoundingMode.HALF_EVEN);

            result = valorArredondado.floatValue();
        }
        return result;
    }

    public static Date GetDateAtual(){
        Date data = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            data = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static Boolean CompareDate(Date dataInicial, Date dataFinal){
        Boolean value = false;
        Date dtInicio, dtFinal;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            dtInicio = simpleDateFormat.parse(simpleDateFormat.format(dataInicial));
            dtFinal = simpleDateFormat.parse(simpleDateFormat.format(dataFinal));

            if (dtInicio.compareTo(dtFinal) > 0){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static Boolean CompareTime(Date dataInicial, Date dataFinal){
        Boolean value = false;
        String dtInicio, dtFinal;
        Integer hrInicio, minInicio, hrFinal, minFinal;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        dtInicio = simpleDateFormat.format(dataInicial);
        dtFinal = simpleDateFormat.format(dataFinal);

        hrInicio = Integer.parseInt(dtInicio.substring(0, 2));
        minInicio = Integer.parseInt(dtInicio.substring(3, 5));

        hrFinal = Integer.parseInt(dtFinal.substring(0, 2));
        minFinal = Integer.parseInt(dtFinal.substring(3, 5));

        int difHoras = hrInicio - hrFinal;
        int difMinutos = minInicio - minFinal;

        while (difMinutos < 0) {
            difMinutos += 60;
            difHoras--;
        }

        while (difHoras < 0) {
            difHoras += 24;
        }

        if (difHoras >= 1){
            value = true;
        }

        return value;
    }


    public static String gerarMD5(){
        String chave = Constants.DTO.registro.codigofuncionario + formatDate(new Date(), "yyyyMMddHHmmss");
        String value = new String(Hex.encodeHex(DigestUtils.md5(chave)));

        return value;
    }

    public static String formatDate(Date data, String formate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formate);

        return simpleDateFormat.format(data);
    }

    public static String getPeriodoMeta(Date data) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");

        return simpleDateFormat.format(data);
    }

    public static Date getDataPadrao() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String dateInString = "30-12-1899";

        Date date = null;
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Date getStringToDate(String data, String formate){
        Date dt = null;
        SimpleDateFormat format = new SimpleDateFormat(formate);

        try {
            dt = format.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dt;
    }

    public static String formatMoeda(float value) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        String valorFormatado = numberFormat.format(value);
        valorFormatado = valorFormatado.replace("R$", "");

        return valorFormatado;
    }

    public static int GetReturnPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    public static void SolicitaPermissao(Activity activity, String[] permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, permission, requestCode);
    }

    public static Date getMaiorDataAtualizacao(Context context, String nometabela) {
        Atualizacao atualizacao = AtualizacaoDAO.getInstance(context).findByNomeTabela(nometabela);
        Date maiorData = null;

        if ((atualizacao.datasincmarcado.compareTo(atualizacao.datasinctotal) < 0) ||
                (atualizacao.datasincmarcado.compareTo(atualizacao.datasincparcial) < 0)) {
            maiorData = atualizacao.datasincmarcado;
        } else if ((atualizacao.datasincparcial.compareTo(atualizacao.datasinctotal) < 0) ||
                (atualizacao.datasincparcial.compareTo(atualizacao.datasincmarcado) < 0)) {
            maiorData = atualizacao.datasincparcial;
        } else if ((atualizacao.datasinctotal.compareTo(atualizacao.datasincparcial) < 0) ||
                (atualizacao.datasinctotal.compareTo(atualizacao.datasincmarcado) < 0)) {
            maiorData = atualizacao.datasinctotal;
        }

        return maiorData;

    }

    public static Material calculaTotalLiquido(Context context, Material material){
        material.precovenda1 = (material.custo * material.quantidade);

        if (Constants.APP.TIPO_APLICACAO == Enums.TIPO_APLICACAO.FORCA_DE_VENDAS) {
            CalculoClass calculoClass = new CalculoClass(context, material);
            calculoClass.setTributos();
        }

        return material;
    }

    public static float calculaTotalExclusao(Context context, Material m){
        m.precovenda1 = (m.custo * m.quantidade);

        if (Constants.APP.TIPO_APLICACAO == Enums.TIPO_APLICACAO.FORCA_DE_VENDAS) {
            CalculoClass calculoClass = new CalculoClass(context, m);
            calculoClass.setTributos();
            return calculoClass.getTotalLiquido();
        }else{
            return m.precovenda1;
        }
    }

    public static Integer CountStr(String aString, String subStr){
        Integer value = 0;
        Integer ini;

        if (aString.equals("")){
            return value;
        }

        ini = aString.indexOf(subStr);

        while (ini > 0){
            value++;
            ini = aString.indexOf(subStr, ini + 1);
        }

        return value;
    }

    public static String StuffString(String aText, Integer aStart, Integer aLength, String aSubText){
        String value = "";

        value = aText.substring(0, aStart);
        value = value + aSubText;
        value = value + aText.substring(aStart+aLength, aText.length());

        return value;
    }

    public static String StringOfChar(Character caracter, Integer count){
        String value = "";

        while (count > 0) {
            count--;
            value = value + caracter;
        }

        return value;
    }

    public static String LeftStr(String aText, Integer aCount){
        return aText.substring(0, aCount);
    }

    public static String PadRight(String aString, Integer nLen, Character caracter){
        String value = aString;
        Integer tam = aString.length();

        if (caracter == null){
            caracter = 32;
        }

        if (tam < nLen){
            value = StringOfChar(caracter, (nLen - tam)) + aString;
        }else{
            value = LeftStr(aString, nLen);
        }

        return value;
    }

    public static String PadLeft(String aString, Integer nLen, Character caracter){
        String value = aString;
        Integer tam = aString.length();

        if (caracter == null){
            caracter = 32;
        }

        if (tam < nLen){
            value = StringOfChar(caracter, (nLen - tam)) + aString;
        }else{
            value = LeftStr(aString, nLen);
        }

        return value;
    }

    public static String PadCenter(String aString, Integer nLen, Character caracter){
        String value = aString;
        Integer tam = aString.length();
        Integer nCharLeft;

        if (caracter == null){
            caracter = 32;
        }

        if (tam < nLen){
            BigDecimal valor = new BigDecimal(String.valueOf(nLen)).subtract(new BigDecimal(String.valueOf(tam))).divide(new BigDecimal("2"));
            String valorFinal = String.valueOf(Misc.fRound(true, valor.floatValue(), 0));
            nCharLeft = Integer.valueOf(valorFinal.substring(0, valorFinal.indexOf("."))) ;
            value = PadRight(StringOfChar(caracter, nCharLeft) + aString, nLen, caracter);
        }else{
            value = LeftStr(aString, nLen);
        }

        return value;
    }

    public static String ApenasNumeros(String value){
        return value.replaceAll("[^0-9]", "");
    }

    public static String PadSpace(String aString, Integer nLen, String separador, Character caracter, Boolean removerEspacos){
        String value = aString;
        String stuffStr, valor;
        Integer nSep, dIni, nCharSep, nResto, nFeito, Ini;
        Double D;
        stuffStr = "";

        if (caracter == null){
            caracter = 32;
        }

        if (removerEspacos == null){
            removerEspacos = true;
        }

        if (aString.length() > nLen){
            value = aString.substring(0, nLen);
        }

        if (separador.equals(caracter)){
            value = value.replaceAll(separador, "ÿ");
            separador = "ÿ";
        }

        nSep = CountStr(value, separador);

        if (nSep < 1){
            value = PadRight(value, nLen, caracter);
            return value;
        }

        if (removerEspacos){
            value = value.trim();
        }

        dIni = (nLen - (value.length()-nSep));
        BigDecimal decimal = new BigDecimal(String.valueOf(dIni)).divide(new BigDecimal(String.valueOf(nSep)), 2, BigDecimal.ROUND_HALF_EVEN);
        String valorFinal = String.valueOf(Misc.fRound(true, decimal.floatValue(), 0));
//        String valorFormatado = valorFinal.substring(0, valorFinal.indexOf("."));
        nCharSep = Integer.valueOf(valorFinal.substring(0, valorFinal.indexOf("."))) ;
        nResto = nLen - ( ( value.length()-nSep) + (nCharSep*nSep) );
        nFeito = nSep;
        stuffStr = StringOfChar(caracter, nCharSep);

        Ini = value.indexOf(separador);

        while (Ini > 0) {
            if (nFeito <= nResto){
                valor = String.valueOf(caracter);
            }else{
                valor = "";
            }

            value = StuffString(value, Ini, separador.length(), stuffStr+valor);
            nFeito++;
            Ini = value.indexOf(separador);
        }

        return value;
    }

    public static String onImprimeLinha(Enums.ALINHAMENTO alinhamento, String mensagem){
        String value = "";

        switch (alinhamento){
            case DIREITA:
                value = PadRight(mensagem, 31, null);
                break;
            case ESQUERDA:
                value = PadLeft(mensagem, 31, null);
                break;
            case CENTRO:
                value = PadCenter(mensagem, 31, null);
                break;
            case SPACE:
                value = PadSpace(mensagem, 31, "|", null, null);
                break;
        }

        return value;
    }

    public static void alerta(Context context, String mensagem){
        Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show();
    }
}
