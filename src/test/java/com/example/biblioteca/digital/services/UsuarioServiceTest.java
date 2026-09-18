package com.example.biblioteca.digital.services;

import com.example.biblioteca.digital.entities.Usuario;
import com.example.biblioteca.digital.exceptions.UsuarioNaoEncontradoException;
import com.example.biblioteca.digital.mapper.UsuarioMapper;
import com.example.biblioteca.digital.repositories.UsuarioRepository;
import com.example.biblioteca.digital.requests.UsuarioRequest;
import com.example.biblioteca.digital.responses.UsuarioResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        UsuarioRequest request = new UsuarioRequest(
                "João da Silva",
                "joao@email.com",
                "123456");

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        String senhaCriptografada = "$2a$10$senhaCriptografada";

        Usuario usuarioSalvo = usuario;

        UsuarioResponse response = new UsuarioResponse(
                null,
                "João da Silva",
                "joao@email.com");

        when(usuarioMapper.toEntity(request)).thenReturn(usuario);

        when(passwordEncoder.encode("123456")).thenReturn(senhaCriptografada);

        when(usuarioRepository.save(usuario)).thenReturn(usuarioSalvo);

        when(usuarioMapper.toResponse(usuarioSalvo)).thenReturn(response);

        UsuarioResponse resultado = usuarioService.cadastrar(request);

        assertNotNull(resultado);
        assertEquals(senhaCriptografada, usuario.getSenha());

        verify(passwordEncoder).encode("123456");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveListarTodosOsUsuarios() {
        Usuario primeiro = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Usuario segundo = new Usuario(
                "Maria Oliveira",
                "maria@email.com",
                "654321");

        UsuarioResponse primeiroResponse =
                new UsuarioResponse(UUID.randomUUID(), "João da Silva", "joao@email.com");

        UsuarioResponse segundoResponse =
                new UsuarioResponse(UUID.randomUUID(), "Maria Oliveira", "maria@email.com");

        when(usuarioRepository.findAll()).thenReturn(List.of(primeiro, segundo));

        when(usuarioMapper.toResponse(primeiro)).thenReturn(primeiroResponse);

        when(usuarioMapper.toResponse(segundo)).thenReturn(segundoResponse);

        List<UsuarioResponse> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals("João da Silva", resultado.get(0).nome());
        assertEquals("Maria Oliveira", resultado.get(1).nome());

        verify(usuarioRepository).findAll();

        verify(usuarioMapper).toResponse(primeiro);

        verify(usuarioMapper).toResponse(segundo);
    }

    @Test
    void deveBuscarUsuarioPorId() {
        UUID id = UUID.randomUUID();

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        UsuarioResponse response = new UsuarioResponse(id, "João da Silva", "joao@email.com");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse resultado = usuarioService.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.id());
        assertEquals("João da Silva", resultado.nome());
        assertEquals("joao@email.com", resultado.email());

        verify(usuarioRepository).findById(id);

        verify(usuarioMapper).toResponse(usuario);
    }

    @Test
    void deveAtualizarUsuarioComSenhaCriptografada() {
        UUID id = UUID.randomUUID();

        UsuarioRequest request = new UsuarioRequest(
                "João Atualizado",
                "joao@email.com",
                "novaSenha");

        Usuario usuario = new Usuario(
                "João",
                "joao@email.com",
                "senhaAntiga");

        String senhaCriptografada = "$2a$10$novaSenhaCriptografada";

        UsuarioResponse response = new UsuarioResponse(
                id,
                "João Atualizado",
                "joao@email.com");

        when(usuarioRepository.findById(id)).thenReturn(java.util.Optional.of(usuario));

        when(passwordEncoder.encode("novaSenha")).thenReturn(senhaCriptografada);

        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse resultado = usuarioService.atualizar(id, request);

        assertNotNull(resultado);
        assertEquals(senhaCriptografada, usuario.getSenha());

        verify(passwordEncoder).encode("novaSenha");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveDeletarUsuario() {
        UUID id = UUID.randomUUID();

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        usuarioService.deletar(id);

        verify(usuarioRepository).findById(id);

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontradoAoBuscar() {
        UUID id = UUID.randomUUID();

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        UsuarioNaoEncontradoException exception =
                assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.buscarPorId(id));

        assertEquals("Usuário não encontrado com ID: " + id, exception.getMessage());

        verify(usuarioRepository).findById(id);

        verify(usuarioMapper, never()).toResponse(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontradoAoAtualizar() {
        UUID id = UUID.randomUUID();

        UsuarioRequest request =
                new UsuarioRequest(
                        "João Santos",
                        "joao.santos@email.com",
                        "654321");

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        UsuarioNaoEncontradoException exception =
                assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.atualizar(id, request));

        assertEquals("Usuário não encontrado com ID: " + id, exception.getMessage());

        verify(usuarioRepository).findById(id);

        verify(usuarioRepository, never()).save(any());

        verify(usuarioMapper, never()).toResponse(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontradoAoDeletar() {
        UUID id = UUID.randomUUID();

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        UsuarioNaoEncontradoException exception =
                assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.deletar(id));

        assertEquals("Usuário não encontrado com ID: " + id, exception.getMessage());

        verify(usuarioRepository).findById(id);

        verify(usuarioRepository, never()).delete(any());
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        UsuarioResponse response = new UsuarioResponse(
                UUID.randomUUID(),
                "João da Silva",
                "joao@email.com");

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        List<UsuarioResponse> resultado = usuarioService.buscarPorEmail("JOAO@EMAIL.COM");

        assertEquals(1, resultado.size());
        assertEquals("joao@email.com", resultado.getFirst().email());

        verify(usuarioRepository).findAll();
        verify(usuarioMapper).toResponse(usuario);
    }
}
