public class TestAlterar {
    public static void main(String[] args) {
        try {
            ClienteDAO dao = new ClienteDAO();
            Cliente c = dao.buscarCliente(1);
            if (c == null) {
                System.out.println("Cliente 1 nao encontrado");
                return;
            }
            System.out.println("Antes: " + c);
            c.setNome(c.getNome() + "_ALTERADO");
            boolean ok = dao.alterarCliente(c);
            System.out.println("alterarCliente returned: " + ok);
            Cliente c2 = dao.buscarCliente(1);
            System.out.println("Depois: " + c2);
        } catch (Exception e) {
            System.out.println("Excecao ao alterar cliente:");
            e.printStackTrace();
        }
    }
}
