package util;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Gerenciador de backup e restauração de arquivos de dados.
 * Compacta todos os arquivos da pasta 'dados' em um único arquivo usando Huffman ou LZW.
 */
public class BackupManager {
    
    private static final String DATA_FOLDER = "dados";
    private static final String BACKUP_FOLDER = "backups";
    
    /**
     * Algoritmos de compressão disponíveis
     */
    public enum CompressionAlgorithm {
        HUFFMAN,
        LZW
    }
    
    /**
     * Resultado da operação de backup
     */
    public static class BackupResult {
        public long originalSize;
        public long compressedSize;
        public double compressionRatio;
        public String backupFile;
        public int filesCount;
        public CompressionAlgorithm algorithm;
        
        @Override
        public String toString() {
            return String.format(
                "=== Resultado do Backup ===\n" +
                "Algoritmo: %s\n" +
                "Arquivos compactados: %d\n" +
                "Tamanho original: %,d bytes (%.2f MB)\n" +
                "Tamanho comprimido: %,d bytes (%.2f MB)\n" +
                "Taxa de compressão: %.2f%%\n" +
                "Arquivo gerado: %s",
                algorithm,
                filesCount,
                originalSize, originalSize / (1024.0 * 1024.0),
                compressedSize, compressedSize / (1024.0 * 1024.0),
                compressionRatio,
                backupFile
            );
        }
    }
    
    /**
     * Cria um backup de todos os arquivos de dados
     */
    public static BackupResult createBackup(CompressionAlgorithm algorithm) throws IOException {
        // Criar pasta de backups se não existir
        File backupDir = new File(BACKUP_FOLDER);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        // Coletar todos os arquivos de dados
        List<FileData> files = collectDataFiles();
        
        if (files.isEmpty()) {
            throw new IOException("Nenhum arquivo encontrado na pasta " + DATA_FOLDER);
        }
        
        // Serializar todos os arquivos em um único array de bytes
        byte[] allData = serializeFiles(files);
        
        // Comprimir dados
        byte[] compressed;
        String algorithmName;
        
        if (algorithm == CompressionAlgorithm.HUFFMAN) {
            compressed = HuffmanCompressor.compress(allData);
            algorithmName = "huffman";
        } else {
            compressed = LZWCompressor.compress(allData);
            algorithmName = "lzw";
        }
        
        // Gerar nome do arquivo de backup
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFileName = String.format("%s/backup_%s_%s.dat", BACKUP_FOLDER, algorithmName, timestamp);
        
        // Salvar arquivo comprimido
        try (FileOutputStream fos = new FileOutputStream(backupFileName)) {
            fos.write(compressed);
        }
        
        // Criar resultado
        BackupResult result = new BackupResult();
        result.originalSize = allData.length;
        result.compressedSize = compressed.length;
        result.compressionRatio = 100.0 * (allData.length - compressed.length) / (double) allData.length;
        result.backupFile = backupFileName;
        result.filesCount = files.size();
        result.algorithm = algorithm;
        
        return result;
    }
    
    /**
     * Restaura um backup
     */
    public static void restoreBackup(String backupFilePath, CompressionAlgorithm algorithm) throws IOException {
        // Ler arquivo comprimido
        byte[] compressed = Files.readAllBytes(Paths.get(backupFilePath));
        
        // Descomprimir
        byte[] decompressed;
        if (algorithm == CompressionAlgorithm.HUFFMAN) {
            decompressed = HuffmanCompressor.decompress(compressed);
        } else {
            decompressed = LZWCompressor.decompress(compressed);
        }
        
        // Desserializar e restaurar arquivos
        deserializeAndRestoreFiles(decompressed);
        
        System.out.println("Backup restaurado com sucesso!");
        System.out.println("Total de bytes restaurados: " + decompressed.length);
    }
    
    /**
     * Lista backups disponíveis
     */
    public static List<String> listBackups() {
        List<String> backups = new ArrayList<>();
        File backupDir = new File(BACKUP_FOLDER);
        
        if (backupDir.exists() && backupDir.isDirectory()) {
            File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File file : files) {
                    backups.add(file.getName());
                }
            }
        }
        
        backups.sort(Collections.reverseOrder()); // Mais recentes primeiro
        return backups;
    }
    
    /**
     * Coleta todos os arquivos da pasta de dados recursivamente
     */
    private static List<FileData> collectDataFiles() throws IOException {
        List<FileData> files = new ArrayList<>();
        File dataDir = new File(DATA_FOLDER);
        
        if (!dataDir.exists()) {
            throw new IOException("Pasta de dados não encontrada: " + DATA_FOLDER);
        }
        
        collectFilesRecursive(dataDir, "", files);
        return files;
    }
    
    private static void collectFilesRecursive(File dir, String relativePath, List<FileData> files) throws IOException {
        File[] entries = dir.listFiles();
        if (entries == null) return;
        
        for (File entry : entries) {
            String currentPath = relativePath.isEmpty() ? entry.getName() : relativePath + "/" + entry.getName();
            
            if (entry.isDirectory()) {
                collectFilesRecursive(entry, currentPath, files);
            } else {
                byte[] content = Files.readAllBytes(entry.toPath());
                files.add(new FileData(currentPath, content));
            }
        }
    }
    
    /**
     * Serializa lista de arquivos em um único array de bytes
     */
    private static byte[] serializeFiles(List<FileData> files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // Escrever número de arquivos
        dos.writeInt(files.size());
        
        // Escrever cada arquivo
        for (FileData file : files) {
            dos.writeUTF(file.path);
            dos.writeInt(file.content.length);
            dos.write(file.content);
        }
        
        dos.close();
        return baos.toByteArray();
    }
    
    /**
     * Desserializa e restaura arquivos
     */
    private static void deserializeAndRestoreFiles(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        
        int numFiles = dis.readInt();
        
        for (int i = 0; i < numFiles; i++) {
            String path = dis.readUTF();
            int length = dis.readInt();
            byte[] content = new byte[length];
            dis.readFully(content);
            
            // Criar diretórios necessários
            File file = new File(DATA_FOLDER + "/" + path);
            file.getParentFile().mkdirs();
            
            // Escrever arquivo
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content);
            }
        }
        
        dis.close();
    }
    
    /**
     * Classe auxiliar para armazenar dados de arquivo
     */
    private static class FileData {
        String path;
        byte[] content;
        
        FileData(String path, byte[] content) {
            this.path = path;
            this.content = content;
        }
    }
    
    /**
     * Detecta algoritmo do arquivo de backup pelo nome
     */
    public static CompressionAlgorithm detectAlgorithm(String fileName) {
        if (fileName.contains("huffman")) {
            return CompressionAlgorithm.HUFFMAN;
        } else if (fileName.contains("lzw")) {
            return CompressionAlgorithm.LZW;
        }
        throw new IllegalArgumentException("Não foi possível detectar o algoritmo do backup: " + fileName);
    }
}
