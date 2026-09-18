package com.example.biblioteca.digital.mapper;

import com.example.biblioteca.digital.entities.Emprestimo;
import com.example.biblioteca.digital.entities.Livro;
import com.example.biblioteca.digital.entities.Usuario;
import com.example.biblioteca.digital.responses.EmprestimoResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoMapperTest {

    private final EmprestimoMapper emprestimoMapper = new EmprestimoMapper();

    @Test
    void deveConverterEmprestimoParaResponse() {
        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        LocalDateTime dataEmprestimo = LocalDateTime.of(2026, 9, 17, 10, 30);

        LocalDateTime dataDevolucao = LocalDateTime.of(2026, 9, 20, 14, 0);

        Emprestimo emprestimo = new Emprestimo(usuario, livro, dataEmprestimo);

        emprestimo.setDataDevolucao(dataDevolucao);

        EmprestimoResponse response = emprestimoMapper.toResponse(emprestimo);

        assertNotNull(response);

        assertEquals(usuario.getId(), response.usuarioId());
        assertEquals(usuario.getNome(), response.nomeUsuario());

        assertEquals(livro.getId(), response.livroId());
        assertEquals(livro.getTitulo(), response.tituloLivro());

        assertEquals(dataEmprestimo, response.dataEmprestimo());

        assertEquals(dataDevolucao, response.dataDevolucao());
    }

    @Test
    void deveConverterEmprestimoAtivoParaResponse() {
        Usuario usuario = new Usuario(
                "Maria Oliveira",
                "maria@email.com",
                "123456");

        Livro livro = new Livro(
                "Effective Java",
                "Joshua Bloch",
                2018);

        LocalDateTime dataEmprestimo = LocalDateTime.of(2026, 9, 17, 9, 0);

        Emprestimo emprestimo = new Emprestimo(usuario, livro, dataEmprestimo);

        EmprestimoResponse response = emprestimoMapper.toResponse(emprestimo);

        assertNotNull(response);

        assertEquals(usuario.getId(), response.usuarioId());
        assertEquals("Maria Oliveira", response.nomeUsuario());

        assertEquals(livro.getId(), response.livroId());
        assertEquals("Effective Java", response.tituloLivro());

        assertEquals(dataEmprestimo, response.dataEmprestimo());

        assertNull(response.dataDevolucao());
    }
}
