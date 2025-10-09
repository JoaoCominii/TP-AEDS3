package dao;

import model.Compra;

public class CompraDAO {
    private Arquivo<Compra> arq;

    public CompraDAO() throws Exception {
        arq = new Arquivo<>("compras", Compra.class.getConstructor());
    }

    public Compra buscar(int id) throws Exception { return arq.read(id); }
    public boolean incluir(Compra c) throws Exception { return arq.create(c) > 0; }
    public boolean alterar(Compra c) throws Exception { return arq.update(c); }
    public boolean excluir(int id) throws Exception { return arq.delete(id); }
}
