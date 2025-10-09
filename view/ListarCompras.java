package view;

import dao.CompraDAO;
import model.Compra;
import util.OutputFormatter;

public class ListarCompras {
    public static void main(String[] args) throws Exception {
        CompraDAO dao = new CompraDAO();
        System.out.println("Listagem de compras:");
        for (int id = 1; id <= 1000; id++) {
            Compra c = dao.buscar(id);
            if (c == null) continue;
            System.out.println(OutputFormatter.formatCompra(c));
        }
    }
}
