package model;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Cliente implements Registro{
    private int id;
    private String nome;
    private String email;
    private String senha;
    private LocalDate cadastro;

    public Cliente() {
        this(-1, "", "", "", LocalDate.now());
    }

    public Cliente(String n, String e, String s, LocalDate d) {
        this(-1, n, e, s, d);
    }

    public Cliente(int i, String n, String e, String s, LocalDate d) {
        this.id = i;
        this.nome = n;
        this.email = e;
        this.senha = s;
        this.cadastro = d;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public LocalDate getCadastro() { return cadastro; }
    public void setCadastro(LocalDate cadastro) { this.cadastro = cadastro; }

    // Implementação do método toByteArray()
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.email);
        dos.writeUTF(this.senha);
        dos.writeLong(this.cadastro.toEpochDay());
        return baos.toByteArray();
    }

    // Implementação do método fromByteArray()
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.email = dis.readUTF();
        this.senha = dis.readUTF();
        this.cadastro = LocalDate.ofEpochDay(dis.readLong());
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String cadastroStr = (this.cadastro == null) ? "" : this.cadastro.format(fmt);
        return "\nID........: " + this.id +
               "\nNome......: " + this.nome +
               "\nEmail.....: " + this.email +
               "\nSenha.....: " + this.senha +
               "\nCadastro..: " + cadastroStr;
    }
}
