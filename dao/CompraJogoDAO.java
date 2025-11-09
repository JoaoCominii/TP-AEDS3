package dao;

import java.util.List;
import model.CompraJogo;

public class CompraJogoDAO {

    private ArquivoCompraJogo arq;
    private IndiceCompraJogo indice;

    public CompraJogoDAO() {
        try {
            arq = new ArquivoCompraJogo();
            indice = new IndiceCompraJogo();
        } catch (Exception e) {
            System.err.println("ERRO ao inicializar CompraJogoDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void create(CompraJogo cj) throws Exception {
        // Garante que a combinação de chaves não existe
        if (arq.read(cj.getIdCompra(), cj.getIdJogo()) != null) {
            throw new Exception("O jogo " + cj.getIdJogo() + " já está associado à compra " + cj.getIdCompra());
        }
        arq.create(cj);
        indice.inserir(cj.getIdCompra(), cj.getIdJogo());
    }

    public CompraJogo read(int idCompra, int idJogo) {
        try {
            return arq.read(idCompra, idJogo);
        } catch (Exception e) {
            System.err.println("ERRO ao ler CompraJogo: " + e.getMessage());
            return null;
        }
    }

    public boolean update(CompraJogo cj) {
        try {
            return arq.update(cj);
        } catch (Exception e) {
            System.err.println("ERRO ao atualizar CompraJogo: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int idCompra, int idJogo) {
        try {
            if (arq.delete(idCompra, idJogo)) {
                indice.remover(idCompra, idJogo);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("ERRO ao deletar CompraJogo: " + e.getMessage());
            return false;
        }
    }

    public List<CompraJogo> readAll() {
        try {
            return arq.readAll();
        } catch (Exception e) {
            System.err.println("ERRO ao ler todos os CompraJogo: " + e.getMessage());
            return null;
        }
    }

    public List<Integer> getIdsJogoPorCompra(int idCompra) {
        return indice.buscarJogosPorCompra(idCompra);
    }

    public List<Integer> getIdsCompraPorJogo(int idJogo) {
        return indice.buscarComprasPorJogo(idJogo);
    }
    
    public void recriarIndice() {
        try {
            List<CompraJogo> todos = arq.readAll();
            indice.recriar(todos);
        } catch (Exception e) {
            System.err.println("ERRO ao recriar índice de CompraJogo: " + e.getMessage());
        }
    }

    public void apagarTudo() {
        try {
            arq.apagarTudo();
            indice.apagarIndices();
            System.out.println("Dados e índices de CompraJogo foram apagados.");
        } catch (Exception e) {
            System.err.println("ERRO ao apagar dados de CompraJogo: " + e.getMessage());
        }
    }
}
