package com.example.biblioteca.digital.repositories;

import com.example.biblioteca.digital.entities.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveSalvarEBuscarUsuario() {

        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        assertNotNull(usuarioSalvo);
        assertNotNull(usuarioSalvo.getId());

        Optional<Usuario> resultado = usuarioRepository.findById(usuarioSalvo.getId());

        assertTrue(resultado.isPresent());

        Usuario usuarioEncontrado = resultado.get();

        assertEquals("João da Silva", usuarioEncontrado.getNome());
        assertEquals("joao@email.com", usuarioEncontrado.getEmail());
        assertEquals("123456", usuarioEncontrado.getSenha());
    }

    @Test
    void deveListarUsuarios() {
        Usuario primeiro = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        Usuario segundo = new Usuario(
                "Maria Oliveira",
                "maria@email.com",
                "654321");

        usuarioRepository.save(primeiro);
        usuarioRepository.save(segundo);

        var usuarios = usuarioRepository.findAll();

        assertEquals(2, usuarios.size());
        assertTrue(usuarios.stream().anyMatch(usuario -> usuario.getEmail().equals("joao@email.com")));

        assertTrue(usuarios.stream().anyMatch(usuario -> usuario.getEmail().equals("maria@email.com")));
    }
}