package view;

import dao.JogoDAO;
import java.util.List;
import java.util.Scanner;
import model.Jogo;
import util.StringSearch;

public class MenuPesquisaPadrao {
    private Scanner console;

    public MenuPesquisaPadrao(Scanner console) {
        this.console = console;
    }

    public void menu() {
        try {
            System.out.println("\nPesquisar por padrão (KMP / BM)");
            System.out.println("Escolha o algoritmo:");
            System.out.println("1 - KMP");
            System.out.println("2 - Boyer-Moore (bad character)");
            System.out.print("Opção: ");
            int opc;
            try { opc = Integer.parseInt(console.nextLine()); } catch (Exception e) { System.out.println("Opção inválida."); return; }
            System.out.print("Padrão a procurar: ");
            String pattern = console.nextLine();
            if (pattern == null || pattern.isEmpty()) { System.out.println("Padrão vazio."); return; }

            System.out.println("Opção de sensibilidade a maiúsculas:");
            System.out.println("1 - Case-sensitive");
            System.out.println("2 - Case-insensitive");
            System.out.print("Opção: ");
            int caseOpt;
            try { caseOpt = Integer.parseInt(console.nextLine()); } catch (Exception e) { caseOpt = 1; }
            boolean caseInsensitive = (caseOpt == 2);
            if (caseInsensitive) pattern = pattern.toLowerCase();

            System.out.println("Procurando em jogos (nome + descricao)...\n");
            JogoDAO jogoDAO = new JogoDAO();
            List<Jogo> jogos = jogoDAO.listarTodos();
            int found = 0;
            for (Jogo j : jogos) {
                boolean match = false;
                String nome = j.getNome() == null ? "" : j.getNome();
                String desc = j.getDescricao() == null ? "" : j.getDescricao();
                String nomeForSearch = caseInsensitive ? nome.toLowerCase() : nome;
                String descForSearch = caseInsensitive ? desc.toLowerCase() : desc;
                switch (opc) {
                    case 1:
                        match = StringSearch.kmpContains(nomeForSearch, pattern) || StringSearch.kmpContains(descForSearch, pattern);
                        break;
                    case 2:
                        match = StringSearch.bmContains(nomeForSearch, pattern) || StringSearch.bmContains(descForSearch, pattern);
                        break;
                    default:
                        System.out.println("Algoritmo inválido.");
                        return;
                }
                if (match) {
                    System.out.println(j.toString());
                    found++;
                }
            }
            System.out.println("\nTotal de registros encontrados: " + found);
        } catch (Exception e) {
            System.out.println("Erro na pesquisa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
