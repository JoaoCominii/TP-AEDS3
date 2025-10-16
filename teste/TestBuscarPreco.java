package teste;

import dao.JogoDAO;
import model.Jogo;
import util.OutputFormatter;
import java.util.List;
import java.util.Scanner;

public class TestBuscarPreco {
    public static void main(String[] args) {
        try {
            JogoDAO jogoDAO = new JogoDAO();
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
                System.out.println("\n=== TESTE BUSCA POR PREÇO (Árvore B+) ===");
                System.out.println("1 - Buscar por preço exato");
                System.out.println("2 - Buscar por faixa de preço");
                System.out.println("3 - Jogos em promoção (até R$ 30)");
                System.out.println("4 - Jogos premium (R$ 100+)");
                System.out.println("5 - Listar todos ordenados por preço");
                System.out.println("6 - Relatório por faixas de preço");
                System.out.println("0 - Sair");
                System.out.print("Escolha: ");
                
                int opcao = scanner.nextInt();
                
                switch (opcao) {
                    case 1:
                        System.out.print("Digite o preço: R$ ");
                        double preco = scanner.nextDouble();
                        List<Jogo> jogosPreco = jogoDAO.buscarPorPreco(preco);
                        exibirResultados("Jogos com preço R$ " + preco, jogosPreco);
                        break;
                        
                    case 2:
                        System.out.print("Preço mínimo: R$ ");
                        double precoMin = scanner.nextDouble();
                        System.out.print("Preço máximo: R$ ");
                        double precoMax = scanner.nextDouble();
                        List<Jogo> jogosFaixa = jogoDAO.buscarPorFaixaPreco(precoMin, precoMax);
                        exibirResultados("Jogos entre R$ " + precoMin + " e R$ " + precoMax, jogosFaixa);
                        break;
                        
                    case 3:
                        List<Jogo> promocao = jogoDAO.buscarJogosPromocao();
                        exibirResultados("Jogos em promoção", promocao);
                        break;
                        
                    case 4:
                        List<Jogo> premium = jogoDAO.buscarJogosPremium();
                        exibirResultados("Jogos premium", premium);
                        break;
                        
                    case 5:
                        List<Jogo> ordenados = jogoDAO.listarOrdenadosPorPreco();
                        exibirResultados("Todos os jogos ordenados por preço", ordenados);
                        break;
                        
                    case 6:
                        jogoDAO.relatorioPorPreco();
                        break;
                        
                    case 0:
                        System.out.println("Saindo...");
                        return;
                        
                    default:
                        System.out.println("Opção inválida!");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void exibirResultados(String titulo, List<Jogo> jogos) {
        System.out.println("\n" + titulo + ":");
        System.out.println("Encontrados: " + jogos.size() + " jogo(s)");
        
        if (jogos.isEmpty()) {
            System.out.println("Nenhum jogo encontrado.");
        } else {
            for (Jogo jogo : jogos) {
                System.out.println(OutputFormatter.formatJogo(jogo));
            }
        }
        System.out.println();
    }
}