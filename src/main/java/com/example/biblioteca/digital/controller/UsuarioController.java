package com.example.biblioteca.digital.controller;

import com.example.biblioteca.digital.requests.UsuarioRequest;
import com.example.biblioteca.digital.responses.UsuarioResponse;
import com.example.biblioteca.digital.services.UsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Operações de gerenciamento de usuários")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cadastrar usuário", description = "Cadastra um novo usuário no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados do usuário inválidos")})
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.cadastrar(request);
        return ResponseEntity.status(201).body(usuario);
    }

    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)))})
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Buscar usuário por ID", description =
            "Busca um usuário específico utilizando seu identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID informado possui formato inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")})
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        UsuarioResponse usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados do usuário ou ID inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")})
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.atualizar(id, request);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Excluir usuário", description = "Exclui um usuário cadastrado no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID informado possui formato inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar usuário por e-mail", description =
            "Retorna os usuários que possuem o e-mail informado, ignorando diferenças entre letras maiúsculas e minúsculas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "E-mail não informado ou inválido")})
    @GetMapping("/email")
    public ResponseEntity<List<UsuarioResponse>> buscarPorEmail(@RequestParam String email) {
        List<UsuarioResponse> usuarios = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuarios);
    }
}
