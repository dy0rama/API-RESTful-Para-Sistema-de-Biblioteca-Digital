package com.example.biblioteca.digital.mapper;

import com.example.biblioteca.digital.entities.Emprestimo;
import com.example.biblioteca.digital.responses.EmprestimoResponse;
import org.springframework.stereotype.Component;

@Component
public class EmprestimoMapper {
    public EmprestimoResponse toResponse(Emprestimo emprestimo) {
        return new EmprestimoResponse(
                emprestimo.getId(),
                emprestimo.getUsuario().getId(),
                emprestimo.getUsuario().getNome(),
                emprestimo.getLivro().getId(),
                emprestimo.getLivro().getTitulo(),
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataDevolucao());
    }
}
