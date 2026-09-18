# 📚 Sistema de Biblioteca Digital

API RESTful desenvolvida em **Java com Spring Boot** para gerenciamento de uma biblioteca digital, contemplando cadastro e gerenciamento de livros e usuários, controle de empréstimos e devoluções, disponibilidade dos livros, consultas, filtros, ordenações, agrupamentos, persistência em PostgreSQL, tratamento global de exceções, testes automatizados, documentação OpenAPI/Swagger e autenticação utilizando **Spring Security, OAuth 2.0, OpenID Connect e Authorization Code com PKCE**.

O projeto foi desenvolvido com foco na aplicação prática de conceitos de desenvolvimento Backend, Programação Orientada a Objetos, Collections, Generics, Streams, Lambda, Comparable, Comparator, Optional, JPA/Hibernate, REST, validação, tratamento de exceções, testes automatizados e segurança.

---

## 🎯 Objetivo do projeto

O objetivo do projeto é simular uma API de gerenciamento de uma biblioteca digital, permitindo controlar:

    * livros cadastrados;
    * autores;
    * usuários;
    * disponibilidade dos livros;
    * empréstimos;
    * devoluções;
    * histórico de empréstimos;
    * consultas por título;
    * filtros por autor;
    * busca de usuários por e-mail;
    * ordenação de livros;
    * agrupamento de livros por autor;
    * autenticação de usuários;
    * autorização de acesso à API;
    * documentação interativa através do Swagger UI.

Além da implementação das funcionalidades, o projeto foi estruturado para demonstrar uma evolução gradual de uma API REST, começando pelas regras de negócio e persistência e posteriormente incorporando validação, testes, documentação e segurança.

---

# 🚀 Principais funcionalidades

## 📖 Gestão de livros

A API permite:

    * cadastrar livros;
    * listar todos os livros;
    * buscar livro por ID;
    * atualizar livros;
    * excluir livros;
    * consultar livros por título;
    * filtrar livros por autor;
    * ordenar livros por título;
    * ordenar livros por ano de publicação;
    * agrupar livros por autor;
    * verificar disponibilidade do livro.

Cada livro possui:

```text
    ID
    Título
    Autor
    Ano de publicação
    Disponibilidade
```

A disponibilidade é controlada automaticamente pelo sistema.

Quando um livro é emprestado:

```text
    disponivel = false
```

Quando o livro é devolvido:

```text
    disponivel = true
```

---

# 👤 Gestão de usuários

A API também possui gerenciamento completo de usuários.

Funcionalidades:

    * cadastrar usuário;
    * listar usuários;
    * buscar usuário por ID;
    * atualizar usuário;
    * excluir usuário;
    * buscar usuário por e-mail.

Cada usuário possui:

```text
    ID
    Nome
    E-mail
    Senha
```

A senha não é retornada pela API.

O cadastro e a atualização utilizam `PasswordEncoder` com **BCrypt**, garantindo que a senha não seja armazenada em texto puro no banco de dados.

---

# 📚 Empréstimos

O módulo de empréstimos relaciona:

```text
    Usuário
       +
    Livro
       ↓
    Empréstimo
```

Um empréstimo registra:

```text
    ID
    Usuário
    Livro
    Data do empréstimo
    Data da devolução
```

Ao criar um empréstimo, o sistema:

1. verifica se o usuário existe;
2. verifica se o livro existe;
3. verifica se o livro está disponível;
4. cria o empréstimo;
5. registra a data e hora do empréstimo;
6. altera o livro para indisponível.

Fluxo:

```text
    Livro disponível
           ↓
    Solicitação de empréstimo
           ↓
    Validação do usuário
           ↓
    Validação do livro
           ↓
    Criação do empréstimo
           ↓
    Livro indisponível
```

---

# 🔄 Devolução de livros

A devolução é realizada através de:

```http
    PATCH /api/emprestimos/{id}/devolver
```

Ao devolver um livro:

    1. o empréstimo é localizado;
    2. verifica-se se ele já foi devolvido;
    3. a data e hora da devolução são registradas;
    4. o livro volta a ficar disponível.

Fluxo:

```text
    Empréstimo ativo
           ↓
    Devolução
           ↓
    dataDevolucao registrada
           ↓
    Livro disponível novamente
```

Uma segunda tentativa de devolução do mesmo empréstimo gera conflito.

---

# 🧠 Regras de negócio

As principais regras implementadas são:

### Livro inexistente

Operações sobre um livro que não existe geram:

```http
    404 Not Found
```

---

### Usuário inexistente

Operações que dependem de um usuário inexistente geram:

```http
    404 Not Found
```

---

### Empréstimo inexistente

Uma consulta ou devolução de empréstimo inexistente gera:

```http
    404 Not Found
```

---

### Livro indisponível

Um livro que já está emprestado não pode ser emprestado novamente.

Resultado:

```http
    409 Conflict
```

---

### Empréstimo já devolvido

