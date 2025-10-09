package teste;

import dao.CompraDAO;
import model.Compra;

public class TestCompra {
    public static void main(String[] args) {
        try {
            CompraDAO dao = new CompraDAO();
            Compra c = new Compra("pendente", 49.90);
            boolean ok = dao.incluir(c);
            System.out.println("incluir returned: " + ok);

            Compra c2 = dao.buscar(1);
            System.out.println(c2 == null ? "buscar retornou null" : c2);

            if (c2 != null) {
                c2.setStatus("pago");
                c2.setValor(39.90);
                boolean alt = dao.alterar(c2);
                System.out.println("alterar returned: " + alt);
            }

            for (int id = 1; id <= 10; id++) {
                Compra x = dao.buscar(id);
                if (x != null) System.out.println(x);
            }

            boolean del = dao.excluir(1);
            System.out.println("excluir returned: " + del);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
