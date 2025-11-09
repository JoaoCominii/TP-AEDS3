package dao;

import java.io.IOException;

public class IndiceClienteHash {
    private static final String NOME_ARQUIVO_INDICE = "dados/cliente_bibliotecas_hash.db";
    private HashExtensivel indice;

    public IndiceClienteHash() {
        try {
            this.indice = HashExtensivel.carregarDeDisco(NOME_ARQUIVO_INDICE);
        } catch (IOException | ClassNotFoundException e) {
            // Se não for possível carregar, cria um novo índice
            this.indice = new HashExtensivel(4); // Bucket size de 4
        }
    }

    public void inserir(int idCliente, int idBiblioteca) {
        indice.inserir(idCliente, idBiblioteca);
        salvarIndice();
    }

    public java.util.List<Integer> buscar(int idCliente) {
        return indice.buscar(idCliente);
    }

    public boolean remover(int idCliente, int idBiblioteca) {
        boolean removido = indice.remover(idCliente, idBiblioteca);
        if (removido) {
            salvarIndice();
        }
        return removido;
    }

    public void salvarIndice() {
        try {
            indice.salvarEmDisco(NOME_ARQUIVO_INDICE);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o índice de hash de clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void recriarIndice(java.util.List<model.Biblioteca> bibliotecas) {
        this.indice = new HashExtensivel(4); // Reinicia o índice
        for (model.Biblioteca b : bibliotecas) {
            if (b.getClienteId() > 0) { // Apenas se houver um cliente associado
                this.indice.inserir(b.getClienteId(), b.getId());
            }
        }
        salvarIndice();
        System.out.println("Índice de hash cliente -> bibliotecas recriado com sucesso.");
    }
}
