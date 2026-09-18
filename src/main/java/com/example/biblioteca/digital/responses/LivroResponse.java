package com.example.biblioteca.digital.responses;

import java.util.List;
import java.util.UUID;

public record LivroResponse(
        UUID id,
        String titulo,
        String autor,
        Integer anoPublicacao,
        boolean disponivel
) {
}
