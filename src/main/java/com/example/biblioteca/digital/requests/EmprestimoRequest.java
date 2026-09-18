package com.example.biblioteca.digital.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EmprestimoRequest(
        @NotNull(message = "O ID do usuário é obrigatório")
        UUID usuarioId,

        @NotNull(message = "O ID do livro é obrigatório")
        UUID livroId
) {
}
