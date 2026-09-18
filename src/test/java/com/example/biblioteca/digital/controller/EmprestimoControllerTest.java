package com.example.biblioteca.digital.controller;

import com.example.biblioteca.digital.responses.EmprestimoResponse;
import com.example.biblioteca.digital.services.EmprestimoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmprestimoController.class)
class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmprestimoService emprestimoService;

    @Test
    void deveCriarEmprestimo() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();
        UUID emprestimoId = UUID.randomUUID();

        EmprestimoResponse response = new EmprestimoResponse(
                emprestimoId,
                usuarioId,
                "João da Silva",
                livroId,
                "Clean Code",
                LocalDateTime.now(),
                null);

        when(emprestimoService.criar(any())).thenReturn(response);

        String request = """
                {
                    "usuarioId": "%s",
                    "livroId": "%s"
                }
                """.formatted(usuarioId, livroId);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                post("/api/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(emprestimoId.toString()))
                .andExpect(jsonPath("$.usuarioId")
                        .value(usuarioId.toString()))
                .andExpect(jsonPath("$.nomeUsuario")
                        .value("João da Silva"))
                .andExpect(jsonPath("$.livroId")
                        .value(livroId.toString()))
                .andExpect(jsonPath("$.tituloLivro")
                        .value("Clean Code"))
                .andExpect(jsonPath("$.dataEmprestimo")
                        .exists())
                .andExpect(jsonPath("$.dataDevolucao")
                        .doesNotExist());
    }

    @Test
    void deveDevolverEmprestimo() throws Exception {
        UUID emprestimoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();

        EmprestimoResponse response = new EmprestimoResponse(
                emprestimoId,
                usuarioId,
                "João da Silva",
                livroId,
                "Clean Code",
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now());

        when(emprestimoService.devolver(eq(emprestimoId))).thenReturn(response);

        mockMvc.perform(patch("/api/emprestimos/{id}/devolver", emprestimoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(emprestimoId.toString()))
                .andExpect(jsonPath("$.dataDevolucao").exists());
    }

    @Test
    void deveListarEmprestimos() throws Exception {
        UUID emprestimoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();

        EmprestimoResponse response = new EmprestimoResponse(
                emprestimoId,
                usuarioId,
                "João da Silva",
                livroId,
                "Clean Code",
                LocalDateTime.now(),
                null
        );

        when(emprestimoService.listarTodos())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nomeUsuario").value("João da Silva"))
                .andExpect(jsonPath("$[0].tituloLivro").value("Clean Code"));
    }

    @Test
    void deveBuscarEmprestimoPorId() throws Exception {
        UUID emprestimoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();

        EmprestimoResponse response = new EmprestimoResponse(
                emprestimoId,
                usuarioId,
                "João da Silva",
                livroId,
                "Clean Code",
                LocalDateTime.now(),
                null);

        when(emprestimoService.buscarPorId(emprestimoId)).thenReturn(response);

        mockMvc.perform(get("/api/emprestimos/{id}", emprestimoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(emprestimoId.toString()))
                .andExpect(jsonPath("$.nomeUsuario").value("João da Silva"))
                .andExpect(jsonPath("$.tituloLivro").value("Clean Code"))
                .andExpect(jsonPath("$.dataDevolucao").doesNotExist());
    }

    @Test
    void deveRetornar400QuandoDadosDoEmprestimoForemInvalidos() throws Exception {
        String request = """
            {
                "usuarioId": null,
                "livroId": null
            }
            """;

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                post("/api/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}