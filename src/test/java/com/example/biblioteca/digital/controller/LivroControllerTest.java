package com.example.biblioteca.digital.controller;

import com.example.biblioteca.digital.exceptions.GlobalExceptionHandler;
import com.example.biblioteca.digital.exceptions.LivroNaoEncontradoException;
import com.example.biblioteca.digital.requests.LivroRequest;
import com.example.biblioteca.digital.responses.LivroResponse;
import com.example.biblioteca.digital.services.LivroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LivroController.class)
@Import(GlobalExceptionHandler.class)
class LivroControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LivroService livroService;

    @Test
    void deveCadastrarLivro() throws Exception {
        LivroRequest request = new LivroRequest(
                "Clean Code",
                "Robert C. Martin",
                2008);

        UUID id = UUID.randomUUID();

        LivroResponse response = new LivroResponse(
                id,
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        when(livroService.cadastrar(any(LivroRequest.class))).thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                        post("/api/livros")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.titulo").value("Clean Code"))
                .andExpect(jsonPath("$.autor").value("Robert C. Martin"))
                .andExpect(jsonPath("$.anoPublicacao").value(2008))
                .andExpect(jsonPath("$.disponivel").value(true));

        verify(livroService).cadastrar(any(LivroRequest.class));
    }

    @Test
    void deveListarTodosOsLivros() throws Exception {
        LivroResponse primeiro = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        LivroResponse segundo = new LivroResponse(
                UUID.randomUUID(),
                "Effective Java",
                "Joshua Bloch",
                2018,
                false);

        when(livroService.listarTodos()).thenReturn(List.of(primeiro, segundo));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Clean Code"))
                .andExpect(jsonPath("$[0].autor").value("Robert C. Martin"))
                .andExpect(jsonPath("$[0].disponivel").value(true))
                .andExpect(jsonPath("$[1].titulo").value("Effective Java"))
                .andExpect(jsonPath("$[1].autor").value("Joshua Bloch"))
                .andExpect(jsonPath("$[1].disponivel").value(false));

        verify(livroService).listarTodos();
    }

    @Test
    void deveBuscarLivroPorId() throws Exception {
        UUID id = UUID.randomUUID();

        LivroResponse response = new LivroResponse(
                id,
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        when(livroService.buscarPorId(id)).thenReturn(response);

        mockMvc.perform(get("/api/livros/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.titulo").value("Clean Code"))
                .andExpect(jsonPath("$.autor").value("Robert C. Martin"))
                .andExpect(jsonPath("$.anoPublicacao").value(2008))
                .andExpect(jsonPath("$.disponivel").value(true));

        verify(livroService).buscarPorId(id);
    }

    @Test
    void deveAtualizarLivro() throws Exception {
        UUID id = UUID.randomUUID();

        LivroRequest request = new LivroRequest(
                "Clean Code - Atualizado",
                "Robert C. Martin",
                2020);

        LivroResponse response = new LivroResponse(
                id,
                "Clean Code - Atualizado",
                "Robert C. Martin",
                2020,
                true);

        when(livroService.atualizar(
                eq(id),
                any(LivroRequest.class)
        )).thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                        put("/api/livros/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.titulo")
                        .value("Clean Code - Atualizado"))
                .andExpect(jsonPath("$.autor")
                        .value("Robert C. Martin"))
                .andExpect(jsonPath("$.anoPublicacao").value(2020))
                .andExpect(jsonPath("$.disponivel").value(true));

        verify(livroService).atualizar(
                eq(id),
                any(LivroRequest.class));
    }

    @Test
    void deveDeletarLivro() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(livroService).deletar(id);

        mockMvc.perform(delete("/api/livros/" + id)).andExpect(status().isNoContent());

        verify(livroService).deletar(id);
    }

    @Test
    void deveRetornar404QuandoLivroNaoForEncontradoAoBuscar() throws Exception {
        UUID id = UUID.randomUUID();

        when(livroService.buscarPorId(id)).thenThrow(new LivroNaoEncontradoException(id));

        mockMvc.perform(get("/api/livros/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro")
                        .value("Livro não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Livro não encontrado com ID: " + id))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(livroService).buscarPorId(id);
    }

    @Test
    void deveRetornar404QuandoLivroNaoForEncontradoAoAtualizar() throws Exception {
        UUID id = UUID.randomUUID();

        LivroRequest request = new LivroRequest(
                "Clean Code",
                "Robert C. Martin",
                2008);

        when(livroService.atualizar(
                eq(id),
                any(LivroRequest.class)
        )).thenThrow(new LivroNaoEncontradoException(id));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                        put("/api/livros/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro")
                        .value("Livro não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Livro não encontrado com ID: " + id))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(livroService).atualizar(
                eq(id),
                any(LivroRequest.class));
    }

    @Test
    void deveRetornar404QuandoLivroNaoForEncontradoAoDeletar() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new LivroNaoEncontradoException(id)).when(livroService).deletar(id);

        mockMvc.perform(delete("/api/livros/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro")
                        .value("Livro não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Livro não encontrado com ID: " + id))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(livroService).deletar(id);
    }

    @Test
    void deveRetornar400QuandoDadosDoLivroForemInvalidos() throws Exception {
        LivroRequest request = new LivroRequest(
                "",
                "",
                0);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                        post("/api/livros")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(livroService);
    }

    @Test
    void deveFiltrarLivrosPorAutor() throws Exception {
        LivroResponse primeiro = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        LivroResponse segundo = new LivroResponse(
                UUID.randomUUID(),
                "Clean Architecture",
                "Robert C. Martin",
                2017,
                false);

        when(livroService.filtrarPorAutor("Robert C. Martin")).thenReturn(List.of(primeiro, segundo));

        mockMvc.perform(
                get("/api/livros/autor").param("autor", "Robert C. Martin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo")
                        .value("Clean Code"))
                .andExpect(jsonPath("$[0].autor")
                        .value("Robert C. Martin"))
                .andExpect(jsonPath("$[1].titulo")
                        .value("Clean Architecture"))
                .andExpect(jsonPath("$[1].autor")
                        .value("Robert C. Martin"));

        verify(livroService).filtrarPorAutor("Robert C. Martin");
    }

    @Test
    void deveBuscarLivrosPorTituloIgnorandoMaiusculasEMinusculas() throws Exception {
        LivroResponse livro = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        when(livroService.buscarPorTitulo("CLEAN CODE")).thenReturn(List.of(livro));

        mockMvc.perform(get("/api/livros/titulo").param("titulo", "CLEAN CODE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo")
                        .value("Clean Code"))
                .andExpect(jsonPath("$[0].autor")
                        .value("Robert C. Martin"))
                .andExpect(jsonPath("$[0].anoPublicacao")
                        .value(2008))
                .andExpect(jsonPath("$[0].disponivel")
                        .value(true));

        verify(livroService).buscarPorTitulo("CLEAN CODE");
    }

    @Test
    void deveListarLivrosOrdenadosPorTitulo() throws Exception {
        LivroResponse primeiro = new LivroResponse(
                UUID.randomUUID(),
                "Clean Architecture",
                "Robert C. Martin",
                2017,
                true);

        LivroResponse segundo = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        LivroResponse terceiro = new LivroResponse(
                UUID.randomUUID(),
                "Effective Java",
                "Joshua Bloch",
                2018,
                true);

        when(livroService.listarOrdenadosPorTitulo())
                .thenReturn(List.of(
                        primeiro,
                        segundo,
                        terceiro));

        mockMvc.perform(get("/api/livros/ordenados/titulo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].titulo")
                        .value("Clean Architecture"))
                .andExpect(jsonPath("$[1].titulo")
                        .value("Clean Code"))
                .andExpect(jsonPath("$[2].titulo")
                        .value("Effective Java"));

        verify(livroService).listarOrdenadosPorTitulo();
    }

    @Test
    void deveListarLivrosOrdenadosPorAno() throws Exception {
        LivroResponse primeiro = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        LivroResponse segundo = new LivroResponse(
                UUID.randomUUID(),
                "Clean Architecture",
                "Robert C. Martin",
                2017,
                true);

        LivroResponse terceiro = new LivroResponse(
                UUID.randomUUID(),
                "Effective Java",
                "Joshua Bloch",
                2018,
                true);

        when(livroService.listarOrdenadosPorAno())
                .thenReturn(List.of(
                        primeiro,
                        segundo,
                        terceiro));

        mockMvc.perform(get("/api/livros/ordenados/ano"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].anoPublicacao")
                        .value(2008))
                .andExpect(jsonPath("$[1].anoPublicacao")
                        .value(2017))
                .andExpect(jsonPath("$[2].anoPublicacao")
                        .value(2018))
                .andExpect(jsonPath("$[0].titulo")
                        .value("Clean Code"))
                .andExpect(jsonPath("$[1].titulo")
                        .value("Clean Architecture"))
                .andExpect(jsonPath("$[2].titulo")
                        .value("Effective Java"));

        verify(livroService).listarOrdenadosPorAno();
    }

    @Test
    void deveAgruparLivrosPorAutor() throws Exception {
        LivroResponse primeiro = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        LivroResponse segundo = new LivroResponse(
                UUID.randomUUID(),
                "Clean Architecture",
                "Robert C. Martin",
                2017,
                true);

        LivroResponse terceiro = new LivroResponse(
                UUID.randomUUID(),
                "Effective Java",
                "Joshua Bloch",
                2018,
                true);

        Map<String, List<LivroResponse>> agrupados = Map.of(
                "Robert C. Martin",
                List.of(primeiro, segundo),

                "Joshua Bloch",
                List.of(terceiro));

        when(livroService.agruparPorAutor()).thenReturn(agrupados);

        mockMvc.perform(get("/api/livros/agrupados/autor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Robert C. Martin']").isArray())
                .andExpect(jsonPath("$.['Robert C. Martin'].length()").value(2))
                .andExpect(jsonPath("$['Robert C. Martin'][0].titulo").value("Clean Code"))
                .andExpect(jsonPath("$['Robert C. Martin'][1].titulo").value("Clean Architecture"))
                .andExpect(jsonPath("$.['Joshua Bloch']").isArray())
                .andExpect(jsonPath("$.['Joshua Bloch'].length()").value(1))
                .andExpect(jsonPath("$['Joshua Bloch'][0].titulo").value("Effective Java"));

        verify(livroService).agruparPorAutor();
    }
}
