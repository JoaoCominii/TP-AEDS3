package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArvoreBMaisClienteBiblioteca implements Serializable {
    private static final long serialVersionUID = 1L;
    private NoB raiz;
    private int ordem;
    private transient NoFolha primeiraFolha;

    public ArvoreBMaisClienteBiblioteca(int ordem) {
        this.ordem = ordem;
        this.raiz = new NoFolha();
        this.primeiraFolha = (NoFolha) raiz;
    }

    public void inserir(int clienteId, int bibliotecaId) {
        NoFolha folha = encontrarFolha(clienteId);
        folha.inserir(clienteId, bibliotecaId, ordem);

        if (folha.isFull(ordem)) {
            dividirFolha(folha);
        }
    }

    public boolean excluir(int clienteId, int bibliotecaId) {
        NoFolha folha = encontrarFolha(clienteId);
        List<Integer> bibliotecas = folha.buscarBibliotecasPorCliente(clienteId);

        boolean removido = bibliotecas.remove(Integer.valueOf(bibliotecaId));

        if (removido && bibliotecas.isEmpty()) {
            int pos = folha.encontrarPosicao(clienteId);
            if (pos < folha.chaves.size() && folha.chaves.get(pos) == clienteId) {
                folha.chaves.remove(pos);
                folha.getValores().remove(pos);
            }
        }
        return removido;
    }

    public List<Integer> buscar(int clienteId) {
        NoFolha folha = encontrarFolha(clienteId);
        return folha.buscarBibliotecasPorCliente(clienteId);
    }

    private NoFolha encontrarFolha(int clienteId) {
        NoB atual = raiz;
        while (!atual.ehFolha) {
            NoInterno noInterno = (NoInterno) atual;
            int pos = 0;
            while (pos < atual.chaves.size() && clienteId >= atual.chaves.get(pos)) {
                pos++;
            }
            atual = noInterno.getFilhos().get(pos);
        }
        return (NoFolha) atual;
    }

    private void dividirFolha(NoFolha folha) {
        NoFolha novaFolha = new NoFolha();
        int meio = ordem / 2;

        while (folha.chaves.size() > meio) {
            int ultimoIndice = folha.chaves.size() - 1;
            novaFolha.chaves.add(0, folha.chaves.remove(ultimoIndice));
            novaFolha.getValores().add(0, folha.getValores().remove(ultimoIndice));
        }

        novaFolha.setProximo(folha.getProximo());
        novaFolha.setAnterior(folha);
        if (folha.getProximo() != null) {
            folha.getProximo().setAnterior(novaFolha);
        }
        folha.setProximo(novaFolha);

        int chavePromovida = novaFolha.chaves.get(0);

        if (folha.pai == null) {
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
                // Lógica de divisão de nó interno (omitida por simplicidade)
            }
        }
    }

    // --- PERSISTÊNCIA ---
    public static ArvoreBMaisClienteBiblioteca carregarDeDisco(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            ArvoreBMaisClienteBiblioteca arvore = (ArvoreBMaisClienteBiblioteca) in.readObject();
            arvore.restaurarEstrutura();
            return arvore;
        }
    }

    public void salvarEmDisco(String path) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(this);
        }
    }

    private void restaurarEstrutura() {
        if (this.raiz == null) return;
        NoB noAtual = this.raiz;
        while (!noAtual.ehFolha) {
            noAtual = ((NoInterno) noAtual).getFilhos().get(0);
        }
        this.primeiraFolha = (NoFolha) noAtual;
        restaurarPonteiros(this.raiz, null);
    }

    private void restaurarPonteiros(NoB no, NoInterno pai) {
        no.pai = pai;
        if (!no.ehFolha) {
            NoInterno noInterno = (NoInterno) no;
            NoB filhoAnterior = null;
            for (NoB filho : noInterno.getFilhos()) {
                restaurarPonteiros(filho, noInterno);
                if (filho.ehFolha && filhoAnterior != null) {
                    ((NoFolha) filhoAnterior).setProximo((NoFolha) filho);
                    ((NoFolha) filho).setAnterior((NoFolha) filhoAnterior);
                }
                filhoAnterior = filho;
            }
        }
    }

    // --- CLASSES INTERNAS ---
    abstract static class NoB implements Serializable {
        private static final long serialVersionUID = 1L;
        protected List<Integer> chaves;
        protected boolean ehFolha;
        protected transient NoB pai;

        public NoB(boolean ehFolha) {
            this.chaves = new ArrayList<>();
            this.ehFolha = ehFolha;
            this.pai = null;
        }

        public abstract boolean isFull(int ordem);
        public abstract void inserir(int chave, int valor, int ordem);
    }

    static class NoInterno extends NoB {
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
        public void inserir(int chave, int valor, int ordem) {
            int pos = 0;
            while (pos < chaves.size() && chave >= chaves.get(pos)) {
                pos++;
            }
            filhos.get(pos).inserir(chave, valor, ordem);
        }

        public void inserirChave(int chave, NoB novoFilho) {
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

    static class NoFolha extends NoB {
        private static final long serialVersionUID = 1L;
        private List<List<Integer>> valores;
        private transient NoFolha proximo;
        private transient NoFolha anterior;

        public NoFolha() {
            super(true);
            this.valores = new ArrayList<>();
        }

        @Override
        public boolean isFull(int ordem) {
            return chaves.size() >= ordem;
        }

        @Override
        public void inserir(int chave, int valor, int ordem) {
            int pos = encontrarPosicao(chave);
            if (pos < chaves.size() && chaves.get(pos) == chave) {
                valores.get(pos).add(valor);
            } else {
                chaves.add(pos, chave);
                List<Integer> novaLista = new ArrayList<>();
                novaLista.add(valor);
                valores.add(pos, novaLista);
            }
        }

        public int encontrarPosicao(int chave) {
            int pos = 0;
            while (pos < chaves.size() && chave > chaves.get(pos)) {
                pos++;
            }
            return pos;
        }

        public List<Integer> buscarBibliotecasPorCliente(int chave) {
            int pos = encontrarPosicao(chave);
            if (pos < chaves.size() && chaves.get(pos) == chave) {
                return new ArrayList<>(valores.get(pos));
            }
            return new ArrayList<>();
        }

        public List<List<Integer>> getValores() { return valores; }
        public NoFolha getProximo() { return proximo; }
        public void setProximo(NoFolha proximo) { this.proximo = proximo; }
        public void setAnterior(NoFolha anterior) { this.anterior = anterior; }
    }
}
