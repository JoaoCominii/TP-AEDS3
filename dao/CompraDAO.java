package dao;

import model.Compra;
import dao.ClienteDAO;
import model.Cliente;

public class CompraDAO {
    private Arquivo<Compra> arq;

    public CompraDAO() throws Exception {
        arq = new Arquivo<>("compras", Compra.class.getConstructor());
    }

    public Compra buscar(int id) throws Exception { return arq.read(id); }
    public boolean incluir(Compra c) throws Exception { return arq.create(c) > 0; }
    public boolean alterar(Compra c) throws Exception { return arq.update(c); }
    // valida cliente existe se clienteId > 0
    public boolean incluirComValidacao(Compra c) throws Exception {
        if (c.getClienteId() > 0) {
            ClienteDAO cd = new ClienteDAO();
            Cliente cli = cd.buscarCliente(c.getClienteId());
            if (cli == null) {
                System.out.println("Cliente informado não existe: " + c.getClienteId());
                return false;
            }
        }
        return incluir(c);
    }

    public boolean alterarComValidacao(Compra c) throws Exception {
        if (c.getClienteId() > 0) {
            ClienteDAO cd = new ClienteDAO();
            Cliente cli = cd.buscarCliente(c.getClienteId());
            if (cli == null) {
                System.out.println("Cliente informado não existe: " + c.getClienteId());
                return false;
            }
        }
        return alterar(c);
    }
    public boolean excluir(int id) throws Exception { return arq.delete(id); }
}