Um empréstimo que já possui data de devolução não pode ser devolvido novamente.

Resultado:

```http
    409 Conflict
```

---

### Senhas

As senhas recebidas pela API são criptografadas utilizando BCrypt antes da persistência.

A senha nunca é incluída no `UsuarioResponse`.

---

# 🧩 Arquitetura

O projeto utiliza uma arquitetura organizada em camadas:

```text
    Controller
        ↓
    Service
        ↓
    Repository
        ↓
    PostgreSQL
```

Complementando o fluxo:

```text
    Request DTO
        ↓
    Controller
        ↓
    Service
        ↓
    Entity
        ↓
    Repository
        ↓
    Database
    
    Database
        ↓
    Entity
        ↓
    Mapper
        ↓
    Response DTO
        ↓
    Controller
```

A aplicação possui separação clara de responsabilidades entre:

    * Controllers;
    * Services;
    * Repositories;
    * Entities;
    * DTOs;
    * Mappers;
    * Exceptions;
    * Security;
    * Configuration.

---

# 🗂️ Estrutura do projeto

```text
    src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── example/
    │   │           └── biblioteca/
    │   │               └── digital/
    │   │
    │   │                   ├── configuration/
    │   │                   │   ├── AuthorizationServerConfiguration.java
    │   │                   │   ├── OpenApiConfiguration.java
    │   │                   │   ├── PasswordEncoderConfiguration.java
    │   │                   │   └── SecurityConfiguration.java
    │   │                   │
    │   │                   ├── controller/
    │   │                   │   ├── EmprestimoController.java
    │   │                   │   ├── LivroController.java
    │   │                   │   └── UsuarioController.java
    │   │                   │
    │   │                   ├── entities/
    │   │                   │   ├── Emprestimo.java
    │   │                   │   ├── Livro.java
    │   │                   │   └── Usuario.java
    │   │                   │
    │   │                   ├── exceptions/
    │   │                   │   ├── EmprestimoJaDevolvidoException.java
    │   │                   │   ├── EmprestimoNaoEncontradoException.java
    │   │                   │   ├── GlobalExceptionHandler.java
    │   │                   │   ├── LivroIndisponivelException.java
    │   │                   │   ├── LivroNaoEncontradoException.java
    │   │                   │   └── UsuarioNaoEncontradoException.java
    │   │                   │
    │   │                   ├── mapper/
    │   │                   │   ├── EmprestimoMapper.java
    │   │                   │   ├── LivroMapper.java
    │   │                   │   └── UsuarioMapper.java
    │   │                   │
    │   │                   ├── repositories/
    │   │                   │   ├── EmprestimoRepository.java
    │   │                   │   ├── LivroRepository.java
    │   │                   │   └── UsuarioRepository.java
    │   │                   │
    │   │                   ├── requests/
    │   │                   │   ├── EmprestimoRequest.java
    │   │                   │   ├── LivroRequest.java
    │   │                   │   └── UsuarioRequest.java
    │   │                   │
    │   │                   ├── responses/
    │   │                   │   ├── EmprestimoResponse.java
    │   │                   │   ├── LivroResponse.java
    │   │                   │   └── UsuarioResponse.java
    │   │                   │
    │   │                   ├── services/
    │   │                   │   ├── CustomUserDetailsService.java
    │   │                   │   ├── EmprestimoService.java
    │   │                   │   ├── LivroService.java
    │   │                   │   └── UsuarioService.java
    │   │                   │
    │   │                   └── SistemaBibliotecaDigitalApplication.java
    │   │
    │   └── resources/
    │       ├── application.properties
    │       ├── static/
    │       └── templates/
    │
    └── test/
        └── java/
            └── com/
                └── example/
                    └── biblioteca/
                        └── digital/
                            ├── configuration/
                            ├── controller/
                            ├── exceptions/
                            ├── mapper/
                            ├── repositories/
                            ├── services/
                            └── SistemaBibliotecaDigitalApplicationTests.java
```

---

# 🏗️ Camadas da aplicação

## Controller

Os Controllers são responsáveis pela exposição dos endpoints HTTP.

Principais Controllers:

```text
    LivroController
    UsuarioController
    EmprestimoController
```

Responsabilidades:

    * receber requisições;
    * validar DTOs;
    * encaminhar operações para os Services;
    * definir códigos HTTP;
    * documentar endpoints com OpenAPI.

---

## Service

A camada Service concentra as regras de negócio.

Principais Services:

```text
    LivroService
    UsuarioService
    EmprestimoService
    CustomUserDetailsService
```

Exemplos de responsabilidades:

    * criação e atualização de entidades;
    * validação de existência;
    * controle de disponibilidade;
    * criação e devolução de empréstimos;
    * aplicação de BCrypt;
    * filtros;
    * ordenações;
    * agrupamentos;
    * autenticação através do `UserDetailsService`.

---

## Repository

A persistência utiliza Spring Data JPA.

Repositories:

