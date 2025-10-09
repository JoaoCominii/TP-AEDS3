package model;

import java.io.*;
import java.time.LocalDate;

public class Compra implements Registro {
    private int id;
    private String status;
    private double valor;
    private LocalDate data;

    public Compra() {
        this(-1, "", 0.0, LocalDate.now());
    }

    public Compra(String status, double valor) {
        this(-1, status, valor, LocalDate.now());
    }

    public Compra(int id, String status, double valor, LocalDate data) {
        this.id = id;
        this.status = status;
        this.valor = valor;
        this.data = data;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.status == null ? "" : this.status);
        dos.writeDouble(this.valor);
        dos.writeLong(this.data == null ? LocalDate.now().toEpochDay() : this.data.toEpochDay());
        dos.flush();
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.status = dis.readUTF();
        this.valor = dis.readDouble();
        this.data = LocalDate.ofEpochDay(dis.readLong());
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Status: " + status + " | Valor: " + valor + " | Data: " + data;
    }
}
