package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BibliotecaJogo implements Registro {
    private int id;
    private int bibliotecaId;
    private int jogoId;

    public BibliotecaJogo() {
        this(-1, -1, -1);
    }

    public BibliotecaJogo(int bibliotecaId, int jogoId) {
        this(-1, bibliotecaId, jogoId);
    }

    public BibliotecaJogo(int id, int bibliotecaId, int jogoId) {
        this.id = id;
        this.bibliotecaId = bibliotecaId;
        this.jogoId = jogoId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getBibliotecaId() {
        return bibliotecaId;
    }

    public void setBibliotecaId(int bibliotecaId) {
        this.bibliotecaId = bibliotecaId;
    }

    public int getJogoId() {
        return jogoId;
    }

    public void setJogoId(int jogoId) {
        this.jogoId = jogoId;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(bibliotecaId);
        dos.writeInt(jogoId);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.bibliotecaId = dis.readInt();
        this.jogoId = dis.readInt();
    }

    @Override
    public String toString() {
        return "BibliotecaJogo [id=" + id + ", bibliotecaId=" + bibliotecaId + ", jogoId=" + jogoId + "]";
    }
}