```text
    LivroRepository
    UsuarioRepository
    EmprestimoRepository
```

Todos utilizam `JpaRepository`.

Os IDs das entidades são `UUID`.

---

# 🧱 Entidades

## Livro

```text
    Livro
    ├── UUID id
    ├── String titulo
    ├── String autor
    ├── Integer anoPublicacao
    └── boolean disponivel
```

A entidade implementa:

```java
    Comparable<Livro>
```

permitindo a ordenação natural dos livros pelo título.

A comparação utiliza:

```java
    compareToIgnoreCase()
```

portanto diferenças entre letras maiúsculas e minúsculas não interferem na ordenação.

---

## Usuario

```text
    Usuario
    ├── UUID id
    ├── String nome
    ├── String email
    └── String senha
```

O e-mail possui restrição de unicidade no banco.

A senha é armazenada utilizando BCrypt.

---

## Emprestimo

```text
    Emprestimo
    ├── UUID id
    ├── Usuario usuario
    ├── Livro livro
    ├── LocalDateTime dataEmprestimo
    └── LocalDateTime dataDevolucao
```

Os relacionamentos utilizam `@ManyToOne`.

Foi utilizado `FetchType.LAZY` nos relacionamentos para evitar carregamento desnecessário das entidades relacionadas.

---

# 🔗 Relacionamentos

O modelo pode ser representado da seguinte forma:

```text
    ┌──────────────┐
    │   Usuario    │
    │              │
    │ id           │
    │ nome         │
    │ email        │
    │ senha        │
    └──────┬───────┘
           │
           │ 1:N
           │
           ▼
    ┌──────────────┐
    │  Emprestimo  │
    │              │
    │ id           │
    │ usuario      │
    │ livro        │
    │ dataEmpr.    │
    │ dataDevol.   │
    └──────┬───────┘
           │
           │ N:1
           │
           ▼
    ┌──────────────┐
    │    Livro     │
    │              │
    │ id           │
    │ titulo       │
    │ autor        │
    │ ano          │
    │ disponivel   │
    └──────────────┘
```

A ausência de `@OneToMany` nas entidades `Usuario` e `Livro` também evita ciclos desnecessários durante a serialização JSON.

---

# 📦 DTOs

O projeto utiliza DTOs específicos para entrada e saída.

## Requests

```text
    LivroRequest
    UsuarioRequest
    EmprestimoRequest
```

## Responses

```text
    LivroResponse
    UsuarioResponse
    EmprestimoResponse
```

Essa separação evita que as entidades JPA sejam utilizadas diretamente como contrato da API.

---

# 🔄 Mappers

Os Mappers são responsáveis pela conversão entre:

```text
    Request → Entity
    Entity → Response
```

Classes:

```text
    LivroMapper
    UsuarioMapper
    EmprestimoMapper
```

Isso reduz repetição de código nos Services e mantém as responsabilidades separadas.

---

# 🧮 Collections, Generics e Streams

O projeto também foi desenvolvido para aplicar conceitos fundamentais da linguagem Java.

São utilizados:

    * `List`;
    * `Map`;
    * `Optional`;
    * Generics;
    * Streams;
    * Lambda;
    * Collectors;
    * Comparator;
    * Comparable.

---

# 🔎 Stream — filtro por autor

A consulta de livros por autor utiliza Stream:

```java
    livroRepository.findAll()
            .stream()
            .filter(livro ->
                    livro.getAutor().equalsIgnoreCase(autor))
            .map(livroMapper::toResponse)
            .toList();
```

O filtro é case-insensitive.

Por exemplo:

```text
    Robert C. Martin
    robert c. martin
    ROBERT C. MARTIN
```

são tratados como o mesmo autor para essa operação.

---

# 🔎 Stream — busca por título

A busca por título também utiliza Stream:

```java
    livroRepository.findAll()
            .stream()
            .filter(livro ->
                    livro.getTitulo().equalsIgnoreCase(titulo))
            .map(livroMapper::toResponse)
            .toList();
```

---

# 📊 Ordenação com Comparator

Os livros podem ser ordenados pelo ano:

```java
    .sorted(Comparator.comparing(Livro::getAnoPublicacao))
```

O resultado é do livro mais antigo para o mais recente.

---

# 🔤 Comparable

A ordenação por título utiliza a implementação de:

```java
    Comparable<Livro>
```

e:

```java
    Comparator.naturalOrder()
```

Isso demonstra a implementação de uma ordenação natural para o domínio `Livro`.

---

# 📚 Agrupamento com Collectors

Também foi implementado agrupamento dos livros por autor:

```java
    Collectors.groupingBy(
        Livro::getAutor,
        Collectors.mapping(
            livroMapper::toResponse,
            Collectors.toList()
        )
    )
```

O resultado possui o formato conceitual:

```json
    {
      "Robert C. Martin": [
        {
          "titulo": "Clean Code"
        }
      ],
      "Joshua Bloch": [
        {
          "titulo": "Effective Java"
        }
      ]
    }
```

