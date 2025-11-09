package dao;
import java.util.ArrayList;
import java.util.List;
import model.Jogo;

public class JogoDAO {
    private Arquivo<Jogo> arq;
    private IndicePrecoJogo indicePreco;
    private CompraJogoDAO compraJogoDAO;
    
    public JogoDAO() throws Exception {
        arq = new Arquivo<>("jogos", Jogo.class.getConstructor());
        indicePreco = new IndicePrecoJogo(this);
        compraJogoDAO = new CompraJogoDAO();
    }
    
    public List<Jogo> listarTodos() throws Exception {
        List<Jogo> jogos = new ArrayList<>();
        
        int id = 1;
        int tentativasConsecutivasFalhas = 0;
        int maxFalhasConsecutivas = 10;
        
        while (tentativasConsecutivasFalhas < maxFalhasConsecutivas) {
            try {
                Jogo jogo = arq.read(id);
                if (jogo != null) {
                    jogos.add(jogo);
                    tentativasConsecutivasFalhas = 0; // Reset contador
                } else {
                    tentativasConsecutivasFalhas++;
                }
                id++;
            } catch (Exception e) {
                tentativasConsecutivasFalhas++;
                id++;
            }
        }
        
        return jogos;
    }
    
    // MÉTODOS ORIGINAIS
    public Jogo buscar(int id) throws Exception {
        return arq.read(id);
    }
    
    public boolean incluir(Jogo j) throws Exception {
        boolean sucesso = arq.create(j) > 0;
        if (sucesso) {
            indicePreco.adicionarJogo(j);
            indicePreco.salvarIndice();
        }
        return sucesso;
    }
    
    public boolean alterar(Jogo j) throws Exception {
        Jogo jogoAntigo = buscar(j.getId());
        if (jogoAntigo != null) {
            indicePreco.removerJogo(jogoAntigo);
        }
        
        boolean sucesso = arq.update(j);
        if (sucesso) {
            indicePreco.adicionarJogo(j);
            indicePreco.salvarIndice();
        }
        return sucesso;
    }
    
    public boolean excluir(int id) throws Exception {
        // Cascade delete: remove all related CompraJogo entries first
        java.util.List<Integer> idsCompra = compraJogoDAO.getIdsCompraPorJogo(id);
        if (idsCompra != null) {
            for (int idCompra : idsCompra) {
                compraJogoDAO.delete(idCompra, id);
            }
        }

        Jogo jogo = buscar(id);
        boolean sucesso = arq.delete(id);
        if (sucesso && jogo != null) {
            indicePreco.removerJogo(jogo);
            indicePreco.salvarIndice();
        }
        return sucesso;
    }
    
    // Buscas por preço usando Árvore B+
    public List<Jogo> buscarPorPreco(double preco) {
        return indicePreco.buscarPorPreco(preco);
    }
    
    public List<Jogo> buscarPorFaixaPreco(double precoMin, double precoMax) {
        return indicePreco.buscarPorFaixaPreco(precoMin, precoMax);
    }
    
    public List<Jogo> buscarJogosPromocao() {
        return indicePreco.buscarJogosPromocao();
    }
    
    public List<Jogo> buscarJogosPremium() {
        return indicePreco.buscarJogosPremium();
    }
    
    public List<Jogo> listarOrdenadosPorPreco() {
        return indicePreco.listarOrdenadosPorPreco();
    }
    
    public void reconstruirIndicePreco() {
        indicePreco.reconstruirIndice();
    }

    public void exibirEstatisticasPreco() {
        System.out.println("\n=== ESTATISTICAS DE PRECOS ===");
        
        List<Jogo> gratuitos = buscarPorPreco(0.0);
        List<Jogo> baratos = buscarPorFaixaPreco(0.01, 29.99);
        List<Jogo> medios = buscarPorFaixaPreco(30.0, 79.99);
        List<Jogo> caros = buscarPorFaixaPreco(80.0, 149.99);
        List<Jogo> premium = buscarJogosPremium();
        
        System.out.println("Gratuitos (R$ 0,00): " + gratuitos.size() + " jogos");
        System.out.println("Baratos (R$ 0,01 - 29,99): " + baratos.size() + " jogos");
        System.out.println("Medios (R$ 30,00 - 79,99): " + medios.size() + " jogos");
        System.out.println("Caros (R$ 80,00 - 149,99): " + caros.size() + " jogos");
        System.out.println("Premium (R$ 150,00+): " + premium.size() + " jogos");
        System.out.println("===============================\n");
    }
}