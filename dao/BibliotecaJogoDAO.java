package dao;

import java.util.ArrayList;
import java.util.List;
import model.BibliotecaJogo;

public class BibliotecaJogoDAO {
    private Arquivo<BibliotecaJogo> arq;
    private IndiceBibliotecaJogo indice;
    private JogoDAO jogoDAO; // Dependência para buscar jogos

    public BibliotecaJogoDAO() throws Exception {
        this.arq = new Arquivo<>("biblioteca_jogos", BibliotecaJogo.class.getConstructor());
        this.jogoDAO = new JogoDAO();
        this.indice = new IndiceBibliotecaJogo(this, this.jogoDAO);
    }

    private boolean isDatabaseEmpty() {
        // A lógica de verificação se a base está vazia foi movida para o construtor do índice.
        return false; 
    }

    public List<BibliotecaJogo> listarTodos() throws Exception {
        List<BibliotecaJogo> relacoes = new ArrayList<>();
        int id = 1;
        int falhas = 0;
        while (falhas < 10) {
            try {
                BibliotecaJogo bj = arq.read(id);
                if (bj != null) {
                    relacoes.add(bj);
                    falhas = 0;
                } else {
                    falhas++;
                }
                id++;
            } catch (Exception e) {
                falhas++;
                id++;
            }
        }
        return relacoes;
    }

    public List<model.Jogo> buscarJogosDaBiblioteca(int bibliotecaId) {
        return indice.buscarJogosPorBiblioteca(bibliotecaId);
    }

    public List<BibliotecaJogo> buscarRelacoesDaBiblioteca(int bibliotecaId) throws Exception {
        List<BibliotecaJogo> todasRelacoes = listarTodos();
        List<BibliotecaJogo> relacoesDaBiblioteca = new ArrayList<>();
        for (BibliotecaJogo bj : todasRelacoes) {
            if (bj.getBibliotecaId() == bibliotecaId) {
                relacoesDaBiblioteca.add(bj);
            }
        }
        return relacoesDaBiblioteca;
    }

    public boolean incluir(BibliotecaJogo bj) throws Exception {
        int id = arq.create(bj);
        if (id > 0) {
            indice.adicionar(bj);
            indice.salvarIndice();
            return true;
        }
        return false;
    }

    public boolean excluir(int id) throws Exception {
        BibliotecaJogo bj = arq.read(id);
        boolean sucesso = arq.delete(id);
        if (sucesso && bj != null) {
            indice.remover(bj);
            indice.salvarIndice();
        }
        return sucesso;
    }
    
    public BibliotecaJogo buscar(int id) throws Exception {
        return arq.read(id);
    }

    public void reconstruirIndice() {
        indice.reconstruirIndice();
    }
}
