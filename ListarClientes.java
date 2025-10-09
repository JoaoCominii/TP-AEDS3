import java.io.File;
import java.io.RandomAccessFile;

public class ListarClientes {
    public static void main(String[] args) throws Exception {
        String path = "./dados/clientes/clientes.db";
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("Arquivo de clientes nao encontrado em: " + path);
            return;
        }

        RandomAccessFile arquivo = new RandomAccessFile(f, "r");
    // Arquivo.java grava um int (4 bytes) seguido por um long (8 bytes) no cabeçalho
    final int TAM_CABECALHO = 4 + 8; // 12

        try {
            if (arquivo.length() <= TAM_CABECALHO) {
                System.out.println("Arquivo de clientes vazio ou corrompido.");
                return;
            }

            arquivo.seek(TAM_CABECALHO);
            System.out.println("ID\tNome\tCPF\tSalario\tNascimento");

            while (arquivo.getFilePointer() < arquivo.length()) {
                // leia lápide e tamanho com segurança
                byte lapide = arquivo.readByte();
                short tamanho = arquivo.readShort();

                // validações básicas para evitar NegativeArraySizeException ou leitura além do arquivo
                if (tamanho <= 0) {
                    // pula registro inválido
                    System.out.println("Encontrado tamanho de registro inválido: " + tamanho + ", interrompendo leitura.");
                    break;
                }

                long posDados = arquivo.getFilePointer();
                if (posDados + tamanho > arquivo.length()) {
                    System.out.println("Registro com tamanho que excede o arquivo, interrompendo leitura.");
                    break;
                }

                byte[] dados = new byte[tamanho];
                arquivo.readFully(dados);

                if (lapide == ' ') {
                    Cliente c = new Cliente();
                    c.fromByteArray(dados);
                    System.out.printf("%d\t%s\t%s\t%.2f\t%s%n",
                        c.getId(), c.getNome(), c.getCpf(), c.getSalario(), c.getNascimento().toString());
                }
            }
        } finally {
            arquivo.close();
        }
    }
}
