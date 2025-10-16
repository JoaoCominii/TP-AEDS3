package teste;

public class TestMinimo {
    public static void main(String[] args) {
        System.out.println("=== TESTE MÍNIMO ===");
        
        try {
            System.out.println("1. Testando imports...");
            
            // Teste 1: Apenas criar um Jogo
            System.out.println("2. Criando objeto Jogo...");
            model.Jogo jogo = new model.Jogo();
            System.out.println("✅ Jogo criado!");
            
            // Teste 2: Testar Arquivo (pode ser aqui o problema)
            System.out.println("3. Testando classe Arquivo...");
            dao.Arquivo<model.Jogo> arq = new dao.Arquivo<>("teste_jogos", model.Jogo.class.getConstructor());
            System.out.println("✅ Arquivo criado!");
            
            // Teste 3: Criar DAO sem índice
            System.out.println("4. Criando JogoDAO original...");
            dao.JogoDAO dao = new dao.JogoDAO();
            System.out.println("✅ JogoDAO criado!");
            
            System.out.println("\n✅ TUDO FUNCIONOU!");
            
        } catch (Exception e) {
            System.err.println("❌ ERRO em: " + e.getMessage());
            System.err.println("Tipo: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}