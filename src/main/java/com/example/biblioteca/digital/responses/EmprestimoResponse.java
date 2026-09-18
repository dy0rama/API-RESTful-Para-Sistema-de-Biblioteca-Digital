package com.example.biblioteca.digital.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmprestimoResponse(
        UUID id,
        UUID usuarioId,
        String nomeUsuario,
        UUID livroId,
        String tituloLivro,
        LocalDateTime dataEmprestimo,
        LocalDateTime dataDevolucao
) {
}