---

# 🔐 Segurança

A aplicação utiliza:

    * Spring Security;
    * BCrypt;
    * `UserDetailsService`;
    * DaoAuthenticationProvider;
    * OAuth 2.0;
    * Authorization Server;
    * OpenID Connect;
    * Authorization Code;
    * PKCE;
    * JWT/OAuth2 infrastructure;
    * integração com Swagger UI.

---

# 🔑 BCrypt

As senhas dos usuários não são armazenadas em texto puro.

O projeto utiliza:

```java
    BCryptPasswordEncoder
```

através de um `PasswordEncoder`.

Fluxo:

```text
    Senha enviada
         ↓
    PasswordEncoder
         ↓
    BCrypt
         ↓
    Hash armazenado
```

Durante a autenticação, o Spring Security compara a senha fornecida com o hash armazenado.

---

# 👤 CustomUserDetailsService

A autenticação consulta o usuário através do e-mail.

O `CustomUserDetailsService` utiliza:

```text
    UsuarioRepository
```

e:

```text
    findByEmailIgnoreCase()
```

para localizar o usuário.

O usuário autenticado recebe a autoridade:

```text
    ROLE_USER
```

---

# 🔐 OAuth 2.0

O projeto implementa um Authorization Server utilizando Spring Authorization Server.

O cliente registrado é:

```text
    Client ID:
    sistema-biblioteca-client
```

O fluxo utilizado é:

```text
    Authorization Code
```

com:

```text
    PKCE
```

---

# 🛡️ PKCE

O cliente é configurado como cliente público:

```text
    ClientAuthenticationMethod.NONE
```

e exige Proof Key:

```text
    requireProofKey(true)
```

O Swagger UI também está configurado para utilizar PKCE:

```properties
    springdoc.swagger-ui.oauth.use-pkce-with-authorization-code-grant=true
```

Durante a autenticação, o Swagger gera:

```text
    code_challenge
    code_challenge_method=S256
```

O fluxo fica:

```text
    Swagger UI
          ↓
    Authorization Request
          ↓
    code_challenge
          ↓
    Login
          ↓
    Authorization Code
          ↓
    Swagger Redirect
          ↓
    Token Endpoint
          ↓
    Access Token
```

---

# 🌐 OpenID Connect

O Authorization Server também habilita OpenID Connect.

Os scopes utilizados pelo cliente são:

```text
    openid
    profile
```

O OpenID Connect foi habilitado na configuração de segurança através da configuração OIDC do Authorization Server.

---

# 🔒 Cadeias de segurança

A aplicação possui duas `SecurityFilterChain`.

## Ordem 1 — Authorization Server

Responsável pelos endpoints do Authorization Server:

```text
    /oauth2/authorize
    /oauth2/token
```

e demais endpoints relacionados ao OAuth2/OIDC.

---

## Ordem 2 — Aplicação

Responsável pela proteção da aplicação e dos endpoints da API.

Os endpoints do Swagger são liberados:

```text
    /swagger-ui/**
    /v3/api-docs/**
    /swagger-ui.html
```

Enquanto as demais requisições exigem autenticação.

---

# 📘 Swagger / OpenAPI

A documentação da API foi criada utilizando:

```text
    springdoc-openapi-starter-webmvc-ui
```

Versão utilizada:

```text
    3.1.1
```

A documentação possui:

    * descrição dos Controllers;
    * descrição dos endpoints;
    * parâmetros;
    * respostas HTTP;
    * schemas;
    * autenticação OAuth2;
    * scopes;
    * fluxo Authorization Code;
    * PKCE.

---

# 🌐 Swagger UI

Com a aplicação em execução:

```text
    http://localhost:8080/swagger-ui/index.html
```

A documentação apresenta os grupos:

```text
    Livros
    Usuários
    Empréstimos
```

e permite testar os endpoints diretamente pelo navegador.

---

# 🔑 Autenticação pelo Swagger

O fluxo de autenticação é:

```text
    1. Abrir Swagger
           ↓
    2. Clicar em Authorize
           ↓
    3. Selecionar sistema-biblioteca-client
           ↓
    4. Swagger gera code_challenge
           ↓
    5. Authorization Server
           ↓
    6. Login
           ↓
    7. Authorization Code
           ↓
    8. Callback para Swagger
           ↓
    9. Token Endpoint
           ↓
    10. Access Token
           ↓
    11. Chamadas autenticadas à API
```

Esse fluxo foi validado manualmente durante o desenvolvimento.

---

# 📡 Endpoints

## 📖 Livros

### Cadastrar livro

```http
    POST /api/livros
```

Exemplo:

```json
    {
      "titulo": "Clean Code",
      "autor": "Robert C. Martin",
      "anoPublicacao": 2008
    }
```

Resposta:

```text
    201 Created
```

---

### Listar livros

```http
    GET /api/livros
```

Resposta:

```text
    200 OK
```

---

### Buscar livro por ID

```http
    GET /api/livros/{id}
```

Respostas:

```text
    200 OK
    400 Bad Request
    404 Not Found
```

---

### Atualizar livro

```http
    PUT /api/livros/{id}
```

Exemplo:

```json
    {
      "titulo": "Clean Code - 2ª Edição",
      "autor": "Robert C. Martin",
      "anoPublicacao": 2020
    }
```

Respostas:

```text
    200 OK
    400 Bad Request
    404 Not Found
```

---

### Excluir livro

```http
    DELETE /api/livros/{id}
```

Respostas:

```text
    204 No Content
    400 Bad Request
    404 Not Found
```

---

### Filtrar livros por autor

```http
    GET /api/livros/autor?autor=Robert%20C.%20Martin
```

Resposta:

```text
    200 OK
```

---

### Buscar livros por título

```http
    GET /api/livros/titulo?titulo=Clean%20Code
```

Resposta:

```text
    200 OK
```

---

### Ordenar livros por título

```http
    GET /api/livros/ordenados/titulo
```

Resposta:

```text
    200 OK
```

---

### Ordenar livros por ano

```http
    GET /api/livros/ordenados/ano
```

Resposta:

```text
    200 OK
```

Os livros são retornados do ano mais antigo para o mais recente.

---

### Agrupar livros por autor

```http
    GET /api/livros/agrupados/autor
```

Resposta:

```text
    200 OK
```

---

# 👤 Usuários

### Cadastrar usuário

```http
    POST /api/usuarios
```

Exemplo:

```json
    {
      "nome": "João da Silva",
      "email": "joao@email.com",
      "senha": "123456"
    }
```

Resposta:

```text
    201 Created
```

A senha não aparece na resposta.

---

### Listar usuários

```http
    GET /api/usuarios
```

Resposta:

```text
    200 OK
```

---

### Buscar usuário por ID

```http
    GET /api/usuarios/{id}
```

Respostas:

```text
    200 OK
    400 Bad Request
    404 Not Found
```

---

### Atualizar usuário

```http
    PUT /api/usuarios/{id}
```

Exemplo:

```json
    {
      "nome": "João Atualizado",
      "email": "joao@email.com",
      "senha": "novaSenha"
    }
```

A nova senha também é armazenada utilizando BCrypt.

---

### Excluir usuário

```http
    DELETE /api/usuarios/{id}
```

Resposta:

```text
    204 No Content
```

---

### Buscar usuário por e-mail

```http
    GET /api/usuarios/email?email=joao@email.com
```

A comparação ignora diferenças entre maiúsculas e minúsculas.

---

# 📚 Empréstimos

### Criar empréstimo

```http
    POST /api/emprestimos
```

Exemplo:

```json
    {
      "usuarioId": "UUID_DO_USUARIO",
      "livroId": "UUID_DO_LIVRO"
    }
```

Respostas:

```text
    201 Created
    400 Bad Request
    404 Not Found
    409 Conflict
```

O conflito ocorre quando o livro está indisponível.

---

### Listar empréstimos

```http
    GET /api/emprestimos
```

Resposta:

```text
    200 OK
```

---

### Buscar empréstimo

```http
    GET /api/emprestimos/{id}
```

Respostas:

```text
    200 OK
    400 Bad Request
    404 Not Found
```

---

### Devolver livro

```http
    PATCH /api/emprestimos/{id}/devolver
```

Respostas:

```text
    200 OK
    400 Bad Request
    404 Not Found
    409 Conflict
```

O conflito ocorre quando o empréstimo já foi devolvido.

---

# 📊 Resumo dos endpoints

    | Método | Endpoint                         | Função             |
    | ------ | -------------------------------- | ------------------ |
    | POST   | `/api/livros`                    | Cadastrar livro    |
    | GET    | `/api/livros`                    | Listar livros      |
    | GET    | `/api/livros/{id}`               | Buscar livro       |
    | PUT    | `/api/livros/{id}`               | Atualizar livro    |
    | DELETE | `/api/livros/{id}`               | Excluir livro      |
    | GET    | `/api/livros/autor`              | Filtrar por autor  |
    | GET    | `/api/livros/titulo`             | Buscar por título  |
    | GET    | `/api/livros/ordenados/titulo`   | Ordenar por título |
    | GET    | `/api/livros/ordenados/ano`      | Ordenar por ano    |
    | GET    | `/api/livros/agrupados/autor`    | Agrupar por autor  |
    | POST   | `/api/usuarios`                  | Cadastrar usuário  |
    | GET    | `/api/usuarios`                  | Listar usuários    |
    | GET    | `/api/usuarios/{id}`             | Buscar usuário     |
    | PUT    | `/api/usuarios/{id}`             | Atualizar usuário  |
    | DELETE | `/api/usuarios/{id}`             | Excluir usuário    |
    | GET    | `/api/usuarios/email`            | Buscar por e-mail  |
    | POST   | `/api/emprestimos`               | Criar empréstimo   |
    | GET    | `/api/emprestimos`               | Listar empréstimos |
    | GET    | `/api/emprestimos/{id}`          | Buscar empréstimo  |
    | PATCH  | `/api/emprestimos/{id}/devolver` | Devolver livro     |

---

# ⚠️ Tratamento de erros

O projeto possui um `GlobalExceptionHandler` baseado em:

```java
    @RestControllerAdvice
```

As exceções de domínio são convertidas em respostas HTTP apropriadas.

### Livro não encontrado

```text
    404 Not Found
```

### Usuário não encontrado

```text
    404 Not Found
```

### Empréstimo não encontrado

```text
    404 Not Found
```

### Livro indisponível

```text
    409 Conflict
```

### Empréstimo já devolvido

```text
    409 Conflict
```

---

# 🧪 Testes automatizados

O projeto possui uma suíte de testes abrangente cobrindo diferentes camadas da aplicação.

Os testes estão distribuídos entre:

```text
    Configuration
    Controllers
    Exceptions
    Mappers
    Repositories
    Services
    Application Context
```

Ao analisar a estrutura de testes do projeto, foram identificados **90 métodos de teste**.

---

## Testes de configuração

```text
    PasswordEncoderConfigurationTest
```

Verifica a configuração do mecanismo de criptografia de senhas.

---

## Testes dos Controllers

```text
    LivroControllerTest
    UsuarioControllerTest
    EmprestimoControllerTest
```

São testadas as operações HTTP dos principais recursos.

---

## Testes dos Services

```text
    LivroServiceTest
    UsuarioServiceTest
    EmprestimoServiceTest
    CustomUserDetailsServiceTest
```

As regras de negócio são verificadas utilizando Mockito quando apropriado.

---

## Testes dos Repositories

```text
    LivroRepositoryTest
    UsuarioRepositoryTest
    EmprestimoRepositoryTest
```

Esses testes validam a integração da camada de persistência com JPA/PostgreSQL.

---

## Testes dos Mappers

```text
    LivroMapperTest
    UsuarioMapperTest
    EmprestimoMapperTest
```

Garantem a conversão correta entre entidades e DTOs.

---

## Testes de exceções

```text
    GlobalExceptionHandlerTest
    GlobalExceptionHandlerTestController
```

Validam o comportamento das exceções e das respostas HTTP correspondentes.

---

## Teste do contexto

```text
    SistemaBibliotecaDigitalApplicationTests
```

Verifica o carregamento do contexto da aplicação.

---

# 🗄️ Banco de dados

O projeto utiliza:

```text
    PostgreSQL
```

As configurações são obtidas através de variáveis de ambiente:

```properties
    DB_URL
    DB_USERNAME
    DB_PASSWORD
```

Configuração:

```properties
    spring.datasource.url=${DB_URL}
    spring.datasource.username=${DB_USERNAME}
    spring.datasource.password=${DB_PASSWORD}
```

O Hibernate está configurado para atualização automática do schema durante o desenvolvimento:

```properties
    spring.jpa.hibernate.ddl-auto=update
```

SQL e formatação SQL também estão habilitados:

```properties
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
```

---

# ⚙️ Configuração do ambiente

Antes de executar o projeto, é necessário possuir:

    * Java 26;
    * Maven ou Maven Wrapper;
    * PostgreSQL;
    * banco de dados criado;
    * variáveis de ambiente configuradas.

---

# 🔧 Variáveis de ambiente

Configure:

```text
    DB_URL
    DB_USERNAME
    DB_PASSWORD
```

Exemplo conceitual:

```text
    DB_URL=jdbc:postgresql://localhost:5432/biblioteca
    DB_USERNAME=postgres
    DB_PASSWORD=sua_senha
```

As credenciais reais não devem ser adicionadas ao Git.

---

# ▶️ Executando o projeto

Clone o repositório:

```bash
    git clone URL_DO_REPOSITORIO
```

Entre no diretório:

```bash
    cd sistemabibliotecadigital
```

Execute os testes:

```bash
    ./mvnw clean test
```

No Windows:

```bash
    mvnw.cmd clean test
```

Execute a aplicação:

```bash
    ./mvnw spring-boot:run
```

No Windows:

```bash
    mvnw.cmd spring-boot:run
```

---

# 📘 Acessando o Swagger

Depois que a aplicação estiver em execução:

```text
    http://localhost:8080/swagger-ui/index.html
```

A especificação OpenAPI pode ser acessada através de:

```text
    http://localhost:8080/v3/api-docs
```

e:

```text
    http://localhost:8080/v3/api-docs.yaml
```

---

# 🔐 Utilizando OAuth2 no Swagger

    1. Abra o Swagger UI.
    2. Clique em **Authorize**.
    3. Selecione o cliente:

