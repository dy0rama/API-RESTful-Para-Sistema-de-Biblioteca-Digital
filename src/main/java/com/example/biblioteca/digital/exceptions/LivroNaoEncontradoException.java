package com.example.biblioteca.digital.exceptions;

import java.util.UUID;

public class LivroNaoEncontradoException extends RuntimeException {
    public LivroNaoEncontradoException(UUID id) {
        super("Livro não encontrado com ID: " + id);
    }
}
