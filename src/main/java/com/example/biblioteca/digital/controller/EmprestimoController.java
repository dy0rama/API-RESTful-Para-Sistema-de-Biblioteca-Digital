package com.example.biblioteca.digital.controller;

import com.example.biblioteca.digital.requests.EmprestimoRequest;
import com.example.biblioteca.digital.responses.EmprestimoResponse;
import com.example.biblioteca.digital.services.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/emprestimos")
@Tag(name = "Empréstimos", description = "Operações de gerenciamento de empréstimos de livros")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @Operation(
            summary = "Criar empréstimo",
            description = "Cria um novo empréstimo associando um usuário a um livro disponível. "
                    + "Após a criação, o livro passa a ser considerado indisponível.")

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empréstimo criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados do empréstimo inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário ou livro não encontrado"),
            @ApiResponse(responseCode = "409", description = "Livro indisponível para empréstimo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                            {
                                              "timestamp": "2026-09-17T18:30:00",
                                              "status": 409,
                                              "erro": "Livro indisponível",
                                              "mensagem": "Livro indisponível para empréstimo. ID: 550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """)))})
    @PostMapping
    public ResponseEntity<EmprestimoResponse> criar(@Valid @RequestBody EmprestimoRequest request) {
        EmprestimoResponse emprestimo = emprestimoService.criar(request);
        return ResponseEntity.status(201).body(emprestimo);
    }

    @Operation(summary = "Listar empréstimos", description = "Retorna todos os empréstimos registrados no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empréstimos listados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponse.class)))})
    @GetMapping
    public ResponseEntity<List<EmprestimoResponse>> listarTodos() {
        List<EmprestimoResponse> emprestimos = emprestimoService.listarTodos();
        return ResponseEntity.ok(emprestimos);
    }

    @Operation(summary = "Buscar empréstimo por ID", description =
            "Busca um empréstimo específico utilizando seu identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empréstimo encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID do empréstimo inválido"),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")})
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponse> buscarPorId(@PathVariable UUID id) {
        EmprestimoResponse emprestimo = emprestimoService.buscarPorId(id);
        return ResponseEntity.ok(emprestimo);
    }

    @Operation(
            summary = "Devolver livro", description =
            "Registra a devolução de um empréstimo e torna o livro novamente disponível.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro devolvido com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID do empréstimo inválido"),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Empréstimo já foi devolvido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                            {
                                              "timestamp": "2026-09-17T18:30:00",
                                              "status": 409,
                                              "erro": "Empréstimo já devolvido",
                                              "mensagem": "Empréstimo já foi devolvido. ID: 550e8400-e29b-41d4-a716-446655440000"
                                            }
                                            """)))})
    @PatchMapping("/{id}/devolver")
    public ResponseEntity<EmprestimoResponse> devolver(@PathVariable UUID id) {
        EmprestimoResponse emprestimo = emprestimoService.devolver(id);
        return ResponseEntity.ok(emprestimo);
    }
}
