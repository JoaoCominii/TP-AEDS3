package model;

import java.io.*;
import java.time.LocalDate;

public class CompraJogo implements Registro {
    // O campo 'id' não é mais a chave primária, mas é mantido para compatibilidade com a interface Registro.
    // A chave primária real é a combinação de (idCompra, idJogo).
    private int id; 
    private int idCompra;
    private int idJogo;
    private double precoPago;
    private long dataAdicao; // Armazenado como Epoch Day

    public CompraJogo() {
        this(-1, -1, 0.0, LocalDate.now());
    }

    public CompraJogo(int idCompra, int idJogo, double precoPago) {
        this(idCompra, idJogo, precoPago, LocalDate.now());
    }

    public CompraJogo(int idCompra, int idJogo, double precoPago, LocalDate dataAdicao) {
        // O ID único não é mais necessário, a chave é composta.
        // Atribuímos um valor padrão, mas ele não será usado para identificação.
        this.id = -1; 
        this.idCompra = idCompra;
        this.idJogo = idJogo;
        this.precoPago = precoPago;
        this.setDataAdicao(dataAdicao);
    }

    // Getters
    public int getId() { return id; } // Mantido por compatibilidade
    public int getIdCompra() { return idCompra; }
    public int getIdJogo() { return idJogo; }
    public double getPrecoPago() { return precoPago; }
    public LocalDate getDataAdicao() { return LocalDate.ofEpochDay(this.dataAdicao); }

    // Setters
    public void setId(int id) { this.id = id; } // Mantido por compatibilidade
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }
    public void setIdJogo(int idJogo) { this.idJogo = idJogo; }
    public void setPrecoPago(double precoPago) { this.precoPago = precoPago; }
    public void setDataAdicao(LocalDate dataAdicao) { this.dataAdicao = dataAdicao.toEpochDay(); }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        // O 'id' não é mais a chave, mas o serializamos para manter a estrutura do registro.
        dos.writeInt(id); 
        dos.writeInt(idCompra);
        dos.writeInt(idJogo);
        dos.writeDouble(precoPago);
        dos.writeLong(dataAdicao);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt(); // Mantido por compatibilidade
        this.idCompra = dis.readInt();
        this.idJogo = dis.readInt();
        this.precoPago = dis.readDouble();
        this.dataAdicao = dis.readLong();
    }

    @Override
    public String toString() {
        return "CompraJogo [ID Compra=" + idCompra + ", ID Jogo=" + idJogo + 
               ", Preço Pago=R$" + String.format("%.2f", precoPago) + 
               ", Data=" + getDataAdicao() + "]";
    }
}
