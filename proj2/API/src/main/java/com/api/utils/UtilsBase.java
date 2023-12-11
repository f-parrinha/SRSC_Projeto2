package com.api.utils;

import com.api.common.shell.Shell;
import com.api.rest.RestResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Classe auxiliar
 */
public class UtilsBase
{
    private static String	digits = "0123456789abcdef";

    /**
     * Checks if a given path is invalid
     * @param path path to validate
     * @return true if invalid, false if valid
     */
    public static boolean isPathInvalid(String path) {
        boolean ends_with_slash = !path.isEmpty() && path.substring(path.length() - 1).contains("/");
        boolean starts_with_slash = !path.isEmpty() && path.substring(0, 1).contains("/");
        return path.contains("//") || ends_with_slash || starts_with_slash;
    }

    /**
     * From path tokens (folders), create a String with the full path
     * @param tokens set of ordered folders
     * @return String in a format like folder1/folder2/.../folderN
     */
    public static String createPathString(String[] tokens) {
        int idx = 0;
        String result = "";
        for (String path : tokens) {
            result = result.concat(path + (idx + 1 != tokens.length ? "/" : ""));
            idx++;
        }

        return result;
    }


    /**
     * Encodes a file's content into a Base64 String
     * @param content file's content
     * @return Base64 encoded object (String)
     */
    public static String encodeFileToJSON(InputStream content) {
        String result = null;

        try {
            byte[] contentBytes = content.readAllBytes();
            result = Base64.getEncoder().encodeToString(contentBytes);
        } catch (IOException e) {
            e.printStackTrace();
            Shell.printError("Could not read content during file encoding.");
        }

        return result;
    }

    /**
     * Decodes a Base64 encoded file into an InputStream, to get the desired file's content
     * @param encodedContent the Base64 encoded string (representing the file's content)
     * @return File's content as an InputStream
     */
    public static InputStream decodeToFile(String encodedContent) {
        byte[] contentBytes = Base64.getDecoder().decode(encodedContent);

        return new ByteArrayInputStream(contentBytes);
    }
    public static String generateDownloadCode(String fileName, String encodedContent) {
        return RestResponse.DOWNLOAD_CODE + fileName.length() + fileName + encodedContent;
    }

    public static String[] retrieveDownload(String downloadContent) {
        int fileLastIdx = Integer.parseInt(downloadContent.substring(0, 1)) + 1;

        String fileName = downloadContent.substring(1, fileLastIdx);
        String content = downloadContent.substring(fileLastIdx);
        Shell.printDebug(downloadContent);
        Shell.printDebug(fileName);
        Shell.printDebug(content);
        return new String[] {fileName, content};
    }

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

        long numericValue = UtilsBase.bytesToLong(inputBytes);
        numericValue++;

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

    public static PublicKey createPublicKey(byte[] publicKeyBytes, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static KeyPair generateKeyPair(String algorithm, int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(keySize); // Adjust the key size based on your security requirements
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] generateRandomBytes(int size) {
        SecureRandom secureRandomGenerator = new SecureRandom();
        byte[] random = new byte[size];
        secureRandomGenerator.nextBytes(random);

        return random;
    }
}
