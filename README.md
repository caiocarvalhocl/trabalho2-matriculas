# Trabalho Prático 2 - API de Cursos e Matrículas

Aluno: Caio Carvalho (UTFPR)

## Descrição

API REST em Spring Boot 3.5 (Java 17) para gerenciar cursos, alunos e as matrículas de alunos em cursos. Persistência com Spring Data JPA em banco H2 em memória (os dados somem ao encerrar a aplicação).

Camadas: `controllers` (rotas REST), `services` (regras de negócio), `repositories` (Spring Data JPA), `models` (entidades), `dtos` (request/response como `record`) e `exceptions` (erros e `@ControllerAdvice`).

Relacionamentos: `Curso` 1..N `Matricula` e `Aluno` 1..N `Matricula` (`@OneToMany(mappedBy)` nos dois lados, `@ManyToOne` em `Matricula`).

## Como executar

Requisitos: Java 17. O Maven vem pelo wrapper.

```bash
./mvnw spring-boot:run     # sobe em http://localhost:8080
./mvnw test                # roda os testes
```

Console do H2: `http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:mem:matriculas`, usuário `sa`, senha vazia.

A coleção de requisições para o [Bruno](https://www.usebruno.com/) está em `bruno/` (abrir a pasta como coleção; executar na ordem, com o banco recém-iniciado).

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/cursos` | Cadastrar curso |
| GET | `/cursos` | Listar cursos (paginado) |
| GET | `/cursos/{id}` | Buscar curso por id |
| PUT | `/cursos/{id}` | Atualizar curso |
| DELETE | `/cursos/{id}` | Remover curso |
| POST | `/alunos` | Cadastrar aluno |
| GET | `/alunos` | Listar alunos (paginado) |
| GET | `/alunos/{id}` | Buscar aluno por id |
| PUT | `/alunos/{id}` | Atualizar aluno |
| DELETE | `/alunos/{id}` | Remover aluno |
| POST | `/matriculas` | Realizar matrícula |
| GET | `/matriculas` | Listar matrículas (paginado) |
| GET | `/matriculas/{id}` | Buscar matrícula por id |
| PUT | `/matriculas/{id}` | Atualizar status da matrícula |
| DELETE | `/matriculas/{id}` | Cancelar matrícula (status passa a `CANCELADA`) |

### Paginação

Todas as listagens aceitam `page`, `size` e `sort` (padrão: `page=0`, `size=10`, `sort=id,asc`):

```
GET /cursos?page=0&size=5&sort=nome,asc
```

A resposta traz `content` e `page` (`size`, `number`, `totalElements`, `totalPages`). Campo de ordenação inexistente retorna 400.

### Corpos de requisição

Curso: `{"nome": "Spring Boot", "cargaHoraria": 40, "categoria": "Tecnologia", "ativo": true}`

Aluno: `{"nome": "Ana", "email": "ana@email.com", "idade": 20, "telefone": "41999999999"}` (`telefone` é opcional)

Matrícula (POST): `{"alunoId": 1, "cursoId": 1, "dataMatricula": "2026-03-01", "status": "ATIVA"}` (`status` opcional, padrão `ATIVA`)

Matrícula (PUT): `{"status": "CONCLUIDA"}`. Status aceitos: `ATIVA`, `CONCLUIDA`, `TRANCADA`, `CANCELADA`.

## Validações

| Campo | Regra |
|---|---|
| Curso.nome | obrigatório, mínimo 5 caracteres |
| Curso.cargaHoraria | obrigatória, maior que 0 |
| Curso.categoria | obrigatória |
| Curso.ativo | obrigatório |
| Aluno.nome | obrigatório |
| Aluno.email | obrigatório e válido |
| Aluno.idade | obrigatória, mínimo 16 |
| Matrícula.dataMatricula | obrigatória |
| Matrícula.status | obrigatório (um dos quatro valores) |
| Matrícula.aluno / curso | obrigatórios |

As anotações estão nas entidades e nos DTOs de entrada. Erro de validação retorna 400 com as mensagens por campo:

```json
{
  "timestamp": "2026-11-03T18:23:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados inválidos",
  "campos": {"nome": "O nome deve ter no mínimo 5 caracteres"}
}
```

## Regras de negócio

| Regra | Resposta |
|---|---|
| Matrícula em curso inativo | 422 |
| Matrícula de aluno menor de 16 anos | 422 |
| Excluir curso que possui matrículas | 409 |
| Excluir aluno que possui matrículas | 409 |
| Aluno, curso ou matrícula inexistente | 404 |

Matrículas canceladas continuam no banco (só mudam de status), então um curso ou aluno com matrícula cancelada também não pode ser excluído.
