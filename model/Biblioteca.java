package model;

import java.io.*;

public class Biblioteca implements Registro {
    private int id;
    private String nome;
    private String descricao;
    private String status;

    public Biblioteca() {
        this(-1, "", "", "");
    }

    public Biblioteca(String nome, String descricao, String status) {
        this(-1, nome, descricao, status);
    }

    public Biblioteca(int id, String nome, String descricao, String status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.descricao);
        dos.writeUTF(this.status);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.descricao = dis.readUTF();
        this.status = dis.readUTF();
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Nome: " + nome + " | Status: " + status + " | Descricao: " + descricao;
    }
}
