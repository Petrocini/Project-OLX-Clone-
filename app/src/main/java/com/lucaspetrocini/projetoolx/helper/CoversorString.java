package com.lucaspetrocini.projetoolx.helper;

public class CoversorString {

    public static String converterTelefone(String fone){
        fone = fone.replaceAll("-", "");
        fone = fone.replaceAll("\\(", "");
        fone = fone.replaceAll("\\)", "");

        return fone;
    }

    public static String converterMoeda(String valor){
        valor = valor.replaceAll("\\.", "");
        return valor;
    }

}
