package com.example.biblioteca.digital.services;

import com.example.biblioteca.digital.entities.Emprestimo;
import com.example.biblioteca.digital.entities.Livro;
import com.example.biblioteca.digital.entities.Usuario;
import com.example.biblioteca.digital.exceptions.*;
import com.example.biblioteca.digital.mapper.EmprestimoMapper;
import com.example.biblioteca.digital.repositories.EmprestimoRepository;
import com.example.biblioteca.digital.repositories.LivroRepository;
import com.example.biblioteca.digital.repositories.UsuarioRepository;
import com.example.biblioteca.digital.requests.EmprestimoRequest;
import com.example.biblioteca.digital.responses.EmprestimoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {
    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Test
    void deveCriarEmprestimo() {
        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();
        UUID emprestimoId = UUID.randomUUID();

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        EmprestimoRequest request = new EmprestimoRequest(usuarioId, livroId);

        Emprestimo emprestimo = new Emprestimo(usuario, livro, java.time.LocalDateTime.now());

        EmprestimoResponse response =
                new EmprestimoResponse(emprestimoId, usuarioId, "João da Silva", livroId,
                        "Clean Code", emprestimo.getDataEmprestimo(), null);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livro));

        when(emprestimoRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(emprestimo);

        when(emprestimoMapper.toResponse(emprestimo)).thenReturn(response);

        EmprestimoResponse resultado = emprestimoService.criar(request);

        assertNotNull(resultado);
        assertEquals("João da Silva", resultado.nomeUsuario());
        assertEquals("Clean Code", resultado.tituloLivro());
        assertNull(resultado.dataDevolucao());

        assertFalse(livro.isDisponivel());

        verify(usuarioRepository).findById(usuarioId);
        verify(livroRepository).findById(livroId);
        verify(emprestimoRepository).save(any(Emprestimo.class));
        verify(livroRepository).save(livro);
        verify(emprestimoMapper).toResponse(emprestimo);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {

        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();

        EmprestimoRequest request =
                new EmprestimoRequest(usuarioId, livroId);

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> emprestimoService.criar(request)
        );

        verify(usuarioRepository).findById(usuarioId);
        verifyNoInteractions(livroRepository);
        verifyNoInteractions(emprestimoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoForEncontrado() {

        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456"
        );

        EmprestimoRequest request =
                new EmprestimoRequest(usuarioId, livroId);

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(livroRepository.findById(livroId))
                .thenReturn(Optional.empty());

        assertThrows(
                LivroNaoEncontradoException.class,
                () -> emprestimoService.criar(request)
        );

        verify(usuarioRepository).findById(usuarioId);
        verify(livroRepository).findById(livroId);
        verifyNoInteractions(emprestimoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoLivroEstiverIndisponivel() {
        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        livro.setDisponivel(false);

        EmprestimoRequest request = new EmprestimoRequest(usuarioId, livroId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livro));

        assertThrows(LivroIndisponivelException.class, () -> emprestimoService.criar(request));

        verify(usuarioRepository).findById(usuarioId);
        verify(livroRepository).findById(livroId);
        verifyNoInteractions(emprestimoRepository);
    }

    @Test
    void deveDevolverEmprestimo() {
        UUID emprestimoId = UUID.randomUUID();

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        livro.setDisponivel(false);

        Emprestimo emprestimo =
                new Emprestimo(usuario, livro, LocalDateTime.now().minusDays(1));

        EmprestimoResponse response =
                new EmprestimoResponse(
                        emprestimoId,
                        UUID.randomUUID(),
                        "João da Silva",
                        UUID.randomUUID(),
                        "Clean Code",
                        emprestimo.getDataEmprestimo(),
                        LocalDateTime.now());

        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));

        when(emprestimoRepository.save(emprestimo)).thenReturn(emprestimo);

        when(emprestimoMapper.toResponse(emprestimo)).thenReturn(response);

        EmprestimoResponse resultado = emprestimoService.devolver(emprestimoId);

        assertNotNull(resultado);
        assertNotNull(emprestimo.getDataDevolucao());

        assertTrue(livro.isDisponivel());

        verify(emprestimoRepository).findById(emprestimoId);

        verify(emprestimoRepository).save(emprestimo);

        verify(livroRepository).save(livro);

        verify(emprestimoMapper).toResponse(emprestimo);
    }

    @Test
    void deveLancarExcecaoQuandoEmprestimoNaoForEncontradoAoDevolver() {
        UUID emprestimoId = UUID.randomUUID();

        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.empty());

        assertThrows(EmprestimoNaoEncontradoException.class, () -> emprestimoService.devolver(emprestimoId));

        verify(emprestimoRepository).findById(emprestimoId);

        verifyNoMoreInteractions(emprestimoRepository);
        verifyNoInteractions(livroRepository);
        verifyNoInteractions(emprestimoMapper);
    }

    @Test
    void deveLancarExcecaoQuandoEmprestimoJaFoiDevolvido() {
        UUID emprestimoId = UUID.randomUUID();

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        Emprestimo emprestimo =
                new Emprestimo(usuario, livro, LocalDateTime.now().minusDays(2));

        emprestimo.setDataDevolucao(LocalDateTime.now().minusDays(1));

        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));

        assertThrows(EmprestimoJaDevolvidoException.class, () -> emprestimoService.devolver(emprestimoId));

        verify(emprestimoRepository).findById(emprestimoId);

        verifyNoMoreInteractions(emprestimoRepository);
        verifyNoInteractions(livroRepository);
        verifyNoInteractions(emprestimoMapper);
    }

    @Test
    void deveListarTodosOsEmprestimos() {
        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        Emprestimo emprestimo =
                new Emprestimo(usuario, livro, LocalDateTime.now());

        when(emprestimoRepository.findAll()).thenReturn(List.of(emprestimo));

        EmprestimoResponse response = new EmprestimoResponse(
                UUID.randomUUID(),
                usuarioId,
                "João da Silva",
                livroId,
                "Clean Code",
                emprestimo.getDataEmprestimo(),
                null);

        when(emprestimoMapper.toResponse(emprestimo)).thenReturn(response);

        List<EmprestimoResponse> resultado = emprestimoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("João da Silva", resultado.getFirst().nomeUsuario());
        assertEquals("Clean Code", resultado.getFirst().tituloLivro());

        verify(emprestimoRepository).findAll();
        verify(emprestimoMapper).toResponse(emprestimo);
    }

    @Test
    void deveBuscarEmprestimoPorId() {
        UUID id = UUID.randomUUID();

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Livro livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        Emprestimo emprestimo =
                new Emprestimo(usuario, livro, LocalDateTime.now());

        when(emprestimoRepository.findById(id)).thenReturn(Optional.of(emprestimo));

        EmprestimoResponse response = new EmprestimoResponse(
                id,
                UUID.randomUUID(),
                "João da Silva",
                UUID.randomUUID(),
                "Clean Code",
                emprestimo.getDataEmprestimo(),
                null);

        when(emprestimoMapper.toResponse(emprestimo)).thenReturn(response);

        EmprestimoResponse resultado = emprestimoService.buscarPorId(id);

        assertEquals(id, resultado.id());
        assertEquals("João da Silva", resultado.nomeUsuario());
        assertEquals("Clean Code", resultado.tituloLivro());

        verify(emprestimoRepository).findById(id);
        verify(emprestimoMapper).toResponse(emprestimo);
    }

    @Test
    void deveLancarExcecaoQuandoEmprestimoNaoForEncontradoAoBuscarPorId() {
        UUID id = UUID.randomUUID();

        when(emprestimoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmprestimoNaoEncontradoException.class, () -> emprestimoService.buscarPorId(id));

        verify(emprestimoRepository).findById(id);
        verifyNoInteractions(emprestimoMapper);
    }
}
