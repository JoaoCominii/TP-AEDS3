package dao;

import java.io.*;
import java.util.*;
import model.Jogo;

public class ArvoreBMaisPreco implements Serializable {
    private static final long serialVersionUID = 1L;
    private NoB raiz;
    private int ordem;
    private transient NoFolha primeiraFolha; // transient para evitar ciclo na serialização padrão

    public ArvoreBMaisPreco(int ordem) {
        this.ordem = ordem;
        this.raiz = new NoFolha();
        this.primeiraFolha = (NoFolha) raiz;
    }
        
    public void inserirJogo(Jogo jogo) {
        inserir(jogo.getPreco(), jogo.getId());
    }
    
    private void inserir(double preco, int jogoId) {
        NoFolha folha = encontrarFolha(preco);
        folha.inserir(preco, jogoId, ordem);
        
        if (folha.isFull(ordem)) {
            dividirFolha(folha);
        }
    }
    // principais
    public List<Integer> buscarPorPreco(double preco) {
        NoFolha folha = encontrarFolha(preco);
        return folha.buscarJogosPorPreco(preco);
    }
    
    public List<Integer> buscarPorFaixaPreco(double precoMin, double precoMax) {
        List<Integer> resultado = new ArrayList<>();
        NoFolha folhaAtual = encontrarFolha(precoMin);
        
        while (folhaAtual != null) {
            for (int i = 0; i < folhaAtual.chaves.size(); i++) {
                double preco = folhaAtual.chaves.get(i);
                
                if (preco >= precoMin && preco <= precoMax) {
                    resultado.addAll(folhaAtual.getValores().get(i));
                } else if (preco > precoMax) {
                    return resultado;
                }
            }
            folhaAtual = folhaAtual.getProximo();
        }
        
        return resultado;
    }
    
    public List<Integer> buscarMaisBaratosQue(double precoMax) {
        return buscarPorFaixaPreco(0.0, precoMax);
    }
    
    public List<Integer> buscarMaisCarosQue(double precoMin) {
        return buscarPorFaixaPreco(precoMin, Double.MAX_VALUE);
    }
    
    public List<Integer> listarTodosOrdenadosPorPreco() {
        List<Integer> resultado = new ArrayList<>();
        NoFolha folhaAtual = primeiraFolha;
        
        while (folhaAtual != null) {
            for (List<Integer> jogosComMesmoPreco : folhaAtual.getValores()) {
                resultado.addAll(jogosComMesmoPreco);
            }
            folhaAtual = folhaAtual.getProximo();
        }
        
        return resultado;
    }
    
    public boolean removerJogo(double preco, int jogoId) {
        NoFolha folha = encontrarFolha(preco);
        List<Integer> jogos = folha.buscarJogosPorPreco(preco);
        
        boolean removido = false;
        for (int i = 0; i < jogos.size(); i++) {
            if (jogos.get(i) == jogoId) {
                jogos.remove(i);
                removido = true;
                break;
            }
        }
        
        // Se a lista ficou vazia, remove o preço também
        if (removido && jogos.isEmpty()) {
            int pos = folha.encontrarPosicao(preco);
            if (pos < folha.chaves.size() && Math.abs(folha.chaves.get(pos) - preco) < 0.01) {
                folha.chaves.remove(pos);
                folha.getValores().remove(pos);
            }
        }
        
        return removido;
    }
    
    // auxiliares
    
    private NoFolha encontrarFolha(double preco) {
        NoB atual = raiz;
        
        while (!atual.ehFolha) {
            NoInterno noInterno = (NoInterno) atual;
            int pos = 0;
            
            while (pos < atual.chaves.size() && preco >= atual.chaves.get(pos)) {
                pos++;
            }
            
            atual = noInterno.getFilhos().get(pos);
        }
        
        return (NoFolha) atual;
    }
    
    private void dividirFolha(NoFolha folha) {
        NoFolha novaFolha = new NoFolha();
        int meio = ordem / 2;
        
        // Move metade das chaves para a nova folha
        while (folha.chaves.size() > meio) {
            int ultimoIndice = folha.chaves.size() - 1;
            novaFolha.chaves.add(0, folha.chaves.remove(ultimoIndice));
            novaFolha.getValores().add(0, folha.getValores().remove(ultimoIndice));
        }
        
        // Atualiza links entre folhas
        novaFolha.setProximo(folha.getProximo());
        novaFolha.setAnterior(folha);
        
        if (folha.getProximo() != null) {
            folha.getProximo().setAnterior(novaFolha);
        }
        folha.setProximo(novaFolha);
        
        // Promove chave para o pai
        double chavePromovida = novaFolha.chaves.get(0);
        
        if (folha.pai == null) {
            // Criar nova raiz
            NoInterno novaRaiz = new NoInterno();
            novaRaiz.chaves.add(chavePromovida);
            novaRaiz.getFilhos().add(folha);
            novaRaiz.getFilhos().add(novaFolha);
            
            folha.pai = novaRaiz;
            novaFolha.pai = novaRaiz;
            raiz = novaRaiz;
        } else {
            NoInterno pai = (NoInterno) folha.pai;
            pai.inserirChave(chavePromovida, novaFolha);
            
            if (pai.isFull(ordem)) {
                // Implementação do split de nó interno seria aqui
                // Para simplificar, vamos deixar sem por enquanto
            }
        }
    }
    
