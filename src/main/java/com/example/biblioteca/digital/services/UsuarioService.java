package com.example.biblioteca.digital.services;

import com.example.biblioteca.digital.entities.Usuario;
import com.example.biblioteca.digital.exceptions.UsuarioNaoEncontradoException;
import com.example.biblioteca.digital.mapper.UsuarioMapper;
import com.example.biblioteca.digital.repositories.UsuarioRepository;
import com.example.biblioteca.digital.requests.UsuarioRequest;
import com.example.biblioteca.digital.responses.UsuarioResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse cadastrar(UsuarioRequest request) {
        Usuario usuario = usuarioMapper.toEntity(request);

        usuario.setSenha(passwordEncoder.encode(request.senha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuarioSalvo);
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse atualizar(UUID id, UsuarioRequest request) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        usuarioExistente.setNome(request.nome());
        usuarioExistente.setEmail(request.email());

        usuarioExistente.setSenha(passwordEncoder.encode(request.senha()));

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);

        return usuarioMapper.toResponse(usuarioAtualizado);
    }

    public void deletar(UUID id) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        usuarioRepository.delete(usuarioExistente);
    }

    public List<UsuarioResponse> buscarPorEmail(String email) {
        return usuarioRepository.findAll()
                .stream()
                .filter(usuario ->
                        usuario.getEmail().equalsIgnoreCase(email))
                .map(usuarioMapper::toResponse)
                .toList();
    }
}
