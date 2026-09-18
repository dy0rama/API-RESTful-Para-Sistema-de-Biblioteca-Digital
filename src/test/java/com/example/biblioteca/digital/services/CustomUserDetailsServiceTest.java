package com.example.biblioteca.digital.services;

import com.example.biblioteca.digital.entities.Usuario;
import com.example.biblioteca.digital.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);

    private final CustomUserDetailsService service =
            new CustomUserDetailsService(usuarioRepository);

    @Test
    void deveCarregarUsuarioPeloEmail() {
        Usuario usuario = new Usuario(
                "João da Silva",
                "joao@email.com",
                "123456");

        when(usuarioRepository.findByEmailIgnoreCase("joao@email.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = service.loadUserByUsername("joao@email.com");

        assertNotNull(userDetails);
        assertEquals("joao@email.com", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_USER")));
    }

    @Test
    void deveAceitarEmailIgnorandoMaiusculasEMinusculas() {

        Usuario usuario = new Usuario(
                "Maria Oliveira",
                "maria@email.com",
                "123456");

        when(usuarioRepository.findByEmailIgnoreCase("MARIA@EMAIL.COM")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = service.loadUserByUsername("MARIA@EMAIL.COM");

        assertNotNull(userDetails);
        assertEquals("maria@email.com", userDetails.getUsername());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {
        when(usuarioRepository.findByEmailIgnoreCase("inexistente@email.com")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class, () -> service.loadUserByUsername("inexistente@email.com"));

        verify(usuarioRepository).findByEmailIgnoreCase("inexistente@email.com");
    }
}
