package model;

import java.io.*;
import java.time.LocalDate;

public class Compra implements Registro {
    private int id;
    private String status;
    private double valor;
    private LocalDate data;
    private int clienteId;

    public Compra() {
        this(-1, "", 0.0, LocalDate.now(), -1);
    }

    public Compra(String status, double valor) {
        this(-1, status, valor, LocalDate.now(), -1);
    }

    public Compra(int id, String status, double valor, LocalDate data, int clienteId) {
        this.id = id;
        this.status = status;
        this.valor = valor;
        this.data = data;
        this.clienteId = clienteId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(this.id);
    dos.writeInt(this.clienteId);
    dos.writeUTF(this.status == null ? "" : this.status);
    dos.writeDouble(this.valor);
    dos.writeLong(this.data == null ? LocalDate.now().toEpochDay() : this.data.toEpochDay());
        dos.flush();
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        // Try new layout: id, clienteId, status, valor, data
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        try {
            this.id = dis.readInt();
            this.clienteId = dis.readInt();
            this.status = dis.readUTF();
            this.valor = dis.readDouble();
            this.data = LocalDate.ofEpochDay(dis.readLong());
        } catch (IOException ex) {
            // fallback to old layout: id, status, valor, data
            ByteArrayInputStream bais2 = new ByteArrayInputStream(b);
            DataInputStream dis2 = new DataInputStream(bais2);
            this.id = dis2.readInt();
            this.clienteId = -1;
            this.status = dis2.readUTF();
            this.valor = dis2.readDouble();
            this.data = LocalDate.ofEpochDay(dis2.readLong());
        }
    }

    @Override
    public String toString() {
        String valorStr = String.format("%.2f", valor);
        return "ID: " + id + " | ClienteID: " + clienteId + " | Status: " + status + " | Valor: " + valorStr + " | Data: " + data;
    }
}
