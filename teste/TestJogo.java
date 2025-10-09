package teste;

import dao.JogoDAO;
import model.Jogo;

import java.util.Arrays;

public class TestJogo {
    public static void main(String[] args) {
        try {
            JogoDAO dao = new JogoDAO();

            // Incluir
            Jogo j = new Jogo("Zelda", "Aventura épica", "2GB", (short)5, "Switch", 199.90, Arrays.asList("Aventura","RPG"), "10");
            boolean ok = dao.incluir(j);
            System.out.println("incluir returned: " + ok);

            // Buscar e imprimir
            Jogo j2 = dao.buscar(1);
            System.out.println(j2 == null ? "buscar retornou null" : j2);

            // Alterar
            if (j2 != null) {
                j2.setPreco(149.90);
                j2.setPlataforma("Switch/Steam");
                boolean alt = dao.alterar(j2);
                System.out.println("alterar returned: " + alt);
            }

            // Listar
            for (int id = 1; id <= 10; id++) {
                Jogo x = dao.buscar(id);
                if (x != null) System.out.println(x);
            }

            // Excluir
            boolean del = dao.excluir(1);
            System.out.println("excluir returned: " + del);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
