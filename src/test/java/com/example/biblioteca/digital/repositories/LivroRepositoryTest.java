package com.example.biblioteca.digital.repositories;

import com.example.biblioteca.digital.entities.Livro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LivroRepositoryTest {
    @Autowired
    private LivroRepository livroRepository;

    @Test
    void devePersistirLivroNoBanco() {
        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008
        );

        Livro livroSalvo = livroRepository.save(livro);

        assertNotNull(livroSalvo.getId());
        assertEquals("Clean Code", livroSalvo.getTitulo());
        assertEquals("Robert C. Martin", livroSalvo.getAutor());
        assertEquals(2008, livroSalvo.getAnoPublicacao());
        assertTrue(livroSalvo.isDisponivel());
    }
}
