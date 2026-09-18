package com.example.biblioteca.digital.exceptions;

import java.util.UUID;

public class EmprestimoJaDevolvidoException extends RuntimeException {
    public EmprestimoJaDevolvidoException(UUID id) {
        super("Empréstimo já foi devolvido. ID: " + id);
    }
}
