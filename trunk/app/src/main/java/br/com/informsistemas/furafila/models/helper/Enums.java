package br.com.informsistemas.furafila.models.helper;

public class Enums {

    public enum TIPO_SINCRONIA{
        NENHUMA(""), PARCIAL("Parcial"), MARCADOS("Marcados"), TOTAL("Total");

        private final String value;

        TIPO_SINCRONIA(String s) {
            value = s;
        }

        public String getString(){
            return value;
        }
    }

    public enum TIPO_APLICACAO{
        FORCA_DE_VENDAS("0002"), NFCE("0004");

        private final String value;

        TIPO_APLICACAO(String s){ this.value = s; }

        public String getString(){ return this.value; }

        public static TIPO_APLICACAO valueOfLabel(String label) {
            for (TIPO_APLICACAO e : values()) {
                if (e.value.equals(label)) {
                    return e;
                }
            }
            return null;
        }
    }

    public enum ALINHAMENTO{
        CENTRO, DIREITA, ESQUERDA, SPACE;
    }
}
