package com.api.common;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Utilities 
 */
public class Utils
    extends UtilsBase
{
    /**
     * Criacao de uma chave AES
     * 
     * @param bitLength
     * @param random
     * @return Chave AES
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static SecretKey createKeyForAES(
        int          bitLength,
        SecureRandom random)
        throws NoSuchAlgorithmException, NoSuchProviderException
    {
        KeyGenerator generator = KeyGenerator.getInstance("AES", "BC");
        
        generator.init(256, random);
        
        return generator.generateKey();
    }
    
    /**
     * Converte um byte array de 8 bits numa string
     * 
     * @param bytes array contendo os caracteres
     * @param length N. de bytes a processar
     * @return String que representa os bytes
     */
    public static String toString(
        byte[] bytes,
        int    length)
    {
        char[]	chars = new char[length];
        
        for (int i = 0; i != chars.length; i++)
        {
            chars[i] = (char)(bytes[i] & 0xff);
        }
        
        return new String(chars);
    }
    
    /**
     * Convete um array de caracteres de 8 bits numa string
     * 
     * @param bytes - Array que contem os caracteres
     * @return String com a representacao dos bytes
     */
    public static String toString(
        byte[]	bytes)
    {
        return toString(bytes, bytes.length);
    }
    
    /**
     * Converte a string passada num array de bytes
     * a partir dos 8 bits de cada caracter contido no array
     * 
     * @param string - String a converter
     * @return - retorna representacao em array de bytes 
     */
    public static byte[] toByteArray(
        String string)
    {
        byte[]	bytes = new byte[string.length()];
        char[]  chars = string.toCharArray();
        
        for (int i = 0; i != chars.length; i++)
        {
            bytes[i] = (byte)chars[i];
        }
        
        return bytes;
    }
}