```text
    sistema-biblioteca-client
```

    4. Utilize o fluxo Authorization Code.
    5. O Swagger gera automaticamente o `code_challenge`.
    6. O navegador será direcionado para o login.
    7. Informe as credenciais de um usuário cadastrado.
    8. Após a autenticação, o Authorization Server processará o Authorization Code.
    9. O Swagger realizará o fluxo de token.
    10. A API poderá ser chamada através do Swagger.

O cliente utiliza:

```text
    Client ID:
    sistema-biblioteca-client
```

Redirect URI:

```text
    http://localhost:8080/swagger-ui/oauth2-redirect.html
```

Scopes:

```text
    openid
    profile
```

---

# 🛠️ Tecnologias utilizadas

## Backend

    * Java 26
    * Spring Boot 4.1.1
    * Spring Web MVC
    * Spring Data JPA
    * Hibernate
    * Spring Validation

## Banco de dados

* PostgreSQL

## Segurança

    * Spring Security
    * BCrypt
    * Spring Authorization Server
    * OAuth 2.0
    * OpenID Connect
    * Authorization Code
    * PKCE
    * OAuth2 Resource Server

## Documentação

    * OpenAPI
    * Swagger UI
    * Springdoc OpenAPI 3.1.1

## Testes

    * JUnit
    * Mockito
    * Spring Boot Test
    * Spring MVC Test
    * Spring Data JPA Test

## Build

    * Maven
    * Maven Wrapper

---

# 🧠 Conceitos de Java aplicados

O projeto foi construído utilizando diversos recursos da linguagem Java:
    
    * Programação Orientada a Objetos;
    * encapsulamento;
    * classes;
    * interfaces;
    * enums quando aplicável;
    * Collections;
    * List;
    * Map;
    * Generics;
    * Optional;
    * Streams;
    * Lambda Expressions;
    * Comparable;
    * Comparator;
    * Collectors;
    * LocalDateTime;
    * UUID;
    * Records;
    * tratamento de exceções.

---

# 🏛️ Padrões e boas práticas utilizados

## Separation of Concerns

Cada camada possui responsabilidade específica.

```text
    Controller → HTTP
    Service → negócio
    Repository → persistência
    Mapper → conversão
    DTO → contrato
    Entity → domínio/persistência
```

---

## DTO Pattern

Os DTOs isolam as entidades do contrato externo da API.

---

## Mapper Pattern

As conversões entre Entity e DTO ficam centralizadas nos Mappers.

---

## Repository Pattern

Spring Data JPA fornece a abstração de persistência através dos Repositories.

---

## Global Exception Handling

O tratamento centralizado evita duplicação de código entre Controllers.

---

## Dependency Injection

As dependências são fornecidas pelo Spring através de injeção por construtor.

---

## Transaction Management

Operações que modificam simultaneamente empréstimos e livros utilizam:

```java
    @Transactional
```

garantindo que as alterações relacionadas sejam tratadas como uma unidade transacional.

---

# 🔄 Fluxo completo de um empréstimo

```text
    Cliente
       │
       │ POST /api/emprestimos
       ▼
    EmprestimoController
       │
       ▼
    EmprestimoService
       │
       ├──→ UsuarioRepository
       │       └── verifica usuário
       │
       ├──→ LivroRepository
       │       └── verifica livro
       │
       ├──→ verifica disponibilidade
       │
       ├──→ cria Emprestimo
       │
       ├──→ altera Livro
       │       └── disponivel = false
       │
       ├──→ salva empréstimo
       │
       └──→ retorna EmprestimoResponse
```

---

# 🔄 Fluxo completo de devolução

```text
    Cliente
       │
       │ PATCH /api/emprestimos/{id}/devolver
       ▼
    EmprestimoController
       │
       ▼
    EmprestimoService
       │
       ├──→ busca empréstimo
       │
       ├──→ verifica se já foi devolvido
       │
       ├──→ registra dataDevolucao
       │
       ├──→ recupera Livro
       │
       ├──→ disponivel = true
       │
       ├──→ salva empréstimo
       │
       └──→ salva livro
```

---

# 🔐 Fluxo completo de autenticação

```text
                     ┌─────────────────┐
                     │   Swagger UI    │
                     └────────┬────────┘
                              │
                              │ Authorization Code
                              │ + PKCE
                              ▼
                  ┌─────────────────────────┐
                  │   Authorization Server  │
                  └────────────┬────────────┘
                               │
                               ▼
                        Tela de Login
                               │
                               ▼
                    CustomUserDetailsService
                               │
                               ▼
                        UsuarioRepository
                               │
                               ▼
                        BCrypt / Password
                               │
                               ▼
                      Authorization Code
                               │
                               ▼
                      OAuth2 Token Endpoint
                               │
                               ▼
                         Access Token
                               │
                               ▼
                      Endpoints protegidos
```

---

