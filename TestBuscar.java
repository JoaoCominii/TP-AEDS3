public class TestBuscar {
    public static void main(String[] args) {
        try {
            ClienteDAO dao = new ClienteDAO();
            Cliente c = dao.buscarCliente(1);
            if (c != null) {
                System.out.println("Encontrado: " + c);
            } else {
                System.out.println("Cliente retornou null");
            }
        } catch (Exception e) {
            System.out.println("Excecao ao buscar cliente:");
            e.printStackTrace();
        }
    }
}
