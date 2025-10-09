package dao;

import model.Biblioteca;

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

    public boolean excluir(int id) throws Exception {
        return arq.delete(id);
    }
}
