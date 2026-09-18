package com.example.biblioteca.digital.mapper;

import com.example.biblioteca.digital.entities.Livro;
import com.example.biblioteca.digital.requests.LivroRequest;
import com.example.biblioteca.digital.responses.LivroResponse;
import org.springframework.stereotype.Component;

@Component
public class LivroMapper {
    public Livro toEntity(LivroRequest request) {
        return new Livro(
                request.titulo(),
                request.autor(),
                request.anoPublicacao());
    }

    public LivroResponse toResponse(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getAnoPublicacao(),
                livro.isDisponivel());
    }
}
