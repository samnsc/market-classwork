package com.samnsc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {


    public static int calculateParityDigit(String productCodeWithoutParity) {
        if (productCodeWithoutParity.length() != 12) {
            return -1;
        }

        int sumTotal = 0;
        for (int i = 0; i < 12; i++) {
            sumTotal += Character.getNumericValue(productCodeWithoutParity.charAt(i)) * (i % 2 == 0 ? 1 : 3);
        }

        int remainder = sumTotal % 10;
        return 10 - remainder;
    }

    public static String calculateChecksum(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes());

            return bytesToHexString(encodedHash);
        } catch (NoSuchAlgorithmException exception) {
            // o método MessageDigest.getInstance é tipado como podendo receber qualquer string para o parâmetro
            // algorithm, o que causa problemas caso a string que for passada não seja um algoritmo válido
            // como o java é uma linguagem maravilhosa e muito bem arquitetada ninguém pensou em usar uma enum
            // para esse parâmetro, ou seja, ao invés de isso gerar um erro durante a compilação, passar uma string
            // inválida como parâmetro levanta uma exceção durante o runtime que eu preciso tratar mesmo sabendo
            // que a string "SHA-256" é um algoritmo válido
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, "This error should never be reached as SHA-256 is a valid algorithm", exception);
            return "";
        }
    }

    private static String bytesToHexString(byte[] input) {
        StringBuilder hex = new StringBuilder();

        for (byte b : input) {
            // o resultado desse bitwise AND sempre é o byte b só que tipado como um int
            // (o java deveria converter o byte em int automaticamente, mas ele é burro e
            // usa a representação binária do int como o input ex.: ele usa 00000010 como
            // o input ao invés de 2)
            String aux = Integer.toHexString(0xff & b);

            // como 8 bits sempre viram 2 dígitos hexadecimais (e o java é burro e não
            // retorna a string já nessa representação) é necessário adicionar o 0 antes
            // em casos que apenas um dígito hexadecimal é o suficiente para representar
            // o byte para que ele fique no formato certo
            if (aux.length() == 1) {
                hex.append('0');
            }

            hex.append(aux);
        }

        return hex.toString();
    }
}
