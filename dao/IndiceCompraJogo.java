package dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.CompraJogo;

public class IndiceCompraJogo {

    private HashExtensivelNN<Integer, List<Integer>> indicePorCompra;
    private HashExtensivelNN<Integer, List<Integer>> indicePorJogo;
    private final String ARQ_INDICE_POR_COMPRA = "dados/compras/indice_compra_jogo.db";
    private final String ARQ_INDICE_POR_JOGO = "dados/jogos/indice_jogo_compra.db";

    @SuppressWarnings("unchecked")
    public IndiceCompraJogo() {
        // --- Carregar ou criar índice Compra -> Jogo ---
        try {
            File f = new File(ARQ_INDICE_POR_COMPRA);
            if (f.exists() && f.length() > 0) {
                indicePorCompra = HashExtensivelNN.carregarDeDisco(ARQ_INDICE_POR_COMPRA);
            } else {
                indicePorCompra = new HashExtensivelNN<>(4);
            }
        } catch (ClassCastException e) {
            System.out.println("Detectado índice antigo (Compra -> Jogo). Recriando...");
            new File(ARQ_INDICE_POR_COMPRA).delete();
            indicePorCompra = new HashExtensivelNN<>(4);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Criando novo índice (Compra -> Jogo)...");
            indicePorCompra = new HashExtensivelNN<>(4);
        }

        // --- Carregar ou criar índice Jogo -> Compra ---
        try {
            File f = new File(ARQ_INDICE_POR_JOGO);
            if (f.exists() && f.length() > 0) {
                indicePorJogo = HashExtensivelNN.carregarDeDisco(ARQ_INDICE_POR_JOGO);
            } else {
                indicePorJogo = new HashExtensivelNN<>(4);
            }
        } catch (ClassCastException e) {
            System.out.println("Detectado índice antigo (Jogo -> Compra). Recriando...");
            new File(ARQ_INDICE_POR_JOGO).delete();
            indicePorJogo = new HashExtensivelNN<>(4);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Criando novo índice (Jogo -> Compra)...");
            indicePorJogo = new HashExtensivelNN<>(4);
        }
    }

    public void inserir(int idCompra, int idJogo) {
        // No índice por compra, a chave é idCompra e o valor é uma lista de idJogo
        List<Integer> jogos = indicePorCompra.buscar(idCompra);
        if (jogos == null) {
            jogos = new ArrayList<>();
        }
        if (!jogos.contains(idJogo)) {
            jogos.add(idJogo);
        }
        indicePorCompra.inserir(idCompra, jogos);

        // No índice por jogo, a chave é idJogo e o valor é uma lista de idCompra
        List<Integer> compras = indicePorJogo.buscar(idJogo);
        if (compras == null) {
            compras = new ArrayList<>();
        }
        if (!compras.contains(idCompra)) {
            compras.add(idCompra);
        }
        indicePorJogo.inserir(idJogo, compras);
        
        salvarIndices();
    }

    public void remover(int idCompra, int idJogo) {
        List<Integer> jogos = indicePorCompra.buscar(idCompra);
        if (jogos != null) {
            jogos.remove(Integer.valueOf(idJogo));
            if (jogos.isEmpty()) {
                indicePorCompra.remover(idCompra);
            } else {
                indicePorCompra.inserir(idCompra, jogos);
            }
        }

        List<Integer> compras = indicePorJogo.buscar(idJogo);
        if (compras != null) {
            compras.remove(Integer.valueOf(idCompra));
            if (compras.isEmpty()) {
                indicePorJogo.remover(idJogo);
            } else {
                indicePorJogo.inserir(idJogo, compras);
            }
        }
        salvarIndices();
    }

    public List<Integer> buscarJogosPorCompra(int idCompra) {
        List<Integer> jogos = indicePorCompra.buscar(idCompra);
        return jogos == null ? new ArrayList<>() : jogos;
    }

    public List<Integer> buscarComprasPorJogo(int idJogo) {
        List<Integer> compras = indicePorJogo.buscar(idJogo);
        return compras == null ? new ArrayList<>() : compras;
    }

    public void salvarIndices() {
        try {
            File dir = new File("dados/compras");
            if (!dir.exists()) dir.mkdirs();
            indicePorCompra.salvarEmDisco(ARQ_INDICE_POR_COMPRA);
        } catch (IOException e) {
            System.err.println("ERRO ao salvar índice Compra->Jogo: " + e.getMessage());
        }
        try {
            File dir = new File("dados/jogos");
            if (!dir.exists()) dir.mkdirs();
            indicePorJogo.salvarEmDisco(ARQ_INDICE_POR_JOGO);
        } catch (IOException e) {
            System.err.println("ERRO ao salvar índice Jogo->Compra: " + e.getMessage());
        }
    }

    public void recriar(List<CompraJogo> todos) {
        apagarIndices();
        indicePorCompra = new HashExtensivelNN<>(4);
        indicePorJogo = new HashExtensivelNN<>(4);

        for (CompraJogo cj : todos) {
            inserir(cj.getIdCompra(), cj.getIdJogo());
        }
        salvarIndices();
        System.out.println("Índices da tabela CompraJogo recriados com sucesso.");
    }
    
    public void apagarIndices() {
        File f1 = new File(ARQ_INDICE_POR_COMPRA);
        if (f1.exists()) f1.delete();
        File f2 = new File(ARQ_INDICE_POR_JOGO);
        if (f2.exists()) f2.delete();
    }
}
