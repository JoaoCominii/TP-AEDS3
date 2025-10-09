package view;

import dao.BibliotecaDAO;
import model.Biblioteca;
import util.OutputFormatter;
import java.util.Scanner;

public class MenuBiblioteca {
    private BibliotecaDAO bibliotecaDAO;
    private Scanner console;

    public MenuBiblioteca(Scanner console) throws Exception {
        this.console = console;
        this.bibliotecaDAO = new BibliotecaDAO();
    }

    public void menu() {
        int opcao;
        do {
            System.out.println("\n\nBiblioteca");
            System.out.println("---------");
            System.out.println("\n1 - Buscar");
            System.out.println("2 - Incluir");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("5 - Listar");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    buscarBiblioteca();
                    break;
                case 2:
                    incluirBiblioteca();
                    break;
                case 3:
                    alterarBiblioteca();
                    break;
                case 4:
                    excluirBiblioteca();
                    break;
                case 5:
                    listarBibliotecas();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void buscarBiblioteca() {
        System.out.print("\nID da biblioteca: ");
        int id;
        try {
            id = Integer.parseInt(console.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }
        try {
            Biblioteca b = bibliotecaDAO.buscar(id);
            if (b != null) System.out.println(OutputFormatter.formatBiblioteca(b));
            else System.out.println("Biblioteca não encontrada.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar biblioteca.");
            e.printStackTrace();
        }
    }

    private void incluirBiblioteca() {
        System.out.println("\nInclusão de biblioteca");
        System.out.print("Cliente ID (vazio para nenhum): "); int clienteId = -1; String cidLine = console.nextLine(); if (!cidLine.isEmpty()) { try { clienteId = Integer.parseInt(cidLine); } catch (Exception ex) { clienteId = -1; } }
        System.out.print("Nome: ");
        String nome = console.nextLine();
        System.out.print("Descrição: ");
        String descricao = console.nextLine();
        System.out.print("Status: ");
        String status = console.nextLine();

        try {
            Biblioteca b = new Biblioteca(nome, descricao, status);
            b.setClienteId(clienteId);
            if (bibliotecaDAO.incluirComValidacao(b)) System.out.println("Biblioteca incluída com sucesso.");
            else System.out.println("Erro ao incluir biblioteca.");
        } catch (Exception e) {
            System.out.println("Erro ao incluir biblioteca.");
            e.printStackTrace();
        }
    }

    private void alterarBiblioteca() {
        System.out.print("\nID da biblioteca a ser alterada: ");
        int id;
        try {
            id = Integer.parseInt(console.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        try {
            Biblioteca b = bibliotecaDAO.buscar(id);
            if (b == null) {
                System.out.println("Biblioteca não encontrada.");
                return;
            }

            System.out.print("Novo nome (vazio para manter): ");
            String nome = console.nextLine();
            if (!nome.isEmpty()) b.setNome(nome);

            System.out.print("Nova descrição (vazio para manter): ");
            String descricao = console.nextLine();
            if (!descricao.isEmpty()) b.setDescricao(descricao);

            System.out.print("Cliente ID (vazio para manter): "); String cid = console.nextLine(); if (!cid.isEmpty()) { try { b.setClienteId(Integer.parseInt(cid)); } catch (Exception ex) {} }
            System.out.print("Novo status (vazio para manter): ");
            String status = console.nextLine();
            if (!status.isEmpty()) b.setStatus(status);

            if (bibliotecaDAO.alterarComValidacao(b)) System.out.println("Biblioteca alterada com sucesso.");
            else System.out.println("Erro ao alterar biblioteca.");
        } catch (Exception e) {
            System.out.println("Erro ao alterar biblioteca.");
            e.printStackTrace();
        }
    }

    private void excluirBiblioteca() {
        System.out.print("\nID da biblioteca a ser excluída: ");
        int id;
        try {
            id = Integer.parseInt(console.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        try {
            Biblioteca b = bibliotecaDAO.buscar(id);
            if (b == null) {
                System.out.println("Biblioteca não encontrada.");
                return;
            }
            System.out.print("Confirma exclusão? (S/N): ");
            String resp = console.nextLine();
            if (!resp.isEmpty() && (resp.charAt(0) == 'S' || resp.charAt(0) == 's')) {
                if (bibliotecaDAO.excluir(id)) System.out.println("Biblioteca excluída com sucesso.");
                else System.out.println("Erro ao excluir biblioteca.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao excluir biblioteca.");
            e.printStackTrace();
        }
    }

    private void listarBibliotecas() {
        try {
            System.out.println("Listagem de bibliotecas:");
            for (int id = 1; id <= 1000; id++) {
                try {
                    model.Biblioteca b = bibliotecaDAO.buscar(id);
                    if (b != null) System.out.println(OutputFormatter.formatBiblioteca(b));
                } catch (Exception e) {
                    // ignora ids inexistentes
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar bibliotecas.");
            e.printStackTrace();
        }
    }
}
