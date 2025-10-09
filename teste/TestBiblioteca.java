package teste;

import dao.BibliotecaDAO;
import model.Biblioteca;

public class TestBiblioteca {
    public static void main(String[] args) {
        try {
            BibliotecaDAO dao = new BibliotecaDAO();
            Biblioteca b = new Biblioteca("Central","Biblioteca central da cidade","ativa");
            boolean ok = dao.incluir(b);
            System.out.println("incluir returned: " + ok);
            Biblioteca b2 = dao.buscar(1);
            System.out.println(b2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
