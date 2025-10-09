package model;

import java.io.*;

public class Biblioteca implements Registro {
    private int id;
    private String nome;
    private String descricao;
    private String status;
    private int clienteId;

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
        this.clienteId = -1;
    }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

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
        dos.writeInt(this.clienteId);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.descricao);
        dos.writeUTF(this.status);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        // Try parsing new layout (id, clienteId, nome, descricao, status).
        // If that fails (old DB files), fall back to old layout (id, nome, descricao, status).
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        try {
            this.id = dis.readInt();
            this.clienteId = dis.readInt();
            this.nome = dis.readUTF();
            this.descricao = dis.readUTF();
            this.status = dis.readUTF();
        } catch (IOException ex) {
            // fallback: try old layout
            ByteArrayInputStream bais2 = new ByteArrayInputStream(b);
            DataInputStream dis2 = new DataInputStream(bais2);
            this.id = dis2.readInt();
            this.clienteId = -1; // unknown in old records
            this.nome = dis2.readUTF();
            this.descricao = dis2.readUTF();
            this.status = dis2.readUTF();
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + " | ClienteID: " + clienteId + " | Nome: " + nome + " | Status: " + status + " | Descricao: " + descricao;
    }
}
