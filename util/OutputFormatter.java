package util;

import java.time.format.DateTimeFormatter;
import model.Biblioteca;
import model.Cliente;

public class OutputFormatter {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static String formatCliente(Cliente c) {
        if (c == null) return "";
        String cadastro = (c.getCadastro() == null) ? "" : c.getCadastro().format(DATE_FMT);
        return "ID: " + c.getId() + " | Nome: " + c.getNome() + " | Email: " + c.getEmail() + " | Senha: " + c.getSenha() + " | Cadastro: " + cadastro;
    }

    public static String formatBiblioteca(Biblioteca b) {
        if (b == null) return "";
        return "ID: " + b.getId() + " | Nome: " + b.getNome() + " | Status: " + b.getStatus() + " | Descricao: " + b.getDescricao();
    }
}
