package view;

import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        int opcao;

        try {
            do {
                System.out.println("\n\nAEDsIII");
                System.out.println("-------");
                System.out.println("> Início");
                System.out.println("\n1 - Clientes");
                System.out.println("2 - Bibliotecas");
                System.out.println("3 - Jogos");
                System.out.println("4 - Compras");
                System.out.println("5 - Busca por Preço (Índice)");
                System.out.println("6 - Pesquisar por padrão (KMP / BM)");
                System.out.println("0 - Sair");

                System.out.print("\nOpção: ");
                try {
                    opcao = Integer.parseInt(console.nextLine());
                } catch (NumberFormatException e) {
                    opcao = -1;
                }

                switch (opcao) {
                    case 1:
                        MenuClientes menuClientes = new MenuClientes(console);
                        menuClientes.menu();
                        break;
                    case 2:
                        MenuBiblioteca menuBiblioteca = new MenuBiblioteca(console);
                        menuBiblioteca.menu();
                        break;
                    case 3:
                        MenuJogos menuJogos = new MenuJogos(console);
                        menuJogos.menu();
                        break;
                    case 4:
                        MenuCompras menuCompras = new MenuCompras(console);
                        menuCompras.menu();
                        break;
                    case 5:
                        MenuBuscaPreco menuBuscaPreco = new MenuBuscaPreco(console);
                        menuBuscaPreco.menu();
                        break;
                    case 6:
                        MenuPesquisaPadrao menuPesquisa = new MenuPesquisaPadrao(console);
                        menuPesquisa.menu();
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }
            } while (opcao != 0);

        } catch (Exception e) {
            System.err.println("Erro fatal no sistema:");
            e.printStackTrace();
        } finally {
            console.close();
        }
    }
}
