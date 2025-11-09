package dao;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import model.CompraJogo;

public class ArquivoCompraJogo {
    private RandomAccessFile arquivo;
    private final String NOME_ARQUIVO = "dados/comprajogo/comprajogo.db";

    public ArquivoCompraJogo() throws Exception {
        File dir = new File("dados/comprajogo");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        arquivo = new RandomAccessFile(NOME_ARQUIVO, "rw");
    }

    public void create(CompraJogo obj) throws Exception {
        arquivo.seek(arquivo.length()); // Ir para o final do arquivo
        byte[] dados = obj.toByteArray();
        arquivo.writeByte(' '); // Lápide
        arquivo.writeShort(dados.length);
        arquivo.write(dados);
    }

    public CompraJogo read(int idCompra, int idJogo) throws Exception {
        arquivo.seek(0);
        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            
            if (lapide == ' ') {
                byte[] dados = new byte[tamanho];
                arquivo.read(dados);
                CompraJogo obj = new CompraJogo();
                obj.fromByteArray(dados);
                if (obj.getIdCompra() == idCompra && obj.getIdJogo() == idJogo) {
                    return obj;
                }
            } else {
                arquivo.skipBytes(tamanho);
            }
        }
        return null;
    }

    public List<CompraJogo> readAll() throws Exception {
        List<CompraJogo> lista = new ArrayList<>();
        arquivo.seek(0);
        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                CompraJogo obj = new CompraJogo();
                obj.fromByteArray(dados);
                lista.add(obj);
            }
        }
        return lista;
    }

    public boolean delete(int idCompra, int idJogo) throws Exception {
        arquivo.seek(0);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long pos = arquivo.getFilePointer();
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            
            if (lapide == ' ') {
                byte[] dados = new byte[tamanho];
                arquivo.read(dados); // Lê os dados para poder comparar
                CompraJogo obj = new CompraJogo();
                obj.fromByteArray(dados);
                if (obj.getIdCompra() == idCompra && obj.getIdJogo() == idJogo) {
                    arquivo.seek(pos);
                    arquivo.writeByte('*'); // Marca a lápide
                    return true;
                }
            } else {
                 arquivo.skipBytes(tamanho);
            }
        }
        return false;
    }

    public boolean update(CompraJogo novoObj) throws Exception {
        // A atualização com registros de tamanho variável sem uma lista livre é complexa.
        // A estratégia mais simples e segura é deletar o antigo e criar um novo.
        if (delete(novoObj.getIdCompra(), novoObj.getIdJogo())) {
            try {
                create(novoObj);
                return true;
            } catch (Exception e) {
                // Se a criação falhar, idealmente deveríamos restaurar o antigo.
                // Por simplicidade, apenas reportamos o erro.
                System.err.println("ERRO CRÍTICO: O registro antigo foi removido, mas o novo não pôde ser criado. " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public void close() throws Exception {
        arquivo.close();
    }

    public void apagarTudo() throws Exception {
        arquivo.close();
        File f = new File(NOME_ARQUIVO);
        if (f.exists()) {
            f.delete();
        }
        arquivo = new RandomAccessFile(NOME_ARQUIVO, "rw");
    }
}
