package teste;

import util.BackupManager;
import util.BackupManager.BackupResult;
import util.BackupManager.CompressionAlgorithm;

import java.io.File;

/**
 * Teste de compressão e descompressão dos arquivos de dados
 */
public class TestCompressao {
    
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  TESTE DE COMPRESSÃO DE DADOS");
        System.out.println("====================================\n");
        
        try {
            // Verificar se existem dados
            File dadosDir = new File("dados");
            if (!dadosDir.exists()) {
                System.out.println("⚠ Pasta 'dados' não encontrada!");
                System.out.println("Execute SeedData primeiro para gerar dados de teste.");
                return;
            }
            
            System.out.println("1. TESTANDO ALGORITMO HUFFMAN");
            System.out.println("==============================\n");
            
            long startHuffman = System.currentTimeMillis();
            BackupResult huffmanResult = BackupManager.createBackup(CompressionAlgorithm.HUFFMAN);
            long durationHuffman = System.currentTimeMillis() - startHuffman;
            
            System.out.println("✓ Backup Huffman criado com sucesso!\n");
            System.out.println("Arquivos compactados: " + huffmanResult.filesCount);
            System.out.println("Tamanho original: " + String.format("%,d", huffmanResult.originalSize) + 
                             " bytes (" + String.format("%.2f", huffmanResult.originalSize / (1024.0 * 1024.0)) + " MB)");
            System.out.println("Tamanho comprimido: " + String.format("%,d", huffmanResult.compressedSize) + 
                             " bytes (" + String.format("%.2f", huffmanResult.compressedSize / (1024.0 * 1024.0)) + " MB)");
            System.out.println("Taxa de compressão: " + String.format("%.2f%%", huffmanResult.compressionRatio));
            System.out.println("Tempo de execução: " + durationHuffman + " ms");
            System.out.println("Arquivo gerado: " + huffmanResult.backupFile);
            
            System.out.println("\n\n2. TESTANDO ALGORITMO LZW");
            System.out.println("==========================\n");
            
            long startLZW = System.currentTimeMillis();
            BackupResult lzwResult = BackupManager.createBackup(CompressionAlgorithm.LZW);
            long durationLZW = System.currentTimeMillis() - startLZW;
            
            System.out.println("✓ Backup LZW criado com sucesso!\n");
            System.out.println("Arquivos compactados: " + lzwResult.filesCount);
            System.out.println("Tamanho original: " + String.format("%,d", lzwResult.originalSize) + 
                             " bytes (" + String.format("%.2f", lzwResult.originalSize / (1024.0 * 1024.0)) + " MB)");
            System.out.println("Tamanho comprimido: " + String.format("%,d", lzwResult.compressedSize) + 
                             " bytes (" + String.format("%.2f", lzwResult.compressedSize / (1024.0 * 1024.0)) + " MB)");
            System.out.println("Taxa de compressão: " + String.format("%.2f%%", lzwResult.compressionRatio));
            System.out.println("Tempo de execução: " + durationLZW + " ms");
            System.out.println("Arquivo gerado: " + lzwResult.backupFile);
            
            System.out.println("\n\n3. COMPARAÇÃO DOS ALGORITMOS");
            System.out.println("=============================\n");
            
            System.out.println("HUFFMAN vs LZW:");
            System.out.println("---------------");
            
            if (huffmanResult.compressionRatio > lzwResult.compressionRatio) {
                double diff = huffmanResult.compressionRatio - lzwResult.compressionRatio;
                System.out.println("✓ Huffman obteve melhor taxa de compressão");
                System.out.println("  Huffman: " + String.format("%.2f%%", huffmanResult.compressionRatio));
                System.out.println("  LZW: " + String.format("%.2f%%", lzwResult.compressionRatio));
                System.out.println("  Diferença: " + String.format("%.2f%%", diff));
            } else if (lzwResult.compressionRatio > huffmanResult.compressionRatio) {
                double diff = lzwResult.compressionRatio - huffmanResult.compressionRatio;
                System.out.println("✓ LZW obteve melhor taxa de compressão");
                System.out.println("  LZW: " + String.format("%.2f%%", lzwResult.compressionRatio));
                System.out.println("  Huffman: " + String.format("%.2f%%", huffmanResult.compressionRatio));
                System.out.println("  Diferença: " + String.format("%.2f%%", diff));
            } else {
                System.out.println("Ambos obtiveram a mesma taxa de compressão");
            }
            
            System.out.println("\nTempo de execução:");
            if (durationHuffman < durationLZW) {
                System.out.println("✓ Huffman foi mais rápido (" + durationHuffman + " ms vs " + durationLZW + " ms)");
            } else if (durationLZW < durationHuffman) {
                System.out.println("✓ LZW foi mais rápido (" + durationLZW + " ms vs " + durationHuffman + " ms)");
            } else {
                System.out.println("Ambos tiveram o mesmo tempo de execução");
            }
            
            System.out.println("\n\n4. TESTANDO DESCOMPRESSÃO");
            System.out.println("==========================\n");
            
            // Criar backup da pasta dados
            System.out.println("Criando backup temporário dos dados...");
            File tempBackup = new File("dados_backup_temp");
            if (tempBackup.exists()) {
                deleteDirectory(tempBackup);
            }
            copyDirectory(dadosDir, tempBackup);
            
            // Testar descompressão Huffman
            System.out.println("\nTestando descompressão Huffman...");
            deleteDirectory(dadosDir);
            BackupManager.restoreBackup(huffmanResult.backupFile, CompressionAlgorithm.HUFFMAN);
            System.out.println("✓ Huffman: Dados restaurados com sucesso!");
            
            // Testar descompressão LZW
            System.out.println("\nTestando descompressão LZW...");
            deleteDirectory(dadosDir);
            BackupManager.restoreBackup(lzwResult.backupFile, CompressionAlgorithm.LZW);
            System.out.println("✓ LZW: Dados restaurados com sucesso!");
            
            // Restaurar backup original
            System.out.println("\nRestaurando dados originais...");
            deleteDirectory(dadosDir);
            copyDirectory(tempBackup, dadosDir);
            deleteDirectory(tempBackup);
            
            System.out.println("\n\n====================================");
            System.out.println("  TESTE CONCLUÍDO COM SUCESSO!");
            System.out.println("====================================");
            
            System.out.println("\n\nRESPOSTAS PARA O FORMULÁRIO:");
            System.out.println("============================\n");
            
            System.out.println("1. HUFFMAN:");
            System.out.println("   a) Tamanho original: " + String.format("%,d", huffmanResult.originalSize) + " bytes");
            System.out.println("   b) Tamanho comprimido: " + String.format("%,d", huffmanResult.compressedSize) + " bytes");
            System.out.println("   c) Taxa de compressão: " + String.format("%.2f%%", huffmanResult.compressionRatio));
            System.out.println("   d) Interpretação: " + interpretarTaxa(huffmanResult.compressionRatio));
            
            System.out.println("\n2. LZW:");
            System.out.println("   a) Tamanho original: " + String.format("%,d", lzwResult.originalSize) + " bytes");
            System.out.println("   b) Tamanho comprimido: " + String.format("%,d", lzwResult.compressedSize) + " bytes");
            System.out.println("   c) Taxa de compressão: " + String.format("%.2f%%", lzwResult.compressionRatio));
            System.out.println("   d) Interpretação: " + interpretarTaxa(lzwResult.compressionRatio));
            
        } catch (Exception e) {
            System.err.println("\n✗ Erro durante o teste:");
            e.printStackTrace();
        }
    }
    
    private static String interpretarTaxa(double taxa) {
        if (taxa < 0) {
            return "Compressão negativa - o arquivo comprimido ficou maior que o original.";
        } else if (taxa < 10) {
            return "Compressão baixa - redução mínima no tamanho do arquivo.";
        } else if (taxa < 30) {
            return "Compressão moderada - redução significativa no tamanho.";
        } else if (taxa < 50) {
            return "Compressão boa - redução substancial no tamanho.";
        } else {
            return "Compressão excelente - redução muito significativa no tamanho.";
        }
    }
    
    private static void deleteDirectory(File dir) {
        if (!dir.exists()) return;
        
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
    
    private static void copyDirectory(File source, File target) throws Exception {
        if (!target.exists()) {
            target.mkdirs();
        }
        
        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                File targetFile = new File(target, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, targetFile);
                } else {
                    java.nio.file.Files.copy(file.toPath(), targetFile.toPath(), 
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
