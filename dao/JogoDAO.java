package dao;

import model.Jogo;

public class JogoDAO {
    private Arquivo<Jogo> arq;

    public JogoDAO() throws Exception {
        arq = new Arquivo<>("jogos", Jogo.class.getConstructor());
    }

    public Jogo buscar(int id) throws Exception {
        return arq.read(id);
    }

    public boolean incluir(Jogo j) throws Exception {
        return arq.create(j) > 0;
    }

    public boolean alterar(Jogo j) throws Exception {
        return arq.update(j);
    }

    public boolean excluir(int id) throws Exception {
        return arq.delete(id);
    }
}
