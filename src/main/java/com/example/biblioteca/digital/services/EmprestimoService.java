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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final EmprestimoMapper emprestimoMapper;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, UsuarioRepository usuarioRepository,
             LivroRepository livroRepository, EmprestimoMapper emprestimoMapper) {

        this.emprestimoRepository = emprestimoRepository;
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
        this.emprestimoMapper = emprestimoMapper;
    }

    @Transactional
    public EmprestimoResponse criar(EmprestimoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(request.usuarioId()));

        Livro livro = livroRepository.findById(request.livroId())
                .orElseThrow(() -> new LivroNaoEncontradoException(request.livroId()));

        if (!livro.isDisponivel()) {
            throw new LivroIndisponivelException(livro.getId());
        }

        Emprestimo emprestimo = new Emprestimo(usuario, livro, LocalDateTime.now());

        livro.setDisponivel(false);

        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);

        livroRepository.save(livro);

        return emprestimoMapper.toResponse(emprestimoSalvo);
    }

    @Transactional
    public EmprestimoResponse devolver(UUID id) {
        Emprestimo emprestimo =
                emprestimoRepository.findById(id).orElseThrow(() -> new EmprestimoNaoEncontradoException(id));

        if (emprestimo.getDataDevolucao() != null) {
            throw new EmprestimoJaDevolvidoException(id);
        }

        emprestimo.setDataDevolucao(LocalDateTime.now());

        Livro livro = emprestimo.getLivro();
        livro.setDisponivel(true);

        Emprestimo emprestimoAtualizado = emprestimoRepository.save(emprestimo);

        livroRepository.save(livro);

        return emprestimoMapper.toResponse(emprestimoAtualizado);
    }

    public List<EmprestimoResponse> listarTodos() {
        return emprestimoRepository.findAll()
                .stream()
                .map(emprestimoMapper::toResponse)
                .toList();
    }

    public EmprestimoResponse buscarPorId(UUID id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new EmprestimoNaoEncontradoException(id));

        return emprestimoMapper.toResponse(emprestimo);
    }
}
