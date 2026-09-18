package com.example.biblioteca.digital.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LivroRequest(
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotBlank(message = "O autor é obrigatório")
        String autor,

        @NotNull(message = "O ano de publicação é obrigatório")
        @Min(value = 1, message = "O ano de publicação deve ser maior que zero")
        Integer anoPublicacao
) {
}
