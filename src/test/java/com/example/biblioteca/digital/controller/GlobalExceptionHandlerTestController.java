package com.example.biblioteca.digital.controller;

import com.example.biblioteca.digital.exceptions.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GlobalExceptionHandlerTestController {
    @GetMapping("/teste/livros/{id}")
    public void buscarLivro(@PathVariable UUID id) {
        throw new LivroNaoEncontradoException(id);
    }

    @GetMapping("/teste/usuarios/{id}")
    public void buscarUsuario(@PathVariable UUID id) {
        throw new UsuarioNaoEncontradoException(id);
    }

    @GetMapping("/teste/emprestimos/{id}")
    public void buscarEmprestimo(@PathVariable UUID id) {
        throw new EmprestimoNaoEncontradoException(id);
    }

    @PostMapping("/teste/emprestimos/{id}/devolver")
    public void devolverEmprestimo(@PathVariable UUID id) {
        throw new EmprestimoJaDevolvidoException(id);
    }

    @PostMapping("/teste/livros/{id}/emprestar")
    public void emprestarLivro(@PathVariable UUID id) {
        throw new LivroIndisponivelException(id);
    }
}
