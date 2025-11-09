package dao;

import java.util.ArrayList;
import java.util.List;
import model.Biblioteca;
import model.Cliente;

public class BibliotecaDAO {
    private Arquivo<Biblioteca> arq;
    private IndiceClienteBiblioteca indiceCliente;

    public BibliotecaDAO() throws Exception {
        arq = new Arquivo<>("bibliotecas", Biblioteca.class.getConstructor());
        indiceCliente = new IndiceClienteBiblioteca(this);
        // A reconstrução do índice será feita se o arquivo de índice não existir.
        // A verificação de arq.length() == 0 pode ser uma alternativa se o método isEmpty() não existir.
    }

    public List<Biblioteca> listarTodos() throws Exception {
        List<Biblioteca> bibliotecas = new ArrayList<>();
        int id = 1;
        int tentativasConsecutivasFalhas = 0;
        int maxFalhasConsecutivas = 10;

        while (tentativasConsecutivasFalhas < maxFalhasConsecutivas) {
            try {
                Biblioteca biblioteca = arq.read(id);
                if (biblioteca != null) {
                    bibliotecas.add(biblioteca);
                    tentativasConsecutivasFalhas = 0;
                } else {
                    tentativasConsecutivasFalhas++;
                }
                id++;
            } catch (Exception e) {
                tentativasConsecutivasFalhas++;
                id++;
            }
        }
        return bibliotecas;
    }

    public Biblioteca buscar(int id) throws Exception {
        return arq.read(id);
    }

    public List<Biblioteca> buscarPorCliente(int clienteId) {
        return indiceCliente.buscarBibliotecasPorCliente(clienteId);
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
        boolean sucesso = arq.create(b) > 0;
        if (sucesso) {
            indiceCliente.adicionarBiblioteca(b);
            indiceCliente.salvarIndice();
        }
        return sucesso;
    }

    public boolean alterarComValidacao(Biblioteca b) throws Exception {
        Biblioteca bibliotecaAntiga = buscar(b.getId());
        if (bibliotecaAntiga != null) {
            indiceCliente.removerBiblioteca(bibliotecaAntiga);
        }

        if (b.getClienteId() > 0) {
            ClienteDAO cd = new ClienteDAO();
            Cliente cli = cd.buscarCliente(b.getClienteId());
            if (cli == null) {
                System.out.println("Cliente informado não existe: " + b.getClienteId());
                // Re-adiciona o antigo ao índice se a validação falhar
                if (bibliotecaAntiga != null) {
                    indiceCliente.adicionarBiblioteca(bibliotecaAntiga);
                }
                return false;
            }
        }

        boolean sucesso = arq.update(b);
        if (sucesso) {
            indiceCliente.adicionarBiblioteca(b);
            indiceCliente.salvarIndice();
        } else if (bibliotecaAntiga != null) {
            // Se a atualização falhar, restaura o índice
            indiceCliente.adicionarBiblioteca(bibliotecaAntiga);
        }
        return sucesso;
    }

    public boolean excluir(int id) throws Exception {
        Biblioteca biblioteca = buscar(id);
        boolean sucesso = arq.delete(id);
        if (sucesso && biblioteca != null) {
            indiceCliente.removerBiblioteca(biblioteca);
            indiceCliente.salvarIndice();
        }
        return sucesso;
    }

    public void reconstruirIndiceCliente() {
        indiceCliente.reconstruirIndice();
    }
}
