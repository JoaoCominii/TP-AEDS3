package dao;

import java.util.ArrayList;
import java.util.List;
import model.Biblioteca;
import model.Cliente;

public class BibliotecaDAO {
    private Arquivo<Biblioteca> arq;
    private final IndiceClienteHash indiceCliente;

    public BibliotecaDAO() throws Exception {
        arq = new Arquivo<>("bibliotecas", Biblioteca.class.getConstructor());
        indiceCliente = new IndiceClienteHash();
    }

    public List<Biblioteca> listarTodos() throws Exception {
        return arq.readAll();
    }

    public Biblioteca buscar(int id) throws Exception {
        return arq.read(id);
    }

    public List<Biblioteca> buscarPorCliente(int clienteId) throws Exception {
        List<Integer> bibliotecaIds = indiceCliente.buscar(clienteId);
        List<Biblioteca> bibliotecas = new ArrayList<>();
        for (int id : bibliotecaIds) {
            Biblioteca b = buscar(id);
            if (b != null) {
                bibliotecas.add(b);
            }
        }
        return bibliotecas;
    }

    public boolean incluirComValidacao(Biblioteca b) throws Exception {
        if (b.getClienteId() > 0) {
            ClienteDAO cd = new ClienteDAO();
            Cliente cli = cd.buscarCliente(b.getClienteId());
            if (cli == null) {
                System.out.println("Cliente informado não existe: " + b.getClienteId());
                return false;
            }
        }
        int id = arq.create(b);
        if (id > 0) {
            b.setId(id);
            if (b.getClienteId() > 0) {
                indiceCliente.inserir(b.getClienteId(), b.getId());
            }
        }
        return id > 0;
    }

    public boolean alterarComValidacao(Biblioteca b) throws Exception {
        Biblioteca bibliotecaAntiga = buscar(b.getId());
        
        if (b.getClienteId() > 0) {
            ClienteDAO cd = new ClienteDAO();
            Cliente cli = cd.buscarCliente(b.getClienteId());
            if (cli == null) {
                System.out.println("Cliente informado não existe: " + b.getClienteId());
                return false;
            }
        }

        boolean sucesso = arq.update(b);
        if (sucesso) {
            // Remove a associação antiga do índice
            if (bibliotecaAntiga != null && bibliotecaAntiga.getClienteId() > 0) {
                indiceCliente.remover(bibliotecaAntiga.getClienteId(), bibliotecaAntiga.getId());
            }
            // Adiciona a nova associação ao índice
            if (b.getClienteId() > 0) {
                indiceCliente.inserir(b.getClienteId(), b.getId());
            }
        }
        return sucesso;
    }

    public boolean excluir(int id) throws Exception {
        Biblioteca biblioteca = buscar(id);
        boolean sucesso = arq.delete(id);
        if (sucesso && biblioteca != null && biblioteca.getClienteId() > 0) {
            indiceCliente.remover(biblioteca.getClienteId(), biblioteca.getId());
        }
        return sucesso;
    }

    public void reconstruirIndiceCliente() throws Exception {
        List<Biblioteca> todas = listarTodos();
        indiceCliente.recriarIndice(todas);
    }
}
