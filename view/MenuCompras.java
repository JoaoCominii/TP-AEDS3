package view;

import dao.CompraDAO;
import model.Compra;
import util.OutputFormatter;

import java.time.LocalDate;
import java.util.Scanner;

public class MenuCompras {
    private CompraDAO compraDAO;
    private Scanner console;

    public MenuCompras(Scanner console) throws Exception {
        this.console = console;
        this.compraDAO = new CompraDAO();
    }

    public void menu() {
        int opcao;
        do {
            System.out.println("\n\nCompras");
            System.out.println("------");
            System.out.println("1 - Buscar");
            System.out.println("2 - Incluir");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("5 - Listar");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            try { opcao = Integer.parseInt(console.nextLine()); } catch (NumberFormatException e) { opcao = -1; }
            switch (opcao) {
                case 1: buscar(); break;
                case 2: incluir(); break;
                case 3: alterar(); break;
                case 4: excluir(); break;
                case 5: listar(); break;
                case 0: break;
                default: System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void buscar() {
        System.out.print("ID da compra: ");
        int id;
        try { id = Integer.parseInt(console.nextLine()); } catch (NumberFormatException e) { System.out.println("ID inválido."); return; }
        try {
            Compra c = compraDAO.buscar(id);
            if (c != null) System.out.println(OutputFormatter.formatCompra(c));
            else System.out.println("Compra não encontrada.");
        } catch (Exception e) { System.out.println("Erro ao buscar compra."); e.printStackTrace(); }
    }

    private void incluir() {
        try {
            System.out.println("\nInclusão de compra");
            System.out.print("Cliente ID (vazio para nenhum): "); int clienteId = -1; String cid = console.nextLine(); if (!cid.isEmpty()) { try { clienteId = Integer.parseInt(cid); } catch (Exception ex) { clienteId = -1; } }
            System.out.print("Status: "); String status = console.nextLine();
            System.out.print("Valor: "); double valor = 0.0; try { valor = Double.parseDouble(console.nextLine()); } catch (Exception ex) { valor = 0.0; }
            LocalDate data = LocalDate.now();
            Compra c = new Compra(status, valor);
            c.setData(data);
            c.setClienteId(clienteId);
            if (compraDAO.incluirComValidacao(c)) System.out.println("Compra incluída com sucesso."); else System.out.println("Erro ao incluir compra.");
        } catch (Exception e) { System.out.println("Erro ao incluir compra."); e.printStackTrace(); }
    }

    private void alterar() {
        System.out.print("ID da compra a ser alterada: ");
        int id; try { id = Integer.parseInt(console.nextLine()); } catch (NumberFormatException e) { System.out.println("ID inválido."); return; }
        try {
            Compra c = compraDAO.buscar(id);
            if (c == null) { System.out.println("Compra não encontrada."); return; }
            System.out.print("Novo status (vazio para manter): "); String status = console.nextLine(); if (!status.isEmpty()) c.setStatus(status);
            System.out.print("Novo valor (vazio para manter): "); String vs = console.nextLine(); if (!vs.isEmpty()) { try { c.setValor(Double.parseDouble(vs)); } catch (Exception ex) {} }
            System.out.print("Cliente ID (vazio para manter): "); String cid = console.nextLine(); if (!cid.isEmpty()) { try { c.setClienteId(Integer.parseInt(cid)); } catch (Exception ex) {} }
            System.out.print("Nova data (DD/MM/AAAA - vazio para hoje): "); String ds = console.nextLine(); if (!ds.isEmpty()) { try { c.setData(LocalDate.parse(ds, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))); } catch (Exception ex) { System.out.println("Data inválida. Mantendo atual."); } } else { c.setData(LocalDate.now()); }
            if (compraDAO.alterarComValidacao(c)) System.out.println("Compra alterada com sucesso."); else System.out.println("Erro ao alterar compra.");
        } catch (Exception e) { System.out.println("Erro ao alterar compra."); e.printStackTrace(); }
    }

    private void excluir() {
        System.out.print("ID da compra a ser excluída: ");
        int id; try { id = Integer.parseInt(console.nextLine()); } catch (NumberFormatException e) { System.out.println("ID inválido."); return; }
        try { Compra c = compraDAO.buscar(id); if (c == null) { System.out.println("Compra não encontrada."); return; } System.out.print("Confirma exclusão? (S/N): "); String r = console.nextLine(); if (!r.isEmpty() && (r.charAt(0)=='S' || r.charAt(0)=='s')) { if (compraDAO.excluir(id)) System.out.println("Compra excluída."); else System.out.println("Erro ao excluir."); } } catch (Exception e) { System.out.println("Erro ao excluir compra."); e.printStackTrace(); }

    }

    private void listar() {
        try {
            System.out.println("Listagem de compras:");
            for (int id = 1; id <= 1000; id++) {
                try { Compra c = compraDAO.buscar(id); if (c != null) System.out.println(OutputFormatter.formatCompra(c)); } catch (Exception e) { }
            }
        } catch (Exception e) { System.out.println("Erro ao listar compras."); e.printStackTrace(); }
    }
}
