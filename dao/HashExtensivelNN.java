package dao;

import java.io.*;
import java.util.*;

public class HashExtensivelNN<K, V> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int globalDepth;
    private final int bucketSize;
    private final List<Bucket<K, V>> diretorio;

    public HashExtensivelNN(int bucketSize) {
        this.globalDepth = 1;
        this.bucketSize = bucketSize;
        this.diretorio = new ArrayList<>();
        diretorio.add(new Bucket<>(globalDepth));
        diretorio.add(new Bucket<>(globalDepth));
    }

    // --- OPERAÇÕES GENÉRICAS ---

    public void inserir(K chave, V valor) {
        int hash = hash(chave);
        int indice = getIndice(hash);

        Bucket<K, V> bucket = diretorio.get(indice);
        
        bucket.inserir(chave, valor);

        if (bucket.isFull(bucketSize)) {
            dividirBucket(bucket);
        }
    }

    public V buscar(K chave) {
        int hash = hash(chave);
        int indice = getIndice(hash);
        Bucket<K, V> bucket = diretorio.get(indice);
        return bucket.buscar(chave);
    }

    public boolean remover(K chave) {
        int hash = hash(chave);
        int indice = getIndice(hash);
        Bucket<K, V> bucket = diretorio.get(indice);
        return bucket.remover(chave);
    }
    
    public List<V> listarTodosValores() {
        Set<Bucket<K, V>> visitados = new HashSet<>();
        List<V> todos = new ArrayList<>();
        for (Bucket<K, V> b : diretorio) {
            if (!visitados.contains(b)) {
                todos.addAll(b.listarTodosValores());
                visitados.add(b);
            }
        }
        return todos;
    }


    // --- MÉTODOS AUXILIARES ---

    private int hash(K chave) {
        return chave.hashCode();
    }

    private int getIndice(int hash) {
        int mask = (1 << globalDepth) - 1;
        return hash & mask;
    }

    private void dividirBucket(Bucket<K, V> bucket) {
        int localDepthAntigo = bucket.localDepth;
        
        if (localDepthAntigo >= globalDepth) {
            duplicarDiretorio();
        }
        
        int novaLocalDepth = localDepthAntigo + 1;
        Bucket<K, V> b0 = new Bucket<>(novaLocalDepth);
        Bucket<K, V> b1 = new Bucket<>(novaLocalDepth);

        // Redistribui os valores do bucket antigo
        for (Map.Entry<K, V> entry : bucket.valores.entrySet()) {
            K chave = entry.getKey();
            V valor = entry.getValue();
            int hash = hash(chave);
            
            if (((hash >> localDepthAntigo) & 1) == 0) {
                b0.valores.put(chave, valor);
            } else {
                b1.valores.put(chave, valor);
            }
        }

        // Atualiza referências no diretório
        for (int i = 0; i < diretorio.size(); i++) {
            if (diretorio.get(i) == bucket) {
                if (((i >> localDepthAntigo) & 1) == 0) {
                    diretorio.set(i, b0);
                } else {
                    diretorio.set(i, b1);
                }
            }
        }
    }

    private void duplicarDiretorio() {
        diretorio.addAll(new ArrayList<>(diretorio)); // Duplica o diretório
        globalDepth++;
    }

    // --- PERSISTÊNCIA ---

    @SuppressWarnings("unchecked")
    public static <K, V> HashExtensivelNN<K, V> carregarDeDisco(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (HashExtensivelNN<K, V>) ois.readObject();
        }
    }

    public void salvarEmDisco(String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(this);
        }
    }

    // --- CLASSE INTERNA BUCKET ---

    private static class Bucket<K, V> implements Serializable {
        private static final long serialVersionUID = 1L;
        int localDepth;
        Map<K, V> valores;

        Bucket(int localDepth) {
            this.localDepth = localDepth;
            this.valores = new LinkedHashMap<>();
        }

        boolean isFull(int bucketSize) {
            return valores.size() >= bucketSize;
        }

        void inserir(K chave, V valor) {
            valores.put(chave, valor);
        }

        V buscar(K chave) {
            return valores.get(chave);
        }

        boolean remover(K chave) {
            return valores.remove(chave) != null;
        }

        List<V> listarTodosValores() {
            return new ArrayList<>(valores.values());
        }
    }
}
