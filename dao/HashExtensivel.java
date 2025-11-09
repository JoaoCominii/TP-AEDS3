package dao;

import java.io.*;
import java.util.*;

public class HashExtensivel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int globalDepth;
    private final int bucketSize;
    private final List<Bucket> diretorio;

    public HashExtensivel(int bucketSize) {
        this.globalDepth = 1;
        this.bucketSize = bucketSize;
        this.diretorio = new ArrayList<>();
        diretorio.add(new Bucket(globalDepth));
        diretorio.add(new Bucket(globalDepth));
    }

    public void inserir(int chave, int valor) {
        int hash = hash(chave);
        int indice = getIndice(hash);

        Bucket bucket = diretorio.get(indice);
        bucket.inserir(chave, valor);

        if (bucket.isFull(bucketSize)) {
            dividirBucket(bucket);
        }
    }

    public List<Integer> buscar(int chave) {
        int hash = hash(chave);
        int indice = getIndice(hash);
        Bucket bucket = diretorio.get(indice);
        return bucket.buscar(chave);
    }

    public boolean remover(int chave, int valor) {
        int hash = hash(chave);
        int indice = getIndice(hash);
        Bucket bucket = diretorio.get(indice);
        return bucket.remover(chave, valor);
    }
    
    public List<Integer> listarTodosValores() {
        Set<Bucket> visitados = new HashSet<>();
        List<Integer> todos = new ArrayList<>();
        for (Bucket b : diretorio) {
            if (!visitados.contains(b)) {
                todos.addAll(b.listarTodosValores());
                visitados.add(b);
            }
        }
        return todos;
    }

    private int hash(int chave) {
        return Integer.hashCode(chave);
    }

    private int getIndice(int hash) {
        int mask = (1 << globalDepth) - 1;
        return hash & mask;
    }

    private void dividirBucket(Bucket bucket) {
        int localDepthAntigo = bucket.localDepth;
        
        if (localDepthAntigo >= globalDepth) {
            duplicarDiretorio();
        }
        
        int novaLocalDepth = localDepthAntigo + 1;
        Bucket b0 = new Bucket(novaLocalDepth);
        Bucket b1 = new Bucket(novaLocalDepth);

        for (Map.Entry<Integer, List<Integer>> entry : bucket.valores.entrySet()) {
            int chave = entry.getKey();
            List<Integer> lista = entry.getValue();
            int hash = hash(chave);

            if (((hash >> localDepthAntigo) & 1) == 0) {
                b0.valores.put(chave, new ArrayList<>(lista));
            } else {
                b1.valores.put(chave, new ArrayList<>(lista));
            }
        }

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
        diretorio.addAll(new ArrayList<>(diretorio));
        globalDepth++;
    }

    @SuppressWarnings("unchecked")
    public static HashExtensivel carregarDeDisco(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (HashExtensivel) ois.readObject();
        }
    }

    public void salvarEmDisco(String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(this);
        }
    }

    private static class Bucket implements Serializable {
        private static final long serialVersionUID = 1L;
        int localDepth;
        Map<Integer, List<Integer>> valores;

        Bucket(int localDepth) {
            this.localDepth = localDepth;
            this.valores = new LinkedHashMap<>();
        }

        boolean isFull(int bucketSize) {
            return valores.size() >= bucketSize;
        }

        void inserir(int chave, int valor) {
            valores.computeIfAbsent(chave, k -> new ArrayList<>()).add(valor);
        }

        List<Integer> buscar(int chave) {
            return valores.getOrDefault(chave, new ArrayList<>());
        }

        boolean remover(int chave, int valor) {
            List<Integer> lista = valores.get(chave);
            if (lista == null) return false;

            boolean removido = lista.remove(Integer.valueOf(valor));
            if (removido && lista.isEmpty()) {
                valores.remove(chave);
            }
            return removido;
        }

        List<Integer> listarTodosValores() {
            List<Integer> todos = new ArrayList<>();
            for (List<Integer> l : valores.values()) {
                todos.addAll(l);
            }
            return todos;
        }
    }
}
