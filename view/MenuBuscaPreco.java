package view;

import dao.JogoDAO;
import java.util.List;
import java.util.Scanner;
import model.Jogo;
import util.OutputFormatter;

public class MenuBuscaPreco {
    private JogoDAO jogoDAO;
    private Scanner console;

    public MenuBuscaPreco(Scanner console) throws Exception {
        this.console = console;
        this.jogoDAO = new JogoDAO();
    }

    public void menu() {
        int opcao;
        do {
            System.out.println("\n\nBusca de Jogos por Preço");
            System.out.println("--------------------------");
            System.out.println("1 - Buscar por preço exato");
            System.out.println("2 - Buscar por faixa de preço");
            System.out.println("3 - Jogos em promoção (até R$ 30)");
            System.out.println("4 - Jogos premium (R$ 100+)");
            System.out.println("5 - Listar todos ordenados por preço");
            System.out.println("6 - Relatório por faixas de preço");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            try {
                opcao = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    buscarPorPrecoExato();
                    break;
                case 2:
                    buscarPorFaixaDePreco();
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
                    jogoDAO.exibirEstatisticasPreco();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void buscarPorPrecoExato() {
        System.out.print("Digite o preço: R$ ");
        try {
            double preco = Double.parseDouble(console.nextLine());
            List<Jogo> jogosPreco = jogoDAO.buscarPorPreco(preco);
            exibirResultados("Jogos com preço R$ " + preco, jogosPreco);
        } catch (NumberFormatException e) {
            System.out.println("Preço inválido.");
        }
    }

    private void buscarPorFaixaDePreco() {
        try {
            System.out.print("Preço mínimo: R$ ");
            double precoMin = Double.parseDouble(console.nextLine());
            System.out.print("Preço máximo: R$ ");
            double precoMax = Double.parseDouble(console.nextLine());
            List<Jogo> jogosFaixa = jogoDAO.buscarPorFaixaPreco(precoMin, precoMax);
            exibirResultados("Jogos entre R$ " + precoMin + " e R$ " + precoMax, jogosFaixa);
        } catch (NumberFormatException e) {
            System.out.println("Preço inválido.");
        }
    }

    private void exibirResultados(String titulo, List<Jogo> jogos) {
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
