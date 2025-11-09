package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.BibliotecaJogo;
import model.Jogo;

public class IndiceBibliotecaJogo {
    private ArvoreBMaisBibliotecaJogo arvore;
    private final String arqPath = "dados/bibliotecas/biblioteca_jogo_idx.db";
    private BibliotecaJogoDAO bibliotecaJogoDAO;
    private JogoDAO jogoDAO;

    public IndiceBibliotecaJogo(BibliotecaJogoDAO bibliotecaJogoDAO, JogoDAO jogoDAO) {
        this.bibliotecaJogoDAO = bibliotecaJogoDAO;
        this.jogoDAO = jogoDAO;
        File f = new File(arqPath);
        if (f.exists()) {
            try {
                this.arvore = ArvoreBMaisBibliotecaJogo.carregarDeDisco(arqPath);
                System.out.println("Índice Biblioteca->Jogo carregado do disco.");
            } catch (Exception e) {
                System.err.println("Erro ao carregar índice Biblioteca->Jogo: " + e.getMessage());
                this.arvore = new ArvoreBMaisBibliotecaJogo(5);
                reconstruirIndice();
            }
        } else {
            System.out.println("Índice Biblioteca->Jogo não encontrado. Criando um novo...");
            this.arvore = new ArvoreBMaisBibliotecaJogo(5);
        }
    }

    public void salvarIndice() {
        try {
            File dir = new File("dados/bibliotecas");
            if (!dir.exists()) dir.mkdirs();
            arvore.salvarEmDisco(arqPath);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o índice Biblioteca->Jogo: " + e.getMessage());
        }
    }

    public void reconstruirIndice() {
        System.out.println("Recriando índice Biblioteca->Jogo...");
        this.arvore = new ArvoreBMaisBibliotecaJogo(5);
        try {
            List<BibliotecaJogo> todasRelacoes = bibliotecaJogoDAO.listarTodos();
            for (BibliotecaJogo bj : todasRelacoes) {
                adicionar(bj);
            }
            salvarIndice();
            System.out.println("Índice Biblioteca->Jogo recriado com sucesso.");
        } catch (Exception e) {
            System.err.println("Falha ao recriar o índice Biblioteca->Jogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void adicionar(BibliotecaJogo bj) {
        if (bj != null) {
            arvore.inserir(bj.getBibliotecaId(), bj.getJogoId());
        }
    }

    public void remover(BibliotecaJogo bj) {
        if (bj != null) {
            arvore.excluir(bj.getBibliotecaId(), bj.getJogoId());
        }
    }

    public List<Jogo> buscarJogosPorBiblioteca(int bibliotecaId) {
        List<Integer> jogoIds = arvore.buscar(bibliotecaId);
        List<Jogo> jogos = new ArrayList<>();
        if (jogoIds != null) {
            for (int id : jogoIds) {
                try {
                    Jogo j = jogoDAO.buscar(id);
                    if (j != null) {
                        jogos.add(j);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao buscar jogo com ID " + id + ": " + e.getMessage());
                }
            }
        }
        return jogos;
    }
}
