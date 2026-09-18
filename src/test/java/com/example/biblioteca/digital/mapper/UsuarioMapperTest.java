package com.example.biblioteca.digital.mapper;

import com.example.biblioteca.digital.entities.Usuario;
import com.example.biblioteca.digital.requests.UsuarioRequest;
import com.example.biblioteca.digital.responses.UsuarioResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UsuarioMapperTest {
    private final UsuarioMapper usuarioMapper = new UsuarioMapper();

    @Test
    void deveConverterRequestParaEntity() {
        UsuarioRequest request = new UsuarioRequest(
                "João da Silva",
                "joao@email.com",
                "123456");

        Usuario usuario = usuarioMapper.toEntity(request);

        assertNotNull(usuario);
        assertEquals("João da Silva", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
        assertEquals("123456", usuario.getSenha());
    }

    @Test
    void deveConverterEntityParaResponse() {
        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        UUID id = UUID.randomUUID();

        try {
            var campoId = Usuario.class.getDeclaredField("id");
            campoId.setAccessible(true);
            campoId.set(usuario, id);
        } catch (Exception exception) {
            fail("Não foi possível configurar o ID " + "do usuário para o teste.");
        }

        UsuarioResponse response = usuarioMapper.toResponse(usuario);

        assertNotNull(response);

        assertEquals(id, response.id());
        assertEquals("João da Silva", response.nome());
        assertEquals("joao@email.com", response.email());
    }
}
