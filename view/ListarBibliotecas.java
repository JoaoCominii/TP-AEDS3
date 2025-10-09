package view;

import dao.BibliotecaDAO;
import model.Biblioteca;
import util.OutputFormatter;

public class ListarBibliotecas {
    public static void main(String[] args) throws Exception {
        BibliotecaDAO dao = new BibliotecaDAO();
        System.out.println("Listagem de bibliotecas:");
        // tentar ler IDs sucessivos até não encontrar (simples)
        int id = 1;
        while (true) {
            Biblioteca b = dao.buscar(id);
            if (b == null) break;
            System.out.println(util.OutputFormatter.formatBiblioteca(b));
            id++;
        }
    }
}
