package util;

import java.io.*;
import java.util.*;

/**
 * Implementação do algoritmo de compressão de Huffman.
 * Utiliza uma árvore binária para codificar bytes mais frequentes com menos bits.
 */
public class HuffmanCompressor {
    
    /**
     * Nó da árvore de Huffman
     */
    private static class Node implements Comparable<Node> {
        byte value;
        int frequency;
        Node left;
        Node right;
        boolean isLeaf;
        
        public Node(byte value, int frequency) {
            this.value = value;
            this.frequency = frequency;
            this.isLeaf = true;
        }
        
        public Node(Node left, Node right) {
            this.frequency = left.frequency + right.frequency;
            this.left = left;
            this.right = right;
            this.isLeaf = false;
        }
        
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }
    
    /**
     * Comprime dados usando o algoritmo de Huffman
     */
    public static byte[] compress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        
        // 1. Contar frequências
        Map<Byte, Integer> frequencies = new HashMap<>();
        for (byte b : data) {
            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
        }
        
        // 2. Construir árvore de Huffman
        Node root = buildHuffmanTree(frequencies);
        
        // 3. Gerar códigos
        Map<Byte, String> codes = new HashMap<>();
        generateCodes(root, "", codes);
        
        // 4. Codificar dados
        StringBuilder encoded = new StringBuilder();
        for (byte b : data) {
            encoded.append(codes.get(b));
        }
        
        // 5. Serializar resultado
        return serialize(frequencies, encoded.toString(), data.length);
    }
    
    /**
     * Descomprime dados comprimidos com Huffman
     */
    public static byte[] decompress(byte[] compressedData) throws IOException {
        if (compressedData == null || compressedData.length == 0) {
            return new byte[0];
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        DataInputStream dis = new DataInputStream(bais);
        
        try {
            // 1. Ler tamanho original
            int originalSize = dis.readInt();
            if (originalSize == 0) {
                return new byte[0];
            }
            
            // 2. Ler tabela de frequências
            int tableSize = dis.readInt();
            Map<Byte, Integer> frequencies = new HashMap<>();
            for (int i = 0; i < tableSize; i++) {
                byte value = dis.readByte();
                int freq = dis.readInt();
                frequencies.put(value, freq);
            }
            
            // 3. Reconstruir árvore
            Node root = buildHuffmanTree(frequencies);
            
            // 4. Ler dados comprimidos
            int compressedBytesCount = dis.readInt();
            byte[] compressedBytes = new byte[compressedBytesCount];
            dis.readFully(compressedBytes);
            
            // 5. Ler bits válidos no último byte
            int validBitsInLastByte = dis.readByte();
            
            // 6. Decodificar
            return decode(root, compressedBytes, validBitsInLastByte, originalSize);
            
        } finally {
            dis.close();
        }
    }
    
    /**
     * Constrói a árvore de Huffman usando uma fila de prioridade
     */
    private static Node buildHuffmanTree(Map<Byte, Integer> frequencies) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        
        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
            pq.offer(new Node(entry.getKey(), entry.getValue()));
        }
        
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            pq.offer(new Node(left, right));
        }
        
        return pq.poll();
    }
    
    /**
     * Gera os códigos de Huffman recursivamente
     */
    private static void generateCodes(Node node, String code, Map<Byte, String> codes) {
        if (node == null) return;
        
        if (node.isLeaf) {
            codes.put(node.value, code.isEmpty() ? "0" : code);
        } else {
            generateCodes(node.left, code + "0", codes);
            generateCodes(node.right, code + "1", codes);
        }
    }
    
    /**
     * Serializa a tabela de frequências e os dados codificados
     */
    private static byte[] serialize(Map<Byte, Integer> frequencies, String encoded, int originalSize) 
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // Escrever tamanho original
        dos.writeInt(originalSize);
        
        // Escrever tabela de frequências
        dos.writeInt(frequencies.size());
        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
            dos.writeByte(entry.getKey());
            dos.writeInt(entry.getValue());
        }
        
        // Converter string de bits para bytes
        int numBytes = (encoded.length() + 7) / 8;
        byte[] compressedBytes = new byte[numBytes];
        for (int i = 0; i < encoded.length(); i++) {
            if (encoded.charAt(i) == '1') {
                compressedBytes[i / 8] |= (1 << (7 - (i % 8)));
            }
        }
        
        // Escrever dados comprimidos
        dos.writeInt(compressedBytes.length);
        dos.write(compressedBytes);
        
        // Escrever quantos bits são válidos no último byte
        int validBitsInLastByte = encoded.length() % 8;
        if (validBitsInLastByte == 0) validBitsInLastByte = 8;
        dos.writeByte(validBitsInLastByte);
        
        dos.close();
        return baos.toByteArray();
    }
    
    /**
     * Decodifica os dados usando a árvore de Huffman
     */
    private static byte[] decode(Node root, byte[] compressedBytes, int validBitsInLastByte, int originalSize) {
        byte[] result = new byte[originalSize];
        int resultIndex = 0;
        Node current = root;
        
        // Se a árvore tem apenas um nó (todos os bytes são iguais)
        if (root.isLeaf) {
            Arrays.fill(result, root.value);
            return result;
        }
        
        for (int i = 0; i < compressedBytes.length; i++) {
            int bitsToRead = (i == compressedBytes.length - 1) ? validBitsInLastByte : 8;
            
            for (int j = 0; j < bitsToRead && resultIndex < originalSize; j++) {
                boolean bit = ((compressedBytes[i] >> (7 - j)) & 1) == 1;
                
                current = bit ? current.right : current.left;
                
                if (current.isLeaf) {
                    result[resultIndex++] = current.value;
                    current = root;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Calcula a taxa de compressão
     */
    public static double getCompressionRatio(int originalSize, int compressedSize) {
        if (originalSize == 0) return 0;
        return 100.0 * (originalSize - compressedSize) / originalSize;
    }
}
