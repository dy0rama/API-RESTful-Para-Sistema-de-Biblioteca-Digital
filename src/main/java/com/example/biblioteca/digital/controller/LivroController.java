package com.example.biblioteca.digital.controller;

import com.example.biblioteca.digital.requests.LivroRequest;
import com.example.biblioteca.digital.responses.LivroResponse;
import com.example.biblioteca.digital.services.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/livros")
@Tag(name = "Livros", description = "Operações de gerenciamento de livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @Operation(summary = "Cadastrar livro", description = "Cadastra um novo livro na biblioteca.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Livro cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados do livro inválidos")})
    @PostMapping
    public ResponseEntity<LivroResponse> cadastrar(@Valid @RequestBody LivroRequest request) {
        LivroResponse response = livroService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar livros", description = "Retorna todos os livros cadastrados na biblioteca.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livros encontrados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponse.class)))})
    @GetMapping
    public ResponseEntity<List<LivroResponse>> listarTodos() {
        return ResponseEntity.ok(livroService.listarTodos());
    }

    @Operation(summary = "Buscar livro por ID", description = "Busca um livro específico utilizando seu identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID informado possui formato inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Livro não encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @Operation(summary = "Atualizar livro", description = "Atualiza os dados de um livro existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados do livro ou ID inválidos"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")})
    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody LivroRequest request) {
        return ResponseEntity.ok(livroService.atualizar(id, request));
    }

    @Operation(summary = "Excluir livro", description = "Exclui um livro cadastrado na biblioteca.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Livro excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID informado possui formato inválido"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrar livros por autor", description =
            "Retorna os livros escritos pelo autor informado, ignorando diferenças entre letras maiúsculas e minúsculas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtro realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Autor não informado ou parâmetro inválido")})
    @GetMapping("/autor")
    public ResponseEntity<List<LivroResponse>> filtrarPorAutor(@RequestParam String autor) {
        return ResponseEntity.ok(livroService.filtrarPorAutor(autor));
    }

    @Operation(summary = "Buscar livros por título", description =
            "Retorna os livros que possuem o título informado, ignorando diferenças entre letras maiúsculas e minúsculas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Título não informado ou parâmetro inválido")})
    @GetMapping("/titulo")
    public ResponseEntity<List<LivroResponse>> buscarPorTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(livroService.buscarPorTitulo(titulo));
    }

    @Operation(summary = "Listar livros ordenados por título", description =
            "Retorna todos os livros em ordem alfabética pelo título.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livros ordenados por título com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponse.class)))})
    @GetMapping("/ordenados/titulo")
    public ResponseEntity<List<LivroResponse>> listarOrdenadosPorTitulo() {
        return ResponseEntity.ok(livroService.listarOrdenadosPorTitulo());
    }

    @Operation(summary = "Listar livros ordenados por ano", description =
            "Retorna todos os livros ordenados pelo ano de publicação, do mais antigo para o mais recente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livros ordenados por ano com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponse.class)))})
    @GetMapping("/ordenados/ano")
    public ResponseEntity<List<LivroResponse>> listarOrdenadosPorAno() {
        return ResponseEntity.ok(livroService.listarOrdenadosPorAno());
    }

    @Operation(summary = "Agrupar livros por autor", description =
            "Retorna os livros agrupados de acordo com o autor.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livros agrupados por autor com sucesso")})
    @GetMapping("/agrupados/autor")
    public ResponseEntity<Map<String, List<LivroResponse>>> agruparPorAutor() {
        return ResponseEntity.ok(livroService.agruparPorAutor());
    }
}
