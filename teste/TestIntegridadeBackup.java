package teste;

import util.BackupManager;
import util.BackupManager.BackupResult;
import util.BackupManager.CompressionAlgorithm;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Teste completo: Compacta, apaga dados, restaura e verifica integridade
 */
public class TestIntegridadeBackup {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  TESTE DE INTEGRIDADE - BACKUP E RESTAURAÇÃO");
        System.out.println("=================================================\n");
        
        try {
            File dadosDir = new File("dados");
            if (!dadosDir.exists()) {
                System.out.println("⚠ Pasta 'dados' não encontrada!");
                return;
            }
            
            // 1. Calcular checksums dos arquivos originais
            System.out.println("1. Calculando checksums dos arquivos originais...");
            Map<String, String> checksumsOriginais = calcularChecksums(dadosDir, "");
            System.out.println("   ✓ " + checksumsOriginais.size() + " arquivos processados\n");
            
            // 2. Criar backup com Huffman
            System.out.println("2. Criando backup com Huffman...");
            BackupResult huffmanBackup = BackupManager.createBackup(CompressionAlgorithm.HUFFMAN);
            System.out.println("   ✓ Backup criado: " + huffmanBackup.backupFile);
            System.out.println("   - Taxa de compressão: " + String.format("%.2f%%", huffmanBackup.compressionRatio) + "\n");
            
            // 3. Criar backup com LZW
            System.out.println("3. Criando backup com LZW...");
            BackupResult lzwBackup = BackupManager.createBackup(CompressionAlgorithm.LZW);
            System.out.println("   ✓ Backup criado: " + lzwBackup.backupFile);
            System.out.println("   - Taxa de compressão: " + String.format("%.2f%%", lzwBackup.compressionRatio) + "\n");
            
            // 4. APAGAR TODOS OS ARQUIVOS DE DADOS
            System.out.println("4. APAGANDO TODOS OS ARQUIVOS DE DADOS...");
            System.out.println("   (Simulando perda de dados)");
            deleteDirectory(dadosDir);
            System.out.println("   ✓ Pasta 'dados' removida completamente\n");
            
            // Verificar que realmente foi apagado
            if (dadosDir.exists()) {
                System.out.println("   ✗ ERRO: Pasta ainda existe!");
                return;
            }
            System.out.println("   ✓ Confirmado: Dados foram completamente apagados\n");
            
            // 5. Restaurar backup Huffman
            System.out.println("5. Restaurando backup Huffman...");
            BackupManager.restoreBackup(huffmanBackup.backupFile, CompressionAlgorithm.HUFFMAN);
            System.out.println("   ✓ Backup Huffman restaurado\n");
            
            // 6. Verificar integridade após Huffman
            System.out.println("6. Verificando integridade dos arquivos (Huffman)...");
            Map<String, String> checksumsHuffman = calcularChecksums(dadosDir, "");
            boolean huffmanOk = verificarIntegridade(checksumsOriginais, checksumsHuffman, "Huffman");
            
            // 7. Apagar novamente e restaurar com LZW
            System.out.println("\n7. Apagando dados novamente...");
            deleteDirectory(dadosDir);
            System.out.println("   ✓ Dados apagados\n");
            
            System.out.println("8. Restaurando backup LZW...");
            BackupManager.restoreBackup(lzwBackup.backupFile, CompressionAlgorithm.LZW);
            System.out.println("   ✓ Backup LZW restaurado\n");
            
            // 8. Verificar integridade após LZW
            System.out.println("9. Verificando integridade dos arquivos (LZW)...");
            Map<String, String> checksumsLZW = calcularChecksums(dadosDir, "");
            boolean lzwOk = verificarIntegridade(checksumsOriginais, checksumsLZW, "LZW");
            
            // Resultado final
            System.out.println("\n=================================================");
            System.out.println("              RESULTADO FINAL");
            System.out.println("=================================================\n");
            
            if (huffmanOk && lzwOk) {
                System.out.println("✓✓✓ TESTE PASSOU COM SUCESSO! ✓✓✓");
                System.out.println("\nAmbos os algoritmos:");
                System.out.println("  • Compactaram corretamente todos os arquivos");
                System.out.println("  • Restauraram os dados sem perda de informação");
                System.out.println("  • Mantiveram a integridade completa dos dados");
                System.out.println("\n✓ O sistema está pronto para uso em produção!");
            } else {
                System.out.println("✗✗✗ TESTE FALHOU ✗✗✗");
                if (!huffmanOk) System.out.println("  • Huffman: Problemas de integridade detectados");
                if (!lzwOk) System.out.println("  • LZW: Problemas de integridade detectados");
            }
            
            System.out.println("\n=================================================\n");
            
        } catch (Exception e) {
            System.err.println("\n✗ Erro durante o teste:");
            e.printStackTrace();
        }
    }
    
    /**
     * Calcula checksums MD5 de todos os arquivos recursivamente
     */
    private static Map<String, String> calcularChecksums(File dir, String relativePath) throws Exception {
        Map<String, String> checksums = new HashMap<>();
        File[] files = dir.listFiles();
        
        if (files != null) {
            for (File file : files) {
                String currentPath = relativePath.isEmpty() ? file.getName() : relativePath + "/" + file.getName();
                
                if (file.isDirectory()) {
                    checksums.putAll(calcularChecksums(file, currentPath));
                } else {
                    byte[] data = Files.readAllBytes(file.toPath());
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] hash = md.digest(data);
                    
                    StringBuilder hexString = new StringBuilder();
                    for (byte b : hash) {
                        String hex = Integer.toHexString(0xff & b);
                        if (hex.length() == 1) hexString.append('0');
                        hexString.append(hex);
                    }
                    
                    checksums.put(currentPath, hexString.toString());
                }
            }
        }
        
        return checksums;
    }
    
    /**
     * Verifica se os checksums batem
     */
    private static boolean verificarIntegridade(Map<String, String> originais, 
                                                 Map<String, String> restaurados, 
                                                 String algoritmo) {
        boolean ok = true;
        int arquivosVerificados = 0;
        
        // Verificar se todos os arquivos originais foram restaurados
        for (Map.Entry<String, String> entry : originais.entrySet()) {
            String arquivo = entry.getKey();
            String checksumOriginal = entry.getValue();
            String checksumRestaurado = restaurados.get(arquivo);
            
            arquivosVerificados++;
            
            if (checksumRestaurado == null) {
                System.out.println("   ✗ Arquivo faltando: " + arquivo);
                ok = false;
            } else if (!checksumOriginal.equals(checksumRestaurado)) {
                System.out.println("   ✗ Checksum diferente: " + arquivo);
                System.out.println("      Original:   " + checksumOriginal);
                System.out.println("      Restaurado: " + checksumRestaurado);
                ok = false;
            }
        }
        
        // Verificar se não há arquivos extras
        for (String arquivo : restaurados.keySet()) {
            if (!originais.containsKey(arquivo)) {
                System.out.println("   ✗ Arquivo extra (não estava no original): " + arquivo);
                ok = false;
            }
        }
        
        if (ok) {
            System.out.println("   ✓ INTEGRIDADE VERIFICADA!");
            System.out.println("   - " + arquivosVerificados + " arquivos verificados");
            System.out.println("   - Todos os checksums MD5 conferem");
            System.out.println("   - Nenhum dado foi perdido ou corrompido");
        } else {
            System.out.println("   ✗ PROBLEMAS DE INTEGRIDADE DETECTADOS!");
        }
        
        return ok;
    }
    
    /**
     * Remove diretório recursivamente
     */
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
}
