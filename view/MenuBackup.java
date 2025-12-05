package view;

import util.BackupManager;
import util.BackupManager.BackupResult;
import util.BackupManager.CompressionAlgorithm;

import java.util.List;
import java.util.Scanner;

public class MenuBackup {
    private Scanner console;

    public MenuBackup(Scanner console) {
        this.console = console;
    }

    public void menu() {
        int opcao;

        do {
            System.out.println("\n\n=================================");
            System.out.println("        BACKUP E COMPRESSÃO");
            System.out.println("=================================");
            System.out.println("\n1 - Criar backup com Huffman");
            System.out.println("2 - Criar backup com LZW");
            System.out.println("3 - Restaurar backup");
            System.out.println("4 - Listar backups disponíveis");
            System.out.println("5 - Comparar algoritmos");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            try {
                switch (opcao) {
                    case 1:
                        criarBackup(CompressionAlgorithm.HUFFMAN);
                        break;
                    case 2:
                        criarBackup(CompressionAlgorithm.LZW);
                        break;
                    case 3:
                        restaurarBackup();
                        break;
                    case 4:
                        listarBackups();
                        break;
                    case 5:
                        compararAlgoritmos();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace();
            }

        } while (opcao != 0);
    }

    private void criarBackup(CompressionAlgorithm algorithm) {
        try {
            System.out.println("\n=== Criando Backup ===");
            System.out.println("Algoritmo: " + algorithm);
            System.out.println("Coletando arquivos...");

            long startTime = System.currentTimeMillis();
            BackupResult result = BackupManager.createBackup(algorithm);
            long duration = System.currentTimeMillis() - startTime;

            System.out.println("\n" + result);
            System.out.println("Tempo de execução: " + duration + " ms");

            System.out.println("\n✓ Backup criado com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao criar backup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void restaurarBackup() {
        try {
            List<String> backups = BackupManager.listBackups();

            if (backups.isEmpty()) {
                System.out.println("\nNenhum backup disponível.");
                return;
            }

            System.out.println("\n=== Backups Disponíveis ===");
            for (int i = 0; i < backups.size(); i++) {
                System.out.println((i + 1) + " - " + backups.get(i));
            }

            System.out.print("\nEscolha o backup (1-" + backups.size() + "): ");
            int escolha = Integer.parseInt(console.nextLine());

            if (escolha < 1 || escolha > backups.size()) {
                System.out.println("Opção inválida!");
                return;
            }

            String backupFile = backups.get(escolha - 1);
            String backupPath = "backups/" + backupFile;

            System.out.println("\n⚠ ATENÇÃO: Esta operação irá sobrescrever todos os arquivos de dados!");
            System.out.print("Deseja continuar? (S/N): ");
            String confirmacao = console.nextLine().trim().toUpperCase();

            if (!confirmacao.equals("S")) {
                System.out.println("Operação cancelada.");
                return;
            }

            System.out.println("\nRestaurando backup...");
            CompressionAlgorithm algorithm = BackupManager.detectAlgorithm(backupFile);
            
            long startTime = System.currentTimeMillis();
            BackupManager.restoreBackup(backupPath, algorithm);
            long duration = System.currentTimeMillis() - startTime;

            System.out.println("Tempo de execução: " + duration + " ms");
            System.out.println("\n✓ Backup restaurado com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao restaurar backup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listarBackups() {
        try {
            List<String> backups = BackupManager.listBackups();

            if (backups.isEmpty()) {
                System.out.println("\nNenhum backup disponível.");
                return;
            }

            System.out.println("\n=== Backups Disponíveis ===");
            for (int i = 0; i < backups.size(); i++) {
                String backup = backups.get(i);
                CompressionAlgorithm algorithm = BackupManager.detectAlgorithm(backup);
                System.out.println((i + 1) + " - " + backup + " [" + algorithm + "]");
            }

        } catch (Exception e) {
            System.err.println("Erro ao listar backups: " + e.getMessage());
        }
    }

    private void compararAlgoritmos() {
        try {
            System.out.println("\n=== Comparação de Algoritmos ===");
            System.out.println("Criando backups com ambos os algoritmos...\n");

            // Huffman
            System.out.println("--- HUFFMAN ---");
            long startHuffman = System.currentTimeMillis();
            BackupResult huffmanResult = BackupManager.createBackup(CompressionAlgorithm.HUFFMAN);
            long durationHuffman = System.currentTimeMillis() - startHuffman;

            System.out.println("Tamanho original: " + String.format("%,d", huffmanResult.originalSize) + " bytes");
            System.out.println("Tamanho comprimido: " + String.format("%,d", huffmanResult.compressedSize) + " bytes");
            System.out.println("Taxa de compressão: " + String.format("%.2f%%", huffmanResult.compressionRatio));
            System.out.println("Tempo de execução: " + durationHuffman + " ms");

            // LZW
            System.out.println("\n--- LZW ---");
            long startLZW = System.currentTimeMillis();
            BackupResult lzwResult = BackupManager.createBackup(CompressionAlgorithm.LZW);
            long durationLZW = System.currentTimeMillis() - startLZW;

            System.out.println("Tamanho original: " + String.format("%,d", lzwResult.originalSize) + " bytes");
            System.out.println("Tamanho comprimido: " + String.format("%,d", lzwResult.compressedSize) + " bytes");
            System.out.println("Taxa de compressão: " + String.format("%.2f%%", lzwResult.compressionRatio));
            System.out.println("Tempo de execução: " + durationLZW + " ms");

            // Comparação
            System.out.println("\n--- COMPARAÇÃO ---");
            if (huffmanResult.compressionRatio > lzwResult.compressionRatio) {
                System.out.println("✓ Huffman obteve melhor taxa de compressão");
                System.out.println("  Diferença: " + String.format("%.2f%%", 
                    huffmanResult.compressionRatio - lzwResult.compressionRatio));
            } else if (lzwResult.compressionRatio > huffmanResult.compressionRatio) {
                System.out.println("✓ LZW obteve melhor taxa de compressão");
                System.out.println("  Diferença: " + String.format("%.2f%%", 
                    lzwResult.compressionRatio - huffmanResult.compressionRatio));
            } else {
                System.out.println("Ambos obtiveram a mesma taxa de compressão");
            }

            if (durationHuffman < durationLZW) {
                System.out.println("✓ Huffman foi mais rápido");
                System.out.println("  Diferença: " + (durationLZW - durationHuffman) + " ms");
            } else if (durationLZW < durationHuffman) {
                System.out.println("✓ LZW foi mais rápido");
                System.out.println("  Diferença: " + (durationHuffman - durationLZW) + " ms");
            } else {
                System.out.println("Ambos tiveram o mesmo tempo de execução");
            }

        } catch (Exception e) {
            System.err.println("Erro ao comparar algoritmos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
