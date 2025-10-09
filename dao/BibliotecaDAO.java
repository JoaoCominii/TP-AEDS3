package dao;

import model.Biblioteca;
import dao.ClienteDAO;
import model.Cliente;

public class BibliotecaDAO {
    private Arquivo<Biblioteca> arq;

    public BibliotecaDAO() throws Exception {
        arq = new Arquivo<>("bibliotecas", Biblioteca.class.getConstructor());
    }

    public Biblioteca buscar(int id) throws Exception {
        return arq.read(id);
    }

    public boolean incluir(Biblioteca b) throws Exception {
        return arq.create(b) > 0;
    }

    public boolean alterar(Biblioteca b) throws Exception {
        return arq.update(b);
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
        return incluir(b);
    }

    public boolean alterarComValidacao(Biblioteca b) throws Exception {
        if (b.getClienteId() > 0) {
            ClienteDAO cd = new ClienteDAO();
            Cliente cli = cd.buscarCliente(b.getClienteId());
            if (cli == null) {
                System.out.println("Cliente informado não existe: " + b.getClienteId());
                return false;
            }
        }
        return alterar(b);
    }

    public boolean excluir(int id) throws Exception {
        return arq.delete(id);
    }
}
