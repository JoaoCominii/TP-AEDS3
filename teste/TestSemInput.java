package teste;

import dao.JogoDAO;
import model.Jogo;
import java.util.List;

public class TestSemInput {
    public static void main(String[] args) {
        System.out.println("=== TESTE ÁRVORE B+ SEM INPUT ===");
        
        try {
            // Teste 1: Criar DAO
            System.out.println("1. Criando JogoDAO...");
            JogoDAO dao = new JogoDAO();
            System.out.println("JogoDAO criado!");
            
            // Teste 2: Criar alguns jogos para testar
            System.out.println("\n2. Adicionando jogos de teste...");
            
            Jogo jogo1 = new Jogo();
            jogo1.setId(1);
            jogo1.setNome("Minecraft");
            jogo1.setPreco(29.99);
            jogo1.setDescricao("Jogo de construção");
            jogo1.setPlataforma("PC");
            
            Jogo jogo2 = new Jogo();
            jogo2.setId(2);
            jogo2.setNome("GTA V");
            jogo2.setPreco(59.99);
            jogo2.setDescricao("Jogo de ação");
            jogo2.setPlataforma("PC");
            
            Jogo jogo3 = new Jogo();
            jogo3.setId(3);
            jogo3.setNome("Cyberpunk 2077");
            jogo3.setPreco(199.99);
            jogo3.setDescricao("RPG futurista");
            jogo3.setPlataforma("PC");
            
            System.out.println("✅ Jogos criados!");
            
            // Teste 3: Incluir jogos (isso vai alimentar a árvore B+)
            System.out.println("\n3. Incluindo jogos no DAO...");
            dao.incluir(jogo1);
            dao.incluir(jogo2);
            dao.incluir(jogo3);
            System.out.println("✅ Jogos incluídos!");
            
            // Teste 4: Buscar por preço exato
            System.out.println("\n4. Testando busca por preço exato (R$ 59.99)...");
            List<Jogo> resultado1 = dao.buscarPorPreco(59.99);
            System.out.println("Encontrados: " + resultado1.size() + " jogos");
            for (Jogo j : resultado1) {
                System.out.println("- " + j.getNome() + " (R$ " + j.getPreco() + ")");
            }
            
            // Teste 5: Buscar por faixa de preço
            System.out.println("\n5. Testando busca por faixa (R$ 30 - R$ 100)...");
            List<Jogo> resultado2 = dao.buscarPorFaixaPreco(30.0, 100.0);
            System.out.println("Encontrados: " + resultado2.size() + " jogos");
            for (Jogo j : resultado2) {
                System.out.println("- " + j.getNome() + " (R$ " + j.getPreco() + ")");
            }
            
            // Teste 6: Jogos em promoção
            System.out.println("\n6. Testando jogos em promoção (até R$ 30)...");
            List<Jogo> promocao = dao.buscarJogosPromocao();
            System.out.println("Encontrados: " + promocao.size() + " jogos");
            for (Jogo j : promocao) {
                System.out.println("- " + j.getNome() + " (R$ " + j.getPreco() + ")");
            }
            
            // Teste 7: Jogos premium
            System.out.println("\n7. Testando jogos premium (R$ 100+)...");
            List<Jogo> premium = dao.buscarJogosPremium();
            System.out.println("Encontrados: " + premium.size() + " jogos");
            for (Jogo j : premium) {
                System.out.println("- " + j.getNome() + " (R$ " + j.getPreco() + ")");
            }
            
            // Teste 8: Estatísticas
            System.out.println("\n8. Exibindo estatísticas...");
            dao.relatorioPorPreco();
            
            // Teste 9: Listar ordenados por preço
            System.out.println("\n9. Listando todos ordenados por preço...");
            List<Jogo> ordenados = dao.listarOrdenadosPorPreco();
            System.out.println("Total ordenados: " + ordenados.size() + " jogos");
            for (Jogo j : ordenados) {
                System.out.println("- " + j.getNome() + " (R$ " + j.getPreco() + ")");
            }
            
            System.out.println("\n✅ TODOS OS TESTES CONCLUÍDOS COM SUCESSO!");
            
        } catch (Exception e) {
            System.err.println("❌ ERRO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}