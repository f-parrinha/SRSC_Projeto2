package com.api.common;

import java.util.Arrays;

/**
 * Classe auxiliar
 */
public class UtilsBase
{
    private static String	digits = "0123456789abcdef";
    
    /**
     * Retorna string hexadecimal a partir de um byte array de certo tamanho
     * 
     * @param data : bytes a coverter
     * @param length : numero de bytes no bloco de dados a serem convertidos.
     * @return  hex : representacaop em hexadecimal dos dados
     */

   public static String toHex(byte[] data, int length)
    {
        StringBuffer	buf = new StringBuffer();
        
        for (int i = 0; i != length; i++)
        {
            int	v = data[i] & 0xff;
            
            buf.append(digits.charAt(v >> 4));
            buf.append(digits.charAt(v & 0xf));
        }
        
        return buf.toString();
    }
    
    /**
     * Retorna dados passados como byte array numa string hexadecimal
     * 
     * @param data : bytes a serem convertidos
     * @return : representacao hexadecimal dos dados.
     */
    public static String toHex(byte[] data)
    {
        return toHex(data, data.length);
    }

    /**
     * Concats multiple byte arrays into a single one
     *
     * @param arrays set of byte arrays
     * @return concatenated array
     */
    public static byte[] concatArrays(byte[]... arrays) {
        byte[] result = new byte[getConcatLength(arrays)];
        int counter = 0;

        for (byte[] array : arrays) {
            //System.out.println("ARRAY: " + Arrays.toString(array));
            for (byte b : array) {
                result[counter++] = b;
            }
        }
        //System.out.println("RESULT: " + Arrays.toString(result));
        return result;
    }

    private static int getConcatLength(byte[]... arrays) {
        int length = 0;

        // Get length
        for (byte[] array : arrays) {
            length += array.length;
        }

        return length;
    }

    public static byte[] incrementByteArray(byte[] inputBytes) {
        // Convert byte array to a numeric value
        long numericValue = UtilsBase.bytesToLong(inputBytes);

        // Increment the numeric value
        numericValue++;

        // Convert the incremented numeric value back to a byte array
        return UtilsBase.longToBytes(numericValue, inputBytes.length);
    }

    public static long bytesToLong(byte[] bytes) {
        long value = 0;
        for (byte aByte : bytes) {
            value = (value << 8) | (aByte & 0xFF);
        }
        return value;
    }

    public static byte[] longToBytes(long value, int length) {
        byte[] result = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }
}
