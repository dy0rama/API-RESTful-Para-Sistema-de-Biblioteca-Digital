package com.example.biblioteca.digital.exceptions;

import java.util.UUID;

public class LivroIndisponivelException extends RuntimeException {
    public LivroIndisponivelException(UUID id) {
        super("Livro indisponível para empréstimo. ID: " + id);
    }
}
