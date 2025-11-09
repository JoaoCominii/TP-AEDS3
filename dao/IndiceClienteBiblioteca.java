package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Biblioteca;

public class IndiceClienteBiblioteca {
    private ArvoreBMaisClienteBiblioteca arvore;
    private final String arqPath = "dados/bibliotecas/cliente_idx.db";
    private BibliotecaDAO bibliotecaDAO;

    @SuppressWarnings("unchecked")
    public IndiceClienteBiblioteca(BibliotecaDAO bibliotecaDAO) {
        this.bibliotecaDAO = bibliotecaDAO;
        File f = new File(arqPath);
        if (f.exists()) {
            try {
                this.arvore = ArvoreBMaisClienteBiblioteca.carregarDeDisco(arqPath);
                System.out.println("Índice Cliente->Biblioteca carregado do disco.");
            } catch (Exception e) {
                System.err.println("Erro ao carregar índice Cliente->Biblioteca: " + e.getMessage());
                this.arvore = new ArvoreBMaisClienteBiblioteca(5);
                reconstruirIndice();
            }
        } else {
            System.out.println("Índice Cliente->Biblioteca não encontrado. Criando um novo...");
            this.arvore = new ArvoreBMaisClienteBiblioteca(5);
            // A reconstrução será chamada pelo DAO se necessário
        }
    }

    public void salvarIndice() {
        try {
            File dir = new File("dados/bibliotecas");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            arvore.salvarEmDisco(arqPath);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o índice Cliente->Biblioteca: " + e.getMessage());
        }
    }

    public void reconstruirIndice() {
        System.out.println("Recriando índice Cliente->Biblioteca...");
        this.arvore = new ArvoreBMaisClienteBiblioteca(5);
        try {
            List<Biblioteca> todasBibliotecas = bibliotecaDAO.listarTodos();
            for (Biblioteca b : todasBibliotecas) {
                adicionarBiblioteca(b);
            }
            salvarIndice();
            System.out.println("Índice Cliente->Biblioteca recriado com sucesso.");
        } catch (Exception e) {
            System.err.println("Falha ao recriar o índice Cliente->Biblioteca: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void adicionarBiblioteca(Biblioteca b) {
        if (b != null && b.getClienteId() > 0) {
            arvore.inserir(b.getClienteId(), b.getId());
        }
    }

    public void removerBiblioteca(Biblioteca b) {
        if (b != null && b.getClienteId() > 0) {
            arvore.excluir(b.getClienteId(), b.getId());
        }
    }

    public List<Biblioteca> buscarBibliotecasPorCliente(int clienteId) {
        List<Integer> ids = arvore.buscar(clienteId);
        return buscarBibliotecasPorIds(ids);
    }

    private List<Biblioteca> buscarBibliotecasPorIds(List<Integer> ids) {
        List<Biblioteca> bibliotecas = new ArrayList<>();
        if (ids != null) {
            for (int id : ids) {
                try {
                    Biblioteca b = bibliotecaDAO.buscar(id);
                    if (b != null) {
                        bibliotecas.add(b);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao buscar biblioteca com ID " + id + ": " + e.getMessage());
                }
            }
        }
        return bibliotecas;
    }
}
