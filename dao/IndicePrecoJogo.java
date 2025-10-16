package dao;

import model.Jogo;
import java.util.*;

public class IndicePrecoJogo {
    private ArvoreBMaisPreco arvore;
    
    public IndicePrecoJogo() {
        this.arvore = new ArvoreBMaisPreco(4);
    }
    
    public void adicionarJogo(Jogo jogo) {
        arvore.inserirJogo(jogo);
    }
    
    public void removerJogo(Jogo jogo) {
        arvore.removerJogo(jogo.getPreco(), jogo.getId());
    }
    
    public List<Jogo> buscarPorPreco(double preco) {
        return arvore.buscarPorPreco(preco);
    }
    
    public List<Jogo> buscarPorFaixaPreco(double precoMin, double precoMax) {
        return arvore.buscarPorFaixaPreco(precoMin, precoMax);
    }
    
    public List<Jogo> buscarJogosPromocao() {
        return arvore.buscarMaisBaratosQue(30.0);
    }
    
    public List<Jogo> buscarJogosPremium() {
        return arvore.buscarMaisCarosQue(100.0);
    }
    
    public List<Jogo> listarOrdenadosPorPreco() {
        return arvore.listarTodosOrdenadosPorPreco();
    }
}