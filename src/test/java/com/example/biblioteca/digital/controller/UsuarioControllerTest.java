package com.example.biblioteca.digital.controller;

import com.example.biblioteca.digital.exceptions.GlobalExceptionHandler;
import com.example.biblioteca.digital.exceptions.UsuarioNaoEncontradoException;
import com.example.biblioteca.digital.requests.UsuarioRequest;
import com.example.biblioteca.digital.responses.UsuarioResponse;
import com.example.biblioteca.digital.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import(GlobalExceptionHandler.class)
class UsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuario() throws Exception {
        UsuarioRequest request = new UsuarioRequest(
                "João da Silva",
                "joao@email.com",
                "123456");

        UUID id = UUID.randomUUID();

        UsuarioResponse response =
                new UsuarioResponse(id, "João da Silva", "joao@email.com");

        when(usuarioService.cadastrar(any(UsuarioRequest.class))).thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                        post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveListarTodosOsUsuarios() throws Exception {
        UUID id = UUID.randomUUID();

        List<UsuarioResponse> usuarios =
                List.of(new UsuarioResponse(id, "João da Silva", "joao@email.com"));

        when(usuarioService.listarTodos()).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].nome").value("João da Silva"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"));
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        UUID id = UUID.randomUUID();

        UsuarioResponse response =
                new UsuarioResponse(id, "João da Silva", "joao@email.com");

        when(usuarioService.buscarPorId(id)).thenReturn(response);

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void deveAtualizarUsuario() throws Exception {
        UUID id = UUID.randomUUID();

        UsuarioRequest request = new UsuarioRequest(
                "João Atualizado",
                "joao.atualizado@email.com",
                "654321");

        UsuarioResponse response =
                new UsuarioResponse(id, "João Atualizado", "joao.atualizado@email.com");

        when(usuarioService.atualizar(eq(id), any(UsuarioRequest.class))).thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                        put("/api/usuarios/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("João Atualizado"))
                .andExpect(jsonPath("$.email")
                        .value("joao.atualizado@email.com"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveDeletarUsuario() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(usuarioService).deletar(id);

        mockMvc.perform(delete("/api/usuarios/{id}", id)).andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoForEncontrado() throws Exception {
        UUID id = UUID.randomUUID();

        when(usuarioService.buscarPorId(id)).thenThrow(new UsuarioNaoEncontradoException(id));

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro")
                        .value("Usuário não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Usuário não encontrado com ID: " + id));
    }

    @Test
    void deveRetornar400QuandoNomeNaoForInformado() throws Exception {
        UsuarioRequest request = new UsuarioRequest(
                "",
                "joao@email.com",
                "123456");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                        post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoEmailForInvalido() throws Exception {
        UsuarioRequest request = new UsuarioRequest(
                "João da Silva",
                "email-invalido",
                "123456");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                        post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoSenhaNaoForInformada() throws Exception {
        UsuarioRequest request = new UsuarioRequest(
                "João da Silva",
                "joao@email.com",
                "");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
                        post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveBuscarUsuarioPorEmail() throws Exception {
        UUID id = UUID.randomUUID();

        UsuarioResponse response =
                new UsuarioResponse(id, "João da Silva", "joao@email.com");

        when(usuarioService.buscarPorEmail("joao@email.com")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/usuarios/email").param("email", "joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].nome").value("João da Silva"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"))
                .andExpect(jsonPath("$[0].senha").doesNotExist());
    }
}
