package com.example.biblioteca.digital.responses;

import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email
) {
}
