package com.example.biblioteca.digital.mapper;

import com.example.biblioteca.digital.entities.Usuario;
import com.example.biblioteca.digital.requests.UsuarioRequest;
import com.example.biblioteca.digital.responses.UsuarioResponse;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public Usuario toEntity(UsuarioRequest request) {
        return new Usuario(
                request.nome(),
                request.email(),
                request.senha());
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail());
    }
}