package view;

import dao.JogoDAO;
import model.Jogo;
import util.OutputFormatter;

public class ListarJogos {
    public static void main(String[] args) throws Exception {
        JogoDAO dao = new JogoDAO();
        System.out.println("Listagem de jogos:");
        for (int id = 1; id <= 1000; id++) {
            Jogo j = dao.buscar(id);
            if (j == null) continue;
            System.out.println(OutputFormatter.formatJogo(j));
        }
    }
}
