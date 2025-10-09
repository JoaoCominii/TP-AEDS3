package view;

import dao.ClienteDAO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import model.Cliente;
import util.OutputFormatter;

public class MenuClientes {
    private ClienteDAO clienteDAO;
    private Scanner console;

    // Recebe o Scanner do Principal para evitar multiplos scanners no System.in
    public MenuClientes(Scanner console) throws Exception {
        this.console = console;
        clienteDAO = new ClienteDAO();
    }

    public void menu() {
        int opcao;
        do {
            System.out.println("\n\nAEDsIII");
            System.out.println("-------");
            System.out.println("> Início > Clientes");
            System.out.println("\n1 - Buscar");
            System.out.println("2 - Incluir");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    buscarCliente();
                    break;
                case 2:
                    incluirCliente();
                    break;
                case 3:
                    alterarCliente();
                    break;
                case 4:
                    excluirCliente();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 0);
    }

    private void buscarCliente() {
        System.out.print("\nID do cliente: ");
        int id;
        try {
            id = Integer.parseInt(console.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }
        try {
            Cliente cliente = clienteDAO.buscarCliente(id);
            if (cliente != null) {
                System.out.println(OutputFormatter.formatCliente(cliente));
            } else {
                System.out.println("Cliente não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar cliente.");
            e.printStackTrace();
        }
    }

    private void incluirCliente() {
        System.out.println("\nInclusão de cliente");

        System.out.print("\nNome: ");
        String nome = console.nextLine();
        System.out.print("Email: ");
        String email = console.nextLine();
        System.out.print("Senha: ");
        String senha = console.nextLine();
        // captura a data de cadastro automaticamente como hoje
        LocalDate cadastro = LocalDate.now();

        try {
            Cliente cliente = new Cliente(nome, email, senha, cadastro);
            if (clienteDAO.incluirCliente(cliente)) {
                System.out.println("Cliente incluído com sucesso.");
                System.out.println(OutputFormatter.formatCliente(cliente));
            } else {
                System.out.println("Erro ao incluir cliente.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao incluir cliente.");
            e.printStackTrace();
        }
    }

    private void alterarCliente() {
        System.out.print("\nID do cliente a ser alterado: ");
        int id;
        try {
            id = Integer.parseInt(console.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        try {
            Cliente cliente = clienteDAO.buscarCliente(id);
            if (cliente == null) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            System.out.print("\nNovo nome (vazio para manter): ");
            String nome = console.nextLine();
            if (!nome.isEmpty()) cliente.setNome(nome);

            System.out.print("Novo email (vazio para manter): ");
            String email = console.nextLine();
            if (!email.isEmpty()) cliente.setEmail(email);

            System.out.print("Nova senha (vazio para manter): ");
            String senha = console.nextLine();
            if (!senha.isEmpty()) cliente.setSenha(senha);

            System.out.print("Nova data de cadastro (DD/MM/AAAA, vazio para manter): ");
            String dataStr = console.nextLine();
            if (!dataStr.isEmpty()) {
                try {
                    cliente.setCadastro(LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } catch (Exception ex) {
                    System.out.println("Data inválida. Alteração cancelada.");
                    return;
                }
            }

            if (clienteDAO.alterarCliente(cliente)) {
                System.out.println("Cliente alterado com sucesso.");
                System.out.println(OutputFormatter.formatCliente(cliente));
            } else {
                System.out.println("Erro ao alterar cliente.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao alterar cliente.");
            e.printStackTrace();
        }
    }

    private void excluirCliente() {
        System.out.print("\nID do cliente a ser excluído: ");
        int id;
        try {
            id = Integer.parseInt(console.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        try {
            Cliente cliente = clienteDAO.buscarCliente(id);
            if (cliente == null) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            System.out.print("Confirma exclusão? (S/N): ");
            String respStr = console.nextLine();
            char resp = respStr.isEmpty() ? 'N' : respStr.charAt(0);
            if (resp == 'S' || resp == 's') {
                if (clienteDAO.excluirCliente(id)) {
                    System.out.println("Cliente excluído com sucesso.");
                } else {
                    System.out.println("Erro ao excluir cliente.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao excluir cliente.");
            e.printStackTrace();
        }
    }
}
