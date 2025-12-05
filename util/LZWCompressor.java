package util;

import java.io.*;
import java.util.*;

/**
 * Implementação do algoritmo de compressão LZW (Lempel-Ziv-Welch).
 * Utiliza um dicionário dinâmico para substituir sequências repetidas por códigos.
 */
public class LZWCompressor {
    
    private static final int MAX_DICT_SIZE = 65536; // 2^16
    private static final int INITIAL_DICT_SIZE = 256; // ASCII completo
    
    /**
     * Comprime dados usando o algoritmo LZW
     */
    public static byte[] compress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        
        // Inicializar dicionário com todos os bytes possíveis
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < INITIAL_DICT_SIZE; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }
        int dictSize = INITIAL_DICT_SIZE;
        
        List<Integer> result = new ArrayList<>();
        String current = "";
        
        for (byte b : data) {
            String next = current + (char) (b & 0xFF);
            
            if (dictionary.containsKey(next)) {
                current = next;
            } else {
                result.add(dictionary.get(current));
                
                // Adicionar nova sequência ao dicionário se houver espaço
                if (dictSize < MAX_DICT_SIZE) {
                    dictionary.put(next, dictSize++);
                }
                
                current = String.valueOf((char) (b & 0xFF));
            }
        }
        
        // Adicionar última sequência
        if (!current.isEmpty()) {
            result.add(dictionary.get(current));
        }
        
        // Serializar resultado
        return serialize(result, data.length);
    }
    
    /**
     * Descomprime dados comprimidos com LZW
     */
    public static byte[] decompress(byte[] compressedData) throws IOException {
        if (compressedData == null || compressedData.length == 0) {
            return new byte[0];
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        DataInputStream dis = new DataInputStream(bais);
        
        try {
            // Ler tamanho original e número de códigos
            int originalSize = dis.readInt();
            if (originalSize == 0) {
                return new byte[0];
            }
            
            int numCodes = dis.readInt();
            List<Integer> codes = new ArrayList<>(numCodes);
            
            for (int i = 0; i < numCodes; i++) {
                codes.add(dis.readInt());
            }
            
            return decode(codes, originalSize);
            
        } finally {
            dis.close();
        }
    }
    
    /**
     * Serializa a lista de códigos
     */
    private static byte[] serialize(List<Integer> codes, int originalSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // Escrever tamanho original e número de códigos
        dos.writeInt(originalSize);
        dos.writeInt(codes.size());
        
        // Escrever códigos
        for (Integer code : codes) {
            dos.writeInt(code);
        }
        
        dos.close();
        return baos.toByteArray();
    }
    
    /**
     * Decodifica a lista de códigos
     */
    private static byte[] decode(List<Integer> codes, int originalSize) {
        // Inicializar dicionário reverso
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < INITIAL_DICT_SIZE; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }
        int dictSize = INITIAL_DICT_SIZE;
        
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        
        if (codes.isEmpty()) {
            return new byte[0];
        }
        
        int prevCode = codes.get(0);
        String current = dictionary.get(prevCode);
        result.write(stringToBytes(current), 0, current.length());
        
        for (int i = 1; i < codes.size(); i++) {
            int code = codes.get(i);
            String entry;
            
            if (dictionary.containsKey(code)) {
                entry = dictionary.get(code);
            } else if (code == dictSize) {
                // Caso especial: código não está no dicionário ainda
                entry = current + current.charAt(0);
            } else {
                throw new IllegalArgumentException("Código inválido: " + code);
            }
            
            result.write(stringToBytes(entry), 0, entry.length());
            
            // Adicionar nova entrada ao dicionário
            if (dictSize < MAX_DICT_SIZE) {
                dictionary.put(dictSize++, current + entry.charAt(0));
            }
            
            current = entry;
        }
        
        return result.toByteArray();
    }
    
    /**
     * Converte string para bytes (cada char é um byte)
     */
    private static byte[] stringToBytes(String s) {
        byte[] bytes = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            bytes[i] = (byte) s.charAt(i);
        }
        return bytes;
    }
    
    /**
     * Calcula a taxa de compressão
     */
    public static double getCompressionRatio(int originalSize, int compressedSize) {
        if (originalSize == 0) return 0;
        return 100.0 * (originalSize - compressedSize) / originalSize;
    }
}
