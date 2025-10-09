package teste;

import dao.ClienteDAO;
import model.Cliente;

public class SeedData {
    public static void main(String[] args) {
        try {
            ClienteDAO dao = new ClienteDAO();
            Cliente c = new Cliente(-1, "Joao", "joao@example.com", "senha123", java.time.LocalDate.now());
            boolean ok = dao.incluirCliente(c);
            System.out.println("seed incluir returned: " + ok);
            System.out.println("Seed Cliente: " + dao.buscarCliente(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
