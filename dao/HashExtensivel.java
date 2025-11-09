package dao;

import model.Compra;
import java.util.*;

public class HashExtensivel {
    private int globalDepth;              // profundidade global
    private int bucketSize;               // capacidade máxima de cada bucket
    private List<Bucket> diretorio;       // diretório (tabela de ponteiros)

    public HashExtensivel(int bucketSize) {
        this.globalDepth = 1;
        this.bucketSize = bucketSize;
        this.diretorio = new ArrayList<>();

        // inicializa com 2 buckets
        diretorio.add(new Bucket(globalDepth));
        diretorio.add(new Bucket(globalDepth));
    }

    // --- Inserção ---

    public void inserirCompra(Compra compra) {
        double valor = compra.getValor();
        int hash = hash(valor);
        int indice = getIndice(hash);

        Bucket bucket = diretorio.get(indice);
        bucket.inserir(valor, compra);

        // se o bucket estiver cheio -> dividir
        if (bucket.isFull(bucketSize)) {
            dividirBucket(indice);
        }
    }

    // --- Busca ---

    public List<Compra> buscarPorValor(double valor) {
        int hash = hash(valor);
        int indice = getIndice(hash);
        Bucket bucket = diretorio.get(indice);
        return bucket.buscarPorValor(valor);
    }

    // --- Listagem ordenada (por valor) ---

    public List<Compra> listarTodasOrdenadasPorValor() {
        Set<Bucket> visitados = new HashSet<>();
        List<Compra> todas = new ArrayList<>();

        for (Bucket b : diretorio) {
            if (!visitados.contains(b)) {
                todas.addAll(b.listarTodas());
                visitados.add(b);
            }
        }

        todas.sort(Comparator.comparingDouble(Compra::getValor));
        return todas;
    }

    // --- Remoção ---

    public boolean removerCompra(double valor, int compraId) {
        int hash = hash(valor);
        int indice = getIndice(hash);
        Bucket bucket = diretorio.get(indice);

        return bucket.remover(valor, compraId);
    }

    // --- Métodos auxiliares ---

    private int hash(double valor) {
        return Objects.hash(valor);
    }

    private int getIndice(int hash) {
        int mask = (1 << globalDepth) - 1;
        return hash & mask;
    }

    private void dividirBucket(int indice) {
        Bucket bucket = diretorio.get(indice);
        int localDepthAntigo = bucket.localDepth;
        int novaLocalDepth = localDepthAntigo + 1;

        // se localDepth excede globalDepth, duplicar diretório
        if (novaLocalDepth > globalDepth) {
            duplicarDiretorio();
        }

        // cria novos buckets
        Bucket b0 = new Bucket(novaLocalDepth);
        Bucket b1 = new Bucket(novaLocalDepth);

        // redistribui as compras do bucket antigo
        for (Map.Entry<Double, List<Compra>> e : bucket.compras.entrySet()) {
            double valor = e.getKey();
            List<Compra> lista = e.getValue();
            int hash = hash(valor);
            int novoIndice = hash & ((1 << novaLocalDepth) - 1);
            if ((novoIndice & 1) == 0)
                b0.compras.put(valor, new ArrayList<>(lista));
            else
                b1.compras.put(valor, new ArrayList<>(lista));
        }

        // atualiza referências no diretório
        for (int i = 0; i < diretorio.size(); i++) {
            int hashI = i & ((1 << novaLocalDepth) - 1);
            if ((hashI >>> (novaLocalDepth - 1)) == 0 && diretorio.get(i) == bucket) {
                diretorio.set(i, b0);
            } else if (diretorio.get(i) == bucket) {
                diretorio.set(i, b1);
            }
        }
    }

    private void duplicarDiretorio() {
        int tamanhoAntigo = diretorio.size();
        for (int i = 0; i < tamanhoAntigo; i++) {
            diretorio.add(diretorio.get(i)); // duplicar referências
        }
        globalDepth++;
    }

    // --- Exibição da estrutura ---

    public void exibirEstrutura() {
        System.out.println("\n=== Estrutura da Hash Extensível ===");
        System.out.println("Profundidade global: " + globalDepth);
        Set<Bucket> vistos = new HashSet<>();
        int id = 1;
        for (Bucket b : diretorio) {
            if (!vistos.contains(b)) {
                System.out.printf("Bucket %d (ld=%d): %d valores\n",
                        id++, b.localDepth, b.compras.size());
                for (Map.Entry<Double, List<Compra>> e : b.compras.entrySet()) {
                    System.out.printf("  Valor R$%.2f -> %d compra(s)\n",
                            e.getKey(), e.getValue().size());
                }
                vistos.add(b);
            }
        }
        System.out.println("=======================================================\n");
    }

    // --- Classe interna Bucket ---

    private static class Bucket {
        int localDepth;
        Map<Double, List<Compra>> compras;

        Bucket(int localDepth) {
            this.localDepth = localDepth;
            this.compras = new LinkedHashMap<>();
        }

        boolean isFull(int bucketSize) {
            return compras.size() >= bucketSize;
        }

        void inserir(double valor, Compra compra) {
            compras.computeIfAbsent(valor, k -> new ArrayList<>()).add(compra);
        }

        List<Compra> buscarPorValor(double valor) {
            return compras.getOrDefault(valor, new ArrayList<>());
        }

        boolean remover(double valor, int compraId) {
            List<Compra> lista = compras.get(valor);
            if (lista == null) return false;

            boolean removido = lista.removeIf(c -> c.getId() == compraId);
            if (removido && lista.isEmpty()) {
                compras.remove(valor);
            }
            return removido;
        }

        List<Compra> listarTodas() {
            List<Compra> todas = new ArrayList<>();
            for (List<Compra> l : compras.values()) todas.addAll(l);
            return todas;
        }
    }
}
