package br.com.informsistemas.furafila.models.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class CPFCNPJMask {

    private static final String maskCNPJ = "##.###.###/####-##";
    private static final String maskCPF = "###.###.###-##";

    public static String unMask(String s){
        return s.replaceAll("[^0-9]*", "");
    }

    private static String getDefaultMask(String str){
        String defaultMask = maskCPF;

        if (str.length() > 11){
            defaultMask = maskCNPJ;
        }

        return defaultMask;
    }

    public static String getMask(String s){
        String value = "";
        String str = unMask(s);
        String defaultMask = getDefaultMask(s);
        int i = 0;
        String old = "";

        for (char m : defaultMask.toCharArray()) {
            if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                value += m;
                continue;
            }

            try {
                value += str.charAt(i);
            } catch (Exception e) {
                break;
            }
            i++;
        }

        return value;
    }

    public static TextWatcher insert(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = CPFCNPJMask.unMask(s.toString());
                String mask;
                String defaultMask = getDefaultMask(str);
                switch (str.length()) {
                    case 11:
                        mask = maskCPF;
                        break;
                    case 14:
                        mask = maskCNPJ;
                        break;

                    default:
                        mask = defaultMask;
                        break;
                }

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

}
