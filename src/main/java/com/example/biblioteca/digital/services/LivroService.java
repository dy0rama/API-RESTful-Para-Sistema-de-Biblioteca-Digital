package com.example.biblioteca.digital.services;

import com.example.biblioteca.digital.entities.Livro;
import com.example.biblioteca.digital.exceptions.LivroNaoEncontradoException;
import com.example.biblioteca.digital.mapper.LivroMapper;
import com.example.biblioteca.digital.repositories.LivroRepository;
import com.example.biblioteca.digital.requests.LivroRequest;
import com.example.biblioteca.digital.responses.LivroResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final LivroMapper livroMapper;

    public LivroService(
            LivroRepository livroRepository,
            LivroMapper livroMapper) {

        this.livroRepository = livroRepository;
        this.livroMapper = livroMapper;
    }

    public LivroResponse cadastrar(LivroRequest request) {

        Livro livro = livroMapper.toEntity(request);

        Livro livroSalvo = livroRepository.save(livro);

        return livroMapper.toResponse(livroSalvo);
    }

    public List<LivroResponse> listarTodos() {

        return livroRepository.findAll()
                .stream()
                .map(livroMapper::toResponse)
                .toList();
    }

    public LivroResponse buscarPorId(UUID id) {

        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException(id));

        return livroMapper.toResponse(livro);
    }

    public LivroResponse atualizar(UUID id, LivroRequest request) {

        Livro livroExistente = livroRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException(id));

        livroExistente.setTitulo(request.titulo());
        livroExistente.setAutor(request.autor());
        livroExistente.setAnoPublicacao(request.anoPublicacao());

        Livro livroAtualizado = livroRepository.save(livroExistente);

        return livroMapper.toResponse(livroAtualizado);
    }

    public void deletar(UUID id) {
        Livro livroExistente = livroRepository.findById(id).orElseThrow(() -> new LivroNaoEncontradoException(id));
        livroRepository.delete(livroExistente);
    }

    public List<LivroResponse> filtrarPorAutor(String autor) {
        return livroRepository.findAll()
                .stream()
                .filter(livro -> livro.getAutor().equalsIgnoreCase(autor))
                .map(livroMapper::toResponse)
                .toList();
    }

    public List<LivroResponse> buscarPorTitulo(String titulo) {
        return livroRepository.findAll()
                .stream()
                .filter(livro -> livro.getTitulo().equalsIgnoreCase(titulo))
                .map(livroMapper::toResponse)
                .toList();
    }

    public List<LivroResponse> listarOrdenadosPorTitulo() {
        return livroRepository.findAll()
                .stream()
                .sorted(Comparator.naturalOrder())
                .map(livroMapper::toResponse)
                .toList();
    }

    public List<LivroResponse> listarOrdenadosPorAno() {
        return livroRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Livro::getAnoPublicacao))
                .map(livroMapper::toResponse)
                .toList();
    }

    public Map<String, List<LivroResponse>> agruparPorAutor() {
        return livroRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Livro::getAutor,
                        Collectors.mapping(livroMapper::toResponse, Collectors.toList())));
    }
}
