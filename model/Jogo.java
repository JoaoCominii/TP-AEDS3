package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Jogo implements Registro {
    private int id;
    private String nome;
    private String descricao;
    private String tamanho;
    private short nota;
    private String plataforma;
    private double preco;
    private List<String> generos;
    private String classificacaoEtaria;

    public Jogo() {
        this(-1, "", "", "", (short)0, "", 0.0, new ArrayList<>(), "");
    }

    public Jogo(String nome, String descricao, String tamanho, short nota, String plataforma, double preco, List<String> generos, String classificacaoEtaria) {
        this(-1, nome, descricao, tamanho, nota, plataforma, preco, generos, classificacaoEtaria);
    }

    public Jogo(int id, String nome, String descricao, String tamanho, short nota, String plataforma, double preco, List<String> generos, String classificacaoEtaria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.nota = nota;
        this.plataforma = plataforma;
        this.preco = preco;
        this.generos = (generos == null) ? new ArrayList<>() : generos;
        this.classificacaoEtaria = classificacaoEtaria;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }
    public short getNota() { return nota; }
    public void setNota(short nota) { this.nota = nota; }
    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public List<String> getGeneros() { return generos; }
    public void setGeneros(List<String> generos) { this.generos = generos; }
    public String getClassificacaoEtaria() { return classificacaoEtaria; }
    public void setClassificacaoEtaria(String classificacaoEtaria) { this.classificacaoEtaria = classificacaoEtaria; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome == null ? "" : this.nome);
        dos.writeUTF(this.descricao == null ? "" : this.descricao);
        dos.writeUTF(this.tamanho == null ? "" : this.tamanho);
    short notaToWrite = this.nota;
    if (notaToWrite < 0) notaToWrite = 0;
    if (notaToWrite > 5) notaToWrite = 5;
    dos.writeShort(notaToWrite);
        dos.writeUTF(this.plataforma == null ? "" : this.plataforma);
        dos.writeDouble(this.preco);
        // generos: first write count, then each genero as UTF
        if (this.generos == null) {
            dos.writeInt(0);
        } else {
            dos.writeInt(this.generos.size());
            for (String g : this.generos) {
                dos.writeUTF(g == null ? "" : g);
            }
        }
        dos.writeUTF(this.classificacaoEtaria == null ? "" : this.classificacaoEtaria);
        dos.flush();
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.descricao = dis.readUTF();
        this.tamanho = dis.readUTF();
    this.nota = dis.readShort();
    // clamp legacy or out-of-range values to 0-5
    if (this.nota < 0) this.nota = 0;
    if (this.nota > 5) this.nota = 5;
        this.plataforma = dis.readUTF();
        this.preco = dis.readDouble();
        // generos
        int count = dis.readInt();
        this.generos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            this.generos.add(dis.readUTF());
        }
        this.classificacaoEtaria = dis.readUTF();
    }

    @Override
    public String toString() {
        String gens = String.join(",", this.generos == null ? new ArrayList<>() : this.generos);
        String precoStr = String.format("%.2f", this.preco);
        return "ID: " + id + " | Nome: " + nome + " | Descricao: " + descricao + " | Tamanho: " + tamanho + " | Nota: " + nota + " | Plataforma: " + plataforma + " | Preco: " + precoStr + " | Generos: " + gens + " | Classificacao: " + classificacaoEtaria;
    }
}
