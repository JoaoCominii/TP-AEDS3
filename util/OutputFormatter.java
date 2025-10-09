package util;

import java.time.format.DateTimeFormatter;
import model.Biblioteca;
import model.Cliente;
import model.Jogo;

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

    public static String formatJogo(Jogo j) {
        if (j == null) return "";
        String gens = String.join(",", j.getGeneros() == null ? new java.util.ArrayList<>() : j.getGeneros());
        String precoStr = String.format("%.2f", j.getPreco());
        return "ID: " + j.getId() + " | Nome: " + j.getNome() + " | Descricao: " + j.getDescricao() + " | Tamanho: " + j.getTamanho() + " | Nota: " + j.getNota() + " | Plataforma: " + j.getPlataforma() + " | Preco: " + precoStr + " | Generos: " + gens + " | Classificacao: " + j.getClassificacaoEtaria();
    }
}
