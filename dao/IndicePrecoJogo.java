package dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Jogo;

public class IndicePrecoJogo {
    private ArvoreBMaisPreco arvore;
    private static final String ARQUIVO_INDICE = "dados/jogos/preco_idx.db";
    private JogoDAO jogoDAO; // para buscar jogos por ID

    public IndicePrecoJogo(JogoDAO jogoDAO) {
        this.jogoDAO = jogoDAO;
        try {
            this.arvore = ArvoreBMaisPreco.carregarDeDisco(ARQUIVO_INDICE);
            System.out.println("Índice de preços carregado do disco.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Não foi possível carregar o índice do disco. Criando um novo...");
            this.arvore = new ArvoreBMaisPreco(4);
            // Opcional: reconstruir a partir dos dados existentes
            // reconstruirIndice();
        }
    }

    public void reconstruirIndice() {
        System.out.println("Reconstruindo índice de preços a partir do arquivo de dados...");
        this.arvore = new ArvoreBMaisPreco(4);
        try {
            List<Jogo> todosJogos = jogoDAO.listarTodos();
            for (Jogo jogo : todosJogos) {
                adicionarJogo(jogo);
            }
            salvarIndice();
            System.out.println("Índice reconstruído e salvo com " + todosJogos.size() + " jogos.");
        } catch (Exception e) {
            System.err.println("Erro ao reconstruir o índice de preços: " + e.getMessage());
        }
    }

    public void salvarIndice() {
        try {
            arvore.salvarEmDisco(ARQUIVO_INDICE);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o índice de preços no disco: " + e.getMessage());
        }
    }

    public void adicionarJogo(Jogo jogo) {
        arvore.inserirJogo(jogo);
    }

    public void removerJogo(Jogo jogo) {
        arvore.removerJogo(jogo.getPreco(), jogo.getId());
    }

    private List<Jogo> buscarJogosPorIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return ids.stream()
                  .map(id -> {
                      try {
                          return jogoDAO.buscar(id);
                      } catch (Exception e) {
                          System.err.println("Erro ao buscar jogo com ID " + id + ": " + e.getMessage());
                          return null;
                      }
                  })
                  .filter(jogo -> jogo != null)
                  .collect(Collectors.toList());
    }

    public List<Jogo> buscarPorPreco(double preco) {
        List<Integer> ids = arvore.buscarPorPreco(preco);
        return buscarJogosPorIds(ids);
    }

    public List<Jogo> buscarPorFaixaPreco(double precoMin, double precoMax) {
        List<Integer> ids = arvore.buscarPorFaixaPreco(precoMin, precoMax);
        return buscarJogosPorIds(ids);
    }

    public List<Jogo> buscarJogosPromocao() {
        List<Integer> ids = arvore.buscarMaisBaratosQue(30.0);
        return buscarJogosPorIds(ids);
    }

    public List<Jogo> buscarJogosPremium() {
        List<Integer> ids = arvore.buscarMaisCarosQue(100.0);
        return buscarJogosPorIds(ids);
    }

    public List<Jogo> listarOrdenadosPorPreco() {
        List<Integer> ids = arvore.listarTodosOrdenadosPorPreco();
        return buscarJogosPorIds(ids);
    }
}