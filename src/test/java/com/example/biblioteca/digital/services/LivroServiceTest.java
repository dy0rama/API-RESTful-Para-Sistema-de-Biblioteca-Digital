package com.example.biblioteca.digital.services;

import com.example.biblioteca.digital.entities.Livro;
import com.example.biblioteca.digital.exceptions.LivroNaoEncontradoException;
import com.example.biblioteca.digital.mapper.LivroMapper;
import com.example.biblioteca.digital.repositories.LivroRepository;
import com.example.biblioteca.digital.requests.LivroRequest;
import com.example.biblioteca.digital.responses.LivroResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private LivroMapper livroMapper;

    @InjectMocks
    private LivroService livroService;

    private Livro livro;
    private LivroRequest request;
    private LivroResponse response;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        livro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        request = new LivroRequest(
                "Clean Code",
                "Robert C. Martin",
                2008);

        response = new LivroResponse(
                id,
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);
    }

    @Test
    void deveCadastrarLivro() {
        when(livroMapper.toEntity(request)).thenReturn(livro);

        when(livroRepository.save(livro)).thenReturn(livro);

        when(livroMapper.toResponse(livro)).thenReturn(response);

        LivroResponse resultado = livroService.cadastrar(request);

        assertNotNull(resultado);
        assertEquals(id, resultado.id());
        assertEquals("Clean Code", resultado.titulo());
        assertEquals("Robert C. Martin", resultado.autor());
        assertEquals(2008, resultado.anoPublicacao());
        assertTrue(resultado.disponivel());

        verify(livroMapper).toEntity(request);
        verify(livroRepository).save(livro);
        verify(livroMapper).toResponse(livro);
    }

    @Test
    void deveListarTodosOsLivros() {
        Livro segundoLivro = new Livro(
                "Effective Java",
                "Joshua Bloch",
                2018);

        LivroResponse segundoResponse = new LivroResponse(
                UUID.randomUUID(),
                "Effective Java",
                "Joshua Bloch",
                2018,
                true);

        when(livroRepository.findAll()).thenReturn(List.of(livro, segundoLivro));

        when(livroMapper.toResponse(livro)).thenReturn(response);

        when(livroMapper.toResponse(segundoLivro)).thenReturn(segundoResponse);

        List<LivroResponse> resultado = livroService.listarTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals("Clean Code", resultado.get(0).titulo());
        assertEquals("Effective Java", resultado.get(1).titulo());

        verify(livroRepository).findAll();
        verify(livroMapper).toResponse(livro);
        verify(livroMapper).toResponse(segundoLivro);
    }

    @Test
    void deveBuscarLivroPorId() {
        when(livroRepository.findById(id)).thenReturn(Optional.of(livro));

        when(livroMapper.toResponse(livro)).thenReturn(response);

        LivroResponse resultado = livroService.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.id());
        assertEquals("Clean Code", resultado.titulo());
        assertEquals("Robert C. Martin", resultado.autor());
        assertEquals(2008, resultado.anoPublicacao());
        assertTrue(resultado.disponivel());

        verify(livroRepository).findById(id);
        verify(livroMapper).toResponse(livro);
    }

    @Test
    void deveAtualizarLivro() {
        LivroRequest novoRequest = new LivroRequest(
                "Clean Code - 2ª Edição",
                "Robert C. Martin",
                2020);

        LivroResponse novoResponse = new LivroResponse(
                id,
                "Clean Code - 2ª Edição",
                "Robert C. Martin",
                2020,
                true);

        when(livroRepository.findById(id)).thenReturn(Optional.of(livro));

        when(livroRepository.save(livro)).thenReturn(livro);

        when(livroMapper.toResponse(livro)).thenReturn(novoResponse);

        LivroResponse resultado = livroService.atualizar(id, novoRequest);

        assertNotNull(resultado);
        assertEquals("Clean Code - 2ª Edição", resultado.titulo());
        assertEquals("Robert C. Martin", resultado.autor());
        assertEquals(2020, resultado.anoPublicacao());
        assertTrue(resultado.disponivel());

        assertEquals("Clean Code - 2ª Edição", livro.getTitulo());
        assertEquals("Robert C. Martin", livro.getAutor());
        assertEquals(2020, livro.getAnoPublicacao());

        verify(livroRepository).findById(id);
        verify(livroRepository).save(livro);
        verify(livroMapper).toResponse(livro);
    }

    @Test
    void deveManterDisponibilidadeAoAtualizarLivro() {
        livro.setDisponivel(false);

        LivroRequest novoRequest = new LivroRequest(
                "Clean Code Atualizado",
                "Robert C. Martin",
                2020);

        LivroResponse novoResponse = new LivroResponse(
                id,
                "Clean Code Atualizado",
                "Robert C. Martin",
                2020,
                false);

        when(livroRepository.findById(id)).thenReturn(Optional.of(livro));

        when(livroRepository.save(livro)).thenReturn(livro);

        when(livroMapper.toResponse(livro)).thenReturn(novoResponse);

        LivroResponse resultado = livroService.atualizar(id, novoRequest);

        assertFalse(livro.isDisponivel());
        assertFalse(resultado.disponivel());

        verify(livroRepository).findById(id);
        verify(livroRepository).save(livro);
    }

    @Test
    void deveDeletarLivro() {
        when(livroRepository.findById(id)).thenReturn(Optional.of(livro));

        livroService.deletar(id);

        verify(livroRepository).findById(id);
        verify(livroRepository).delete(livro);
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoForEncontradoAoBuscar() {
        when(livroRepository.findById(id)).thenReturn(Optional.empty());

        LivroNaoEncontradoException exception =
                assertThrows(LivroNaoEncontradoException.class, () -> livroService.buscarPorId(id));

        assertEquals("Livro não encontrado com ID: " + id, exception.getMessage());

        verify(livroRepository).findById(id);
        verifyNoInteractions(livroMapper);
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoForEncontradoAoAtualizar() {
        when(livroRepository.findById(id)).thenReturn(Optional.empty());

        LivroNaoEncontradoException exception =
                assertThrows(LivroNaoEncontradoException.class, () -> livroService.atualizar(id, request));

        assertEquals("Livro não encontrado com ID: " + id, exception.getMessage());

        verify(livroRepository).findById(id);
        verify(livroRepository, never()).save(any(Livro.class));
        verifyNoInteractions(livroMapper);
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoForEncontradoAoDeletar() {
        when(livroRepository.findById(id)).thenReturn(Optional.empty());

        LivroNaoEncontradoException exception =
                assertThrows(LivroNaoEncontradoException.class, () -> livroService.deletar(id));

        assertEquals("Livro não encontrado com ID: " + id, exception.getMessage());

        verify(livroRepository).findById(id);
        verify(livroRepository, never()).delete(any(Livro.class));
        verifyNoInteractions(livroMapper);
    }

    @Test
    void deveFiltrarLivrosPorAutor() {
        Livro primeiroLivro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        Livro segundoLivro = new Livro(
                "Clean Architecture",
                "Robert C. Martin",
                2017);

        Livro terceiroLivro = new Livro(
                "Effective Java",
                "Joshua Bloch",
                2018);

        LivroResponse primeiroResponse = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        LivroResponse segundoResponse = new LivroResponse(
                UUID.randomUUID(),
                "Clean Architecture",
                "Robert C. Martin",
                2017,
                true);

        when(livroRepository.findAll())
                .thenReturn(List.of(
                        primeiroLivro,
                        segundoLivro,
                        terceiroLivro));

        when(livroMapper.toResponse(primeiroLivro)).thenReturn(primeiroResponse);

        when(livroMapper.toResponse(segundoLivro)).thenReturn(segundoResponse);

        List<LivroResponse> resultado = livroService.filtrarPorAutor("robert c. martin");

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals("Clean Code", resultado.get(0).titulo());
        assertEquals("Clean Architecture", resultado.get(1).titulo());

        assertTrue(
                resultado.stream()
                        .allMatch(livro ->
                                livro.autor().equalsIgnoreCase("Robert C. Martin")));

        verify(livroRepository).findAll();
        verify(livroMapper).toResponse(primeiroLivro);
        verify(livroMapper).toResponse(segundoLivro);

        verify(livroMapper, never()).toResponse(terceiroLivro);
    }

    @Test
    void deveBuscarLivroPorTituloIgnorandoMaiusculasEMinusculas() {
        Livro primeiroLivro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        Livro segundoLivro = new Livro(
                "Clean Architecture",
                "Robert C. Martin",
                2017);

        Livro terceiroLivro = new Livro(
                "Effective Java",
                "Joshua Bloch",
                2018);

        LivroResponse primeiroResponse = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        when(livroRepository.findAll()).thenReturn(List.of(primeiroLivro, segundoLivro, terceiroLivro));

        when(livroMapper.toResponse(primeiroLivro)).thenReturn(primeiroResponse);

        List<LivroResponse> resultado = livroService.buscarPorTitulo("CLEAN CODE");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        assertEquals(
                "Clean Code",
                resultado.getFirst().titulo());

        assertEquals(
                "Robert C. Martin",
                resultado.getFirst().autor());

        verify(livroRepository).findAll();

        verify(livroMapper).toResponse(primeiroLivro);

        verify(livroMapper, never()).toResponse(segundoLivro);

        verify(livroMapper, never()).toResponse(terceiroLivro);
    }

    @Test
    void deveListarLivrosOrdenadosPorTitulo() {
        Livro primeiroLivro = new Livro(
                "Effective Java",
                "Joshua Bloch",
                2018);

        Livro segundoLivro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        Livro terceiroLivro = new Livro(
                "Clean Architecture",
                "Robert C. Martin",
                2017);

        LivroResponse primeiroResponse = new LivroResponse(
                UUID.randomUUID(),
                "Clean Architecture",
                "Robert C. Martin",
                2017,
                true);

        LivroResponse segundoResponse = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        LivroResponse terceiroResponse = new LivroResponse(
                UUID.randomUUID(),
                "Effective Java",
                "Joshua Bloch",
                2018,
                true);

        when(livroRepository.findAll())
                .thenReturn(List.of(
                        primeiroLivro,
                        segundoLivro,
                        terceiroLivro));

        when(livroMapper.toResponse(terceiroLivro)).thenReturn(primeiroResponse);

        when(livroMapper.toResponse(segundoLivro)).thenReturn(segundoResponse);

        when(livroMapper.toResponse(primeiroLivro)).thenReturn(terceiroResponse);

        List<LivroResponse> resultado = livroService.listarOrdenadosPorTitulo();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        assertEquals("Clean Architecture", resultado.get(0).titulo());

        assertEquals("Clean Code", resultado.get(1).titulo());

        assertEquals("Effective Java", resultado.get(2).titulo());

        verify(livroRepository).findAll();

        verify(livroMapper).toResponse(primeiroLivro);

        verify(livroMapper).toResponse(segundoLivro);

        verify(livroMapper).toResponse(terceiroLivro);
    }

    @Test
    void deveListarLivrosOrdenadosPorAno() {
        Livro primeiroLivro = new Livro(
                "Effective Java",
                "Joshua Bloch",
                2018);

        Livro segundoLivro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        Livro terceiroLivro = new Livro(
                "Clean Architecture",
                "Robert C. Martin",
                2017);

        LivroResponse primeiroResponse = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        LivroResponse segundoResponse = new LivroResponse(
                UUID.randomUUID(),
                "Clean Architecture",
                "Robert C. Martin",
                2017,
                true);

        LivroResponse terceiroResponse = new LivroResponse(
                UUID.randomUUID(),
                "Effective Java",
                "Joshua Bloch",
                2018,
                true);

        when(livroRepository.findAll())
                .thenReturn(List.of(
                        primeiroLivro,
                        segundoLivro,
                        terceiroLivro));

        when(livroMapper.toResponse(segundoLivro)).thenReturn(primeiroResponse);

        when(livroMapper.toResponse(terceiroLivro)).thenReturn(segundoResponse);

        when(livroMapper.toResponse(primeiroLivro)).thenReturn(terceiroResponse);

        List<LivroResponse> resultado = livroService.listarOrdenadosPorAno();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        assertEquals(
                2008,
                resultado.get(0).anoPublicacao());

        assertEquals(
                2017,
                resultado.get(1).anoPublicacao());

        assertEquals(
                2018,
                resultado.get(2).anoPublicacao());

        assertEquals(
                "Clean Code",
                resultado.get(0).titulo());

        assertEquals(
                "Clean Architecture",
                resultado.get(1).titulo());

        assertEquals(
                "Effective Java",
                resultado.get(2).titulo()
       );

        verify(livroRepository).findAll();

        verify(livroMapper).toResponse(primeiroLivro);

        verify(livroMapper).toResponse(segundoLivro);

        verify(livroMapper).toResponse(terceiroLivro);
    }

    @Test
    void deveAgruparLivrosPorAutor() {
        Livro primeiroLivro = new Livro(
                "Clean Code",
                "Robert C. Martin",
                2008);

        Livro segundoLivro = new Livro(
                "Clean Architecture",
                "Robert C. Martin",
                2017);

        Livro terceiroLivro = new Livro(
                "Effective Java",
                "Joshua Bloch",
                2018);

        LivroResponse primeiroResponse = new LivroResponse(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                2008,
                true);

        LivroResponse segundoResponse = new LivroResponse(
                UUID.randomUUID(),
                "Clean Architecture",
                "Robert C. Martin",
                2017,
                true);

        LivroResponse terceiroResponse = new LivroResponse(
                UUID.randomUUID(),
                "Effective Java",
                "Joshua Bloch",
                2018,
                true);

        when(livroRepository.findAll())
                .thenReturn(List.of(
                        primeiroLivro,
                        segundoLivro,
                        terceiroLivro));

        when(livroMapper.toResponse(primeiroLivro)).thenReturn(primeiroResponse);

        when(livroMapper.toResponse(segundoLivro)).thenReturn(segundoResponse);

        when(livroMapper.toResponse(terceiroLivro)).thenReturn(terceiroResponse);

        Map<String, List<LivroResponse>> resultado = livroService.agruparPorAutor();

        assertNotNull(resultado);

        assertEquals(2, resultado.size());

        assertTrue(resultado.containsKey("Robert C. Martin"));

        assertTrue(resultado.containsKey("Joshua Bloch"));
        assertEquals(2, resultado.get("Robert C. Martin").size());
        assertEquals(1, resultado.get("Joshua Bloch").size());
        assertEquals("Clean Code", resultado.get("Robert C. Martin").get(0).titulo());
        assertEquals("Clean Architecture", resultado.get("Robert C. Martin").get(1).titulo());

        assertEquals("Effective Java", resultado.get("Joshua Bloch").getFirst().titulo());

        verify(livroRepository).findAll();

        verify(livroMapper).toResponse(primeiroLivro);

        verify(livroMapper).toResponse(segundoLivro);

        verify(livroMapper).toResponse(terceiroLivro);
    }
}
