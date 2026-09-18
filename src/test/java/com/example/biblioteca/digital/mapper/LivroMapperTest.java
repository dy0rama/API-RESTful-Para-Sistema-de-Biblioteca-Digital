package com.example.biblioteca.digital.mapper;

import com.example.biblioteca.digital.mapper.LivroMapper;
import com.example.biblioteca.digital.requests.LivroRequest;
import com.example.biblioteca.digital.entities.Livro;
import com.example.biblioteca.digital.responses.LivroResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LivroMapperTest {
    private final LivroMapper livroMapper = new LivroMapper();

    @Test
    void deveConverterRequestParaEntity() {
        LivroRequest request = new LivroRequest(
                "Clean Code",
                "Robert C. Martin",
                2008);

        Livro livro = livroMapper.toEntity(request);

        assertNotNull(livro);
        assertEquals("Clean Code", livro.getTitulo());
        assertEquals("Robert C. Martin", livro.getAutor());
        assertEquals(2008, livro.getAnoPublicacao());
        assertTrue(livro.isDisponivel());
    }

    @Test
    void deveConverterEntityParaResponse() {
        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        UUID id = UUID.randomUUID();

        // O ID é gerado pelo JPA normalmente.
        // Para o teste do Mapper, precisamos apenas simular um ID.
        try {
            var campoId = Livro.class.getDeclaredField("id");
            campoId.setAccessible(true);
            campoId.set(livro, id);
        } catch (Exception exception) {
            fail("Não foi possível configurar o ID do livro para o teste.");
        }

        LivroResponse response = livroMapper.toResponse(livro);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Clean Code", response.titulo());
        assertEquals("Robert C. Martin", response.autor());
        assertEquals(2008, response.anoPublicacao());
        assertTrue(response.disponivel());
    }
}
