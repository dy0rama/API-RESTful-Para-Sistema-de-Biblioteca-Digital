package com.example.biblioteca.digital.repositories;

import com.example.biblioteca.digital.entities.Emprestimo;
import com.example.biblioteca.digital.entities.Livro;
import com.example.biblioteca.digital.entities.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmprestimoRepositoryTest {
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveSalvarEBuscarEmprestimo() {
        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@teste.com",
                "123456");

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        usuario = usuarioRepository.save(usuario);
        livro = livroRepository.save(livro);

        Emprestimo emprestimo = new Emprestimo(usuario, livro, LocalDateTime.now());

        Emprestimo salvo = emprestimoRepository.save(emprestimo);

        assertNotNull(salvo.getId());

        Emprestimo encontrado = emprestimoRepository.findById(salvo.getId()).orElseThrow();

        assertEquals(usuario.getId(), encontrado.getUsuario().getId());
        assertEquals(livro.getId(), encontrado.getLivro().getId());
        assertNotNull(encontrado.getDataEmprestimo());
        assertNull(encontrado.getDataDevolucao());
    }
}
