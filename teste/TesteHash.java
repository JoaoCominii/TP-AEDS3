package teste;

import java.time.LocalDate;
import dao.HashExtensivel;
import dao.CompraDAO;
import model.Compra;

public class TesteHash {
    public static void main(String[] args) {
        try {
            // Inicializa DAO e índice hash
            CompraDAO dao = new CompraDAO();
            HashExtensivel indiceValor = new HashExtensivel(2);

            // --- Carrega todas as compras existentes do arquivo para o índice ---
            for (int id = 1; id <= 1000; id++) { // ajuste o range conforme esperado
                Compra c = dao.buscar(id);
                if (c != null) {
                    indiceValor.inserirCompra(c);
                }
            }

            // --- Inserir novas compras no arquivo e no índice ---
            Compra c1 = new Compra(1, "Paga", 350.0, LocalDate.now(), 10);
            Compra c2 = new Compra(2, "Pendente", 120.0, LocalDate.now(), 11);
            Compra c3 = new Compra(3, "Cancelada", 350.0, LocalDate.now(), 12);

            // Grava no arquivo e só insere no índice se persistiu
            if (dao.incluir(c1)) indiceValor.inserirCompra(c1);
            if (dao.incluir(c2)) indiceValor.inserirCompra(c2);
            if (dao.incluir(c3)) indiceValor.inserirCompra(c3);

            // --- Busca por valor ---
            System.out.println("Compras com valor 350.0:");
            for (Compra c : indiceValor.buscarPorValor(350.0)) {
                System.out.println(c);
            }

            // --- Listagem ordenada ---
            System.out.println("\nTodas as compras ordenadas por valor:");
            for (Compra c : indiceValor.listarTodasOrdenadasPorValor()) {
                System.out.println(c);
            }

            // --- Remover uma compra ---
            if (indiceValor.removerCompra(120.0, 2)) {
                dao.excluir(2); // remove também do arquivo
            }

            // --- Exibe estrutura do índice ---
            indiceValor.exibirEstrutura();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
