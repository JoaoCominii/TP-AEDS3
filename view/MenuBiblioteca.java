package view;

import dao.BibliotecaDAO;
import dao.BibliotecaJogoDAO;
import java.util.List;
import java.util.Scanner;
import model.Biblioteca;
import model.BibliotecaJogo;
import model.Jogo;
import util.OutputFormatter;

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
            System.out.println("6 - Gerenciar Jogos da Biblioteca");
            System.out.println("7 - Buscar por Cliente");
            System.out.println("8 - Reconstruir Índice Cliente->Biblioteca");
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
                case 6:
                    menuJogosBiblioteca();
                    break;
                case 7:
                    buscarPorCliente();
                    break;
                case 8:
                    reconstruirIndice();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void menuJogosBiblioteca() {
        System.out.print("\nID da Biblioteca para gerenciar os jogos: ");
        int bibliotecaId;
        try {
            bibliotecaId = Integer.parseInt(console.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        try {
            Biblioteca b = bibliotecaDAO.buscar(bibliotecaId);
            if (b == null) {
                System.out.println("Biblioteca não encontrada.");
                return;
            }
            
            int op;
            do {
                System.out.println("\nGerenciando Jogos da Biblioteca: " + b.getNome());
                System.out.println("1 - Adicionar Jogo");
                System.out.println("2 - Listar Jogos");
                System.out.println("3 - Remover Jogo (relação)");
                System.out.println("4 - Reconstruir Índice Biblioteca->Jogos");
                System.out.println("0 - Voltar");
                System.out.print("Opção: ");
                op = Integer.parseInt(console.nextLine());

                switch(op) {
                    case 1:
                        adicionarJogoNaBiblioteca(bibliotecaId);
                        break;
                    case 2:
                        listarJogosDaBiblioteca(bibliotecaId);
                        break;
                    case 3:
                        removerJogoDaBiblioteca();
                        break;
                    case 4:
                        new BibliotecaJogoDAO().reconstruirIndice();
                        System.out.println("Índice reconstruído.");
                        break;
                }
            } while (op != 0);

        } catch (Exception e) {
            System.out.println("Erro ao gerenciar jogos da biblioteca: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void adicionarJogoNaBiblioteca(int bibliotecaId) throws Exception {
        System.out.print("ID do Jogo a ser adicionado: ");
        int jogoId = Integer.parseInt(console.nextLine());
        // Aqui seria bom validar se o jogo existe
        BibliotecaJogoDAO bjDAO = new BibliotecaJogoDAO();
        BibliotecaJogo bj = new BibliotecaJogo(bibliotecaId, jogoId);
        if (bjDAO.incluir(bj)) {
            System.out.println("Jogo adicionado à biblioteca com sucesso.");
        } else {
            System.out.println("Falha ao adicionar jogo.");
        }
    }

    private void listarJogosDaBiblioteca(int bibliotecaId) throws Exception {
        BibliotecaJogoDAO bjDAO = new BibliotecaJogoDAO();
        List<Jogo> jogos = bjDAO.buscarJogosDaBiblioteca(bibliotecaId);
        if (jogos.isEmpty()) {
            System.out.println("Nenhum jogo nesta biblioteca.");
        } else {
            System.out.println("Jogos na biblioteca:");
            for (Jogo j : jogos) {
                System.out.println(OutputFormatter.formatJogo(j));
            }
        }
    }

    private void removerJogoDaBiblioteca() throws Exception {
        System.out.print("ID da Relação (BibliotecaJogo) a ser removida: ");
        int relacaoId = Integer.parseInt(console.nextLine());
        BibliotecaJogoDAO bjDAO = new BibliotecaJogoDAO();
        if (bjDAO.excluir(relacaoId)) {
            System.out.println("Relação removida com sucesso.");
        } else {
            System.out.println("Falha ao remover relação.");
        }
    }
    
    private void buscarPorCliente() {
        System.out.print("\nID do cliente: ");
        int id;
        try {
            id = Integer.parseInt(console.nextLine());
            List<Biblioteca> bibliotecas = bibliotecaDAO.buscarPorCliente(id);
            if (bibliotecas != null && !bibliotecas.isEmpty()) {
                System.out.println("Bibliotecas encontradas: " + bibliotecas.size());
                for(Biblioteca b : bibliotecas) {
                    System.out.println(OutputFormatter.formatBiblioteca(b));
                }
            } else {
                System.out.println("Nenhuma biblioteca encontrada para este cliente.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID de cliente inválido.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar bibliotecas por cliente.");
            e.printStackTrace();
        }
    }

    private void reconstruirIndice() {
        System.out.print("Confirma a reconstrução do índice Cliente->Biblioteca? (S/N): ");
        String resp = console.nextLine();
        if (resp.equalsIgnoreCase("S")) {
            bibliotecaDAO.reconstruirIndiceCliente();
        } else {
            System.out.println("Operação cancelada.");
        }
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
