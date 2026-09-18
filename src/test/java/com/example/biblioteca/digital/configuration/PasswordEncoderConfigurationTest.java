package com.example.biblioteca.digital.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderConfigurationTest {
    private final PasswordEncoder passwordEncoder = new PasswordEncoderConfiguration().passwordEncoder();

    @Test
    void deveCodificarSenha() {
        String senha = "123456";

        String senhaCriptografada = passwordEncoder.encode(senha);

        assertNotNull(senhaCriptografada);
        assertNotEquals(senha, senhaCriptografada);
    }

    @Test
    void deveValidarSenhaCorreta() {
        String senha = "123456";

        String senhaCriptografada = passwordEncoder.encode(senha);

        assertTrue(passwordEncoder.matches(senha, senhaCriptografada));
    }

    @Test
    void naoDeveValidarSenhaIncorreta() {
        String senha = "123456";

        String senhaCriptografada = passwordEncoder.encode(senha);

        assertFalse(passwordEncoder.matches("senha-incorreta", senhaCriptografada));
    }
}