# 📋 Status do projeto

    | Funcionalidade              | Status |
    | --------------------------- | ------ |
    | CRUD de livros              | ✅      |
    | Busca por título            | ✅      |
    | Filtro por autor            | ✅      |
    | Ordenação por título        | ✅      |
    | Ordenação por ano           | ✅      |
    | Agrupamento por autor       | ✅      |
    | Controle de disponibilidade | ✅      |
    | CRUD de usuários            | ✅      |
    | Busca por e-mail            | ✅      |
    | BCrypt                      | ✅      |
    | Criação de empréstimos      | ✅      |
    | Devolução                   | ✅      |
    | Histórico de empréstimos    | ✅      |
    | Regras de negócio           | ✅      |
    | Exceções personalizadas     | ✅      |
    | Global Exception Handler    | ✅      |
    | DTOs                        | ✅      |
    | Mappers                     | ✅      |
    | PostgreSQL                  | ✅      |
    | JPA/Hibernate               | ✅      |
    | Streams                     | ✅      |
    | Lambda                      | ✅      |
    | Comparable                  | ✅      |
    | Comparator                  | ✅      |
    | Generics                    | ✅      |
    | Testes automatizados        | ✅      |
    | Swagger/OpenAPI             | ✅      |
    | Spring Security             | ✅      |
    | OAuth 2.0                   | ✅      |
    | Authorization Server        | ✅      |
    | OpenID Connect              | ✅      |
    | Authorization Code          | ✅      |
    | PKCE                        | ✅      |
    | Integração Swagger + OAuth2 | ✅      |

---

# 📈 Possíveis evoluções

Embora o projeto esteja funcional, algumas evoluções poderiam ser implementadas futuramente:

### Banco de dados

    * Flyway;
    * Liquibase;
    * migrations versionadas.

### API

    * paginação;
    * filtros combinados;
    * ordenação dinâmica;
    * busca parcial por título;
    * busca parcial por autor;
    * endpoints específicos para livros disponíveis;
    * consulta de empréstimos por usuário;
    * consulta de empréstimos ativos;
    * histórico de empréstimos.

### Segurança

    * diferentes perfis de usuário;
    * autorização baseada em roles;
    * escopos OAuth2 específicos;
    * rotação de chaves;
    * persistência dos clientes OAuth2;
    * gerenciamento de consentimento.

### Infraestrutura

    * Docker;
    * Docker Compose;
    * Testcontainers;
    * CI/CD;
    * Spring Boot Actuator;
    * monitoramento;
    * logs estruturados.

Essas funcionalidades representam possíveis evoluções e não são necessárias para o funcionamento atual do projeto.

---

# 🎓 Objetivos de aprendizado

Este projeto foi desenvolvido para consolidar conhecimentos em:

### Java

```text
    POO
    Collections
    Generics
    Streams
    Lambda
    Comparable
    Comparator
    Optional
    LocalDateTime
    UUID
    Records
```

### Spring Boot

```text
    REST API
    Dependency Injection
    Spring Data JPA
    Validation
    Exception Handling
    Transactions
```

### Banco de dados

```text
    PostgreSQL
    JPA
    Hibernate
    Relacionamentos
    Persistência
```

### Segurança

```text
    Spring Security
    BCrypt
    OAuth2
    OpenID Connect
    Authorization Server
    Authorization Code
    PKCE
```

### Qualidade

```text
    JUnit
    Mockito
    Testes de Controller
    Testes de Service
    Testes de Repository
    Testes de Mapper
    Testes de configuração
```

### Documentação

```text
    OpenAPI
    Swagger UI
    OAuth2 no Swagger
```

---

# 👨‍💻 Autor

**Rodrigo Marques Viana**

Desenvolvedor Backend em formação, com foco em:

```text
    Java
    Spring Boot
    APIs REST
    JPA / Hibernate
    PostgreSQL
    Spring Security
    OAuth2
    Git / GitHub
```

---

# 📌 Considerações finais

O Sistema de Biblioteca Digital representa a evolução de uma aplicação inicialmente focada nas operações de domínio para uma API REST completa, persistente, documentada, testada e protegida.

O projeto reúne em uma única aplicação conceitos de:

```text
    Java
       ↓
    POO
       ↓
    Collections / Generics
       ↓
    Streams / Lambda
       ↓
    Spring Boot
       ↓
    REST
       ↓
    JPA / Hibernate
       ↓
    PostgreSQL
       ↓
    DTO / Mapper
       ↓
    Validation
       ↓
    Exception Handling
       ↓
    Testes
       ↓
    OpenAPI / Swagger
       ↓
    Spring Security
       ↓
    OAuth2
       ↓
    OpenID Connect
       ↓
    Authorization Code
       ↓
    PKCE
```

A arquitetura foi construída de maneira incremental, permitindo validar cada camada antes da introdução de novos recursos.

O resultado é uma API que não se limita a operações CRUD, incorporando regras de negócio, persistência relacional, processamento de coleções, tratamento de erros, testes automatizados, documentação interativa e um fluxo moderno de autenticação baseado em OAuth 2.0 e PKCE.
