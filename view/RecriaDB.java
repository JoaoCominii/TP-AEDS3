package view;

import dao.Arquivo;
import model.Cliente;
import java.time.LocalDate;

public class RecriaDB {
    public static void main(String[] args) throws Exception {
        // remove existing file if present
        java.io.File f = new java.io.File("./dados/clientes/clientes.db");
        if (f.exists()) f.delete();

        Arquivo<Cliente> arq = new Arquivo<>("clientes", Cliente.class.getConstructor());
        Cliente c = new Cliente("Joao", "joao@example.com", "senha123", LocalDate.of(2005,4,5));
        arq.create(c);
        Cliente c2 = new Cliente("Maria", "maria@example.com", "maria123", LocalDate.of(1990,1,1));
        arq.create(c2);
        arq.close();
        System.out.println("Banco recriado com 2 clientes de exemplo.");
    }
}
