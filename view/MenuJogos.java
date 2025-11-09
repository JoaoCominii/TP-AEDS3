package view;

import dao.JogoDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.Jogo;
import util.OutputFormatter;

public class MenuJogos {
    private JogoDAO jogoDAO;
    private Scanner console;

    public MenuJogos(Scanner console) throws Exception {
        this.console = console;
        this.jogoDAO = new JogoDAO();
    }

    public void menu() {
        int opcao;
        do {
            System.out.println("\n\nJogos");
            System.out.println("-----");
            System.out.println("1 - Buscar");
            System.out.println("2 - Incluir");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("5 - Listar");
            System.out.println("6 - Reconstruir índice de preços");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            try {
                opcao = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }
            switch (opcao) {
                case 1: buscar(); break;
                case 2: incluir(); break;
                case 3: alterar(); break;
                case 4: excluir(); break;
                case 5: listar(); break;
                case 6: reconstruirIndice(); break;
                case 0: break;
                default: System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void buscar() {
        System.out.print("ID do jogo: ");
        int id;
        try { id = Integer.parseInt(console.nextLine()); } catch (NumberFormatException e) { System.out.println("ID inválido."); return; }
        try {
            Jogo j = jogoDAO.buscar(id);
            if (j != null) System.out.println(OutputFormatter.formatJogo(j));
            else System.out.println("Jogo não encontrado.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar jogo.");
            e.printStackTrace();
        }
    }

    private void incluir() {
        try {
            System.out.println("\nInclusão de jogo");
            System.out.print("Nome: "); String nome = console.nextLine();
            System.out.print("Descrição: "); String descricao = console.nextLine();
            System.out.print("Tamanho (ex: 45MB): "); String tamanho = console.nextLine();
            System.out.print("Nota (0-5): "); short nota = 0;
            try { int nn = Integer.parseInt(console.nextLine()); if (nn < 0) nn = 0; if (nn > 5) nn = 5; nota = (short) nn; } catch (Exception ex) { nota = 0; }
            System.out.print("Plataforma: "); String plataforma = console.nextLine();
            System.out.print("Preco (ex: 59.90): "); double preco = 0.0;
            try { preco = Double.parseDouble(console.nextLine()); } catch (Exception ex) { preco = 0.0; }
            System.out.print("Generos (separe por ,): "); String generosLine = console.nextLine();
            List<String> generos = new ArrayList<>();
            if (!generosLine.trim().isEmpty()) {
                String[] parts = generosLine.split(",");
                for (String p : parts) generos.add(p.trim());
            }
            System.out.print("Classificação etária (ex: L, 10, 12): "); String classificacao = console.nextLine();

            Jogo j = new Jogo(nome, descricao, tamanho, nota, plataforma, preco, generos, classificacao);
            if (jogoDAO.incluir(j)) System.out.println("Jogo incluído com sucesso.");
            else System.out.println("Erro ao incluir jogo.");
        } catch (Exception e) {
            System.out.println("Erro ao incluir jogo.");
            e.printStackTrace();
        }
    }

    private void alterar() {
        System.out.print("ID do jogo a ser alterado: ");
        int id;
        try { id = Integer.parseInt(console.nextLine()); } catch (NumberFormatException e) { System.out.println("ID inválido."); return; }
        try {
            Jogo j = jogoDAO.buscar(id);
            if (j == null) { System.out.println("Jogo não encontrado."); return; }
            System.out.print("Novo nome (vazio para manter): "); String nome = console.nextLine(); if (!nome.isEmpty()) j.setNome(nome);
            System.out.print("Nova descrição (vazio para manter): "); String descricao = console.nextLine(); if (!descricao.isEmpty()) j.setDescricao(descricao);
            System.out.print("Novo tamanho (vazio para manter): "); String tamanho = console.nextLine(); if (!tamanho.isEmpty()) j.setTamanho(tamanho);
            System.out.print("Nova nota (vazio para manter): "); String notaS = console.nextLine(); if (!notaS.isEmpty()) { try { int nn = Integer.parseInt(notaS); if (nn < 0) nn = 0; if (nn > 5) nn = 5; j.setNota((short) nn); } catch (Exception ex) {} }
            System.out.print("Nova plataforma (vazio para manter): "); String plataforma = console.nextLine(); if (!plataforma.isEmpty()) j.setPlataforma(plataforma);
            System.out.print("Novo preco (vazio para manter): "); String precoS = console.nextLine(); if (!precoS.isEmpty()) { try { j.setPreco(Double.parseDouble(precoS)); } catch (Exception ex) {} }
            System.out.print("Novos generos (separe por , - vazio para manter): "); String gens = console.nextLine(); if (!gens.isEmpty()) { List<String> generos = new ArrayList<>(); for (String p : gens.split(",")) generos.add(p.trim()); j.setGeneros(generos); }
            System.out.print("Nova classificação etária (vazio para manter): "); String classif = console.nextLine(); if (!classif.isEmpty()) j.setClassificacaoEtaria(classif);

            if (jogoDAO.alterar(j)) System.out.println("Jogo alterado com sucesso.");
            else System.out.println("Erro ao alterar jogo.");
        } catch (Exception e) {
            System.out.println("Erro ao alterar jogo.");
            e.printStackTrace();
        }
    }

    private void excluir() {
        System.out.print("ID do jogo a ser excluído: ");
        int id;
        try { id = Integer.parseInt(console.nextLine()); } catch (NumberFormatException e) { System.out.println("ID inválido."); return; }
        try {
            Jogo j = jogoDAO.buscar(id);
            if (j == null) { System.out.println("Jogo não encontrado."); return; }
            System.out.print("Confirma exclusão? (S/N): "); String resp = console.nextLine();
            if (!resp.isEmpty() && (resp.charAt(0) == 'S' || resp.charAt(0) == 's')) {
                if (jogoDAO.excluir(id)) System.out.println("Jogo excluído com sucesso.");
                else System.out.println("Erro ao excluir jogo.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao excluir jogo.");
            e.printStackTrace();
        }
    }

    private void reconstruirIndice() {
        System.out.print("Tem certeza que deseja reconstruir o índice de preços? (S/N): ");
        String resp = console.nextLine();
        if (resp.equalsIgnoreCase("S")) {
            System.out.println("Reconstruindo... aguarde.");
            jogoDAO.reconstruirIndicePreco();
            System.out.println("Índice reconstruído com sucesso.");
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private void listar() {
        try {
            System.out.println("Listagem de jogos:");
            for (int id = 1; id <= 1000; id++) {
                Jogo j = jogoDAO.buscar(id);
                if (j == null) continue;
                System.out.println(OutputFormatter.formatJogo(j));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar jogos.");
            e.printStackTrace();
        }
    }
}