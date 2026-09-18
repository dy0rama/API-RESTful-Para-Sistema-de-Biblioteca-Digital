package com.example.biblioteca.digital.exceptions;

import java.util.UUID;

public class EmprestimoNaoEncontradoException extends RuntimeException {
    public EmprestimoNaoEncontradoException(UUID id) {
        super("Empréstimo não encontrado com ID: " + id);
    }
}