    public void exibirEstrutura() {
        System.out.println("\n=== Estrutura da Árvore B+ (Preços) ===");
        NoFolha folhaAtual = primeiraFolha;
        int folhaNum = 1;
        
        while (folhaAtual != null) {
            System.out.println("Folha " + folhaNum + ":");
            for (int i = 0; i < folhaAtual.chaves.size(); i++) {
                double preco = folhaAtual.chaves.get(i);
                int qtdJogos = folhaAtual.getValores().get(i).size();
                System.out.println("  Preço R$" + String.format("%.2f", preco) + " -> " + qtdJogos + " jogo(s)");
            }
            folhaAtual = folhaAtual.getProximo();
            folhaNum++;
        }
        System.out.println("=====================================\n");
    }

    // --- MÉTODOS DE PERSISTÊNCIA ---

    public void salvarEmDisco(String path) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(this);
        }
    }

    public static ArvoreBMaisPreco carregarDeDisco(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            ArvoreBMaisPreco arvore = (ArvoreBMaisPreco) in.readObject();
            arvore.restaurarEstrutura();
            return arvore;
        }
    }

    private void restaurarEstrutura() {
        if (this.raiz == null) return;

        // Encontra a primeira folha
        NoB noAtual = this.raiz;
        while (!noAtual.ehFolha) {
            noAtual = ((NoInterno) noAtual).getFilhos().get(0);
        }
        this.primeiraFolha = (NoFolha) noAtual;

        // Restaura os ponteiros 'pai' e 'proximo'/'anterior'
        restaurarPonteiros(this.raiz, null);
    }

    private void restaurarPonteiros(NoB no, NoInterno pai) {
        no.pai = pai;
        if (!no.ehFolha) {
            NoInterno noInterno = (NoInterno) no;
            NoB filhoAnterior = null;
            for (NoB filho : noInterno.getFilhos()) {
                restaurarPonteiros(filho, noInterno);
                // elo entre folhas
                if (filho.ehFolha && filhoAnterior != null) {
                    ((NoFolha) filhoAnterior).setProximo((NoFolha) filho);
                    ((NoFolha) filho).setAnterior((NoFolha) filhoAnterior);
                }
                filhoAnterior = filho;
            }
        }
    }
}

// classe nos

abstract class NoB implements Serializable {
    private static final long serialVersionUID = 1L;
    protected List<Double> chaves;
    protected boolean ehFolha;
    protected transient NoB pai;
    
    public NoB(boolean ehFolha) {
        this.chaves = new ArrayList<>();
        this.ehFolha = ehFolha;
        this.pai = null;
    }
    
    public abstract boolean isFull(int ordem);
    public abstract void inserir(double preco, int jogoId, int ordem);
}

class NoInterno extends NoB {
    private static final long serialVersionUID = 1L;
    private List<NoB> filhos;
    
    public NoInterno() {
        super(false);
        this.filhos = new ArrayList<>();
    }
    
    @Override
    public boolean isFull(int ordem) {
        return chaves.size() >= ordem - 1;
    }
    
    @Override
    public void inserir(double preco, int jogoId, int ordem) {
        int pos = 0;
        while (pos < chaves.size() && preco > chaves.get(pos)) {
            pos++;
        }
        filhos.get(pos).inserir(preco, jogoId, ordem);
    }
    
    public void inserirChave(double chave, NoB novoFilho) {
        int pos = 0;
        while (pos < chaves.size() && chave > chaves.get(pos)) {
            pos++;
        }
        chaves.add(pos, chave);
        filhos.add(pos + 1, novoFilho);
        novoFilho.pai = this;
    }
    
    public List<NoB> getFilhos() { 
        return filhos; 
    }
}

class NoFolha extends NoB {
    private static final long serialVersionUID = 1L;
    private List<List<Integer>> valores;
    private transient NoFolha proximo;
    private transient NoFolha anterior;
    
    public NoFolha() {
        super(true);
        this.valores = new ArrayList<>();
        this.proximo = null;
        this.anterior = null;
    }
    
    @Override
    public boolean isFull(int ordem) {
        return chaves.size() >= ordem;
    }
    
    @Override
    public void inserir(double preco, int jogoId, int ordem) {
        int pos = encontrarPosicao(preco);
        
        if (pos < chaves.size() && Math.abs(chaves.get(pos) - preco) < 0.01) {
            // Preço já existe, adiciona o jogo na lista
            valores.get(pos).add(jogoId);
        } else {
            // Novo preço
            chaves.add(pos, preco);
            List<Integer> listaIds = new ArrayList<>();
            listaIds.add(jogoId);
            valores.add(pos, listaIds);
        }
    }
    
    public int encontrarPosicao(double preco) {
        int pos = 0;
        while (pos < chaves.size() && preco > chaves.get(pos)) {
            pos++;
        }
        return pos;
    }
    
    public List<Integer> buscarJogosPorPreco(double preco) {
        int pos = encontrarPosicao(preco);
        if (pos < chaves.size() && Math.abs(chaves.get(pos) - preco) < 0.01) {
            return new ArrayList<>(valores.get(pos));
        }
        return new ArrayList<>();
    }
    
    // Getters e Setters
    public List<List<Integer>> getValores() { return valores; }
    public NoFolha getProximo() { return proximo; }
    public NoFolha getAnterior() { return anterior; }
    public void setProximo(NoFolha proximo) { this.proximo = proximo; }
    public void setAnterior(NoFolha anterior) { this.anterior = anterior; }
}