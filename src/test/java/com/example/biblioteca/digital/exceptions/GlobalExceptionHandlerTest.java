package com.example.biblioteca.digital.exceptions;

import com.example.biblioteca.digital.controller.GlobalExceptionHandlerTestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTestController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornar404QuandoLivroNaoForEncontrado() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/teste/livros/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Livro não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Livro não encontrado com ID: " + id))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoForEncontrado() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/teste/usuarios/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Usuário não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Usuário não encontrado com ID: " + id))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornar404QuandoEmprestimoNaoForEncontrado()
            throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/teste/emprestimos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro")
                        .value("Empréstimo não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Empréstimo não encontrado com ID: " + id));
    }

    @Test
    void deveRetornar409QuandoEmprestimoJaFoiDevolvido()
            throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/teste/emprestimos/{id}/devolver", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro")
                        .value("Empréstimo já devolvido"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Empréstimo já foi devolvido. ID: " + id));
    }

    @Test
    void deveRetornar409QuandoLivroEstiverIndisponivel()
            throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/teste/livros/{id}/emprestar", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro")
                        .value("Livro indisponível"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Livro indisponível para empréstimo. ID: " + id));
    }
}
