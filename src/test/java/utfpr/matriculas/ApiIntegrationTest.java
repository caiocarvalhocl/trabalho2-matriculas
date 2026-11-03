package utfpr.matriculas;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private ResultActions enviar(String metodo, String url, Object corpo) throws Exception {
        var req = switch (metodo) {
            case "POST" -> post(url);
            case "PUT" -> put(url);
            default -> throw new IllegalArgumentException(metodo);
        };
        return mvc.perform(req.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(corpo)));
    }

    private long criarCurso(String nome, boolean ativo) throws Exception {
        String json = enviar("POST", "/cursos", Map.of("nome", nome, "cargaHoraria", 40, "categoria", "TI", "ativo", ativo))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return mapper.readTree(json).get("id").asLong();
    }

    private long criarAluno(String nome, int idade) throws Exception {
        String json = enviar("POST", "/alunos", Map.of("nome", nome, "email", nome + "@email.com", "idade", idade))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return mapper.readTree(json).get("id").asLong();
    }

    private ResultActions matricular(long alunoId, long cursoId) throws Exception {
        return enviar("POST", "/matriculas",
                Map.of("alunoId", alunoId, "cursoId", cursoId, "dataMatricula", "2026-03-01"));
    }

    @Test
    void crudDeCurso() throws Exception {
        long id = criarCurso("Java Basico", true);

        mvc.perform(get("/cursos/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.nome", is("Java Basico")));

        enviar("PUT", "/cursos/" + id,
                Map.of("nome", "Java Avancado", "cargaHoraria", 80, "categoria", "TI", "ativo", false))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Java Avancado")))
                .andExpect(jsonPath("$.ativo", is(false)));

        mvc.perform(delete("/cursos/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/cursos/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void crudDeAluno() throws Exception {
        long id = criarAluno("Maria", 20);

        enviar("PUT", "/alunos/" + id,
                Map.of("nome", "Maria Silva", "email", "maria@email.com", "idade", 21, "telefone", "41999999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefone", is("41999999999")));

        mvc.perform(delete("/alunos/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/alunos/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void validacaoDeCursoRetornaMensagensPorCampo() throws Exception {
        enviar("POST", "/cursos", Map.of("nome", "abc", "cargaHoraria", 0, "categoria", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome", is("O nome deve ter no mínimo 5 caracteres")))
                .andExpect(jsonPath("$.campos.cargaHoraria", is("A carga horária deve ser maior que 0")))
                .andExpect(jsonPath("$.campos.categoria", is("A categoria é obrigatória")))
                .andExpect(jsonPath("$.campos.ativo", is("O campo ativo é obrigatório")));
    }

    @Test
    void validacaoDeAluno() throws Exception {
        enviar("POST", "/alunos", Map.of("nome", "Joao", "email", "invalido", "idade", 15))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.email", is("O e-mail é inválido")))
                .andExpect(jsonPath("$.campos.idade", is("A idade mínima é 16 anos")));
    }

    @Test
    void idInexistenteRetorna404() throws Exception {
        mvc.perform(get("/cursos/999")).andExpect(status().isNotFound());
        mvc.perform(get("/alunos/999")).andExpect(status().isNotFound());
        mvc.perform(get("/matriculas/999")).andExpect(status().isNotFound());
        mvc.perform(delete("/matriculas/999")).andExpect(status().isNotFound());
    }

    @Test
    void paginacaoEOrdenacao() throws Exception {
        for (String nome : new String[] {"Curso C", "Curso A", "Curso B"}) {
            criarCurso(nome, true);
        }

        mvc.perform(get("/cursos?page=0&size=2&sort=nome,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nome", is("Curso A")))
                .andExpect(jsonPath("$.content[1].nome", is("Curso B")))
                .andExpect(jsonPath("$.page.totalElements", is(3)))
                .andExpect(jsonPath("$.page.totalPages", is(2)));

        mvc.perform(get("/cursos?page=1&size=2&sort=nome,desc"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("Curso A")));
    }

    @Test
    void ordenacaoPorCampoInexistenteRetorna400() throws Exception {
        mvc.perform(get("/cursos?sort=naoExiste,asc")).andExpect(status().isBadRequest());
    }

    @Test
    void matriculaCompletaEMudancaDeStatus() throws Exception {
        long aluno = criarAluno("Ana", 22);
        long curso = criarCurso("Spring Boot", true);

        String json = matricular(aluno, curso)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("ATIVA")))
                .andExpect(jsonPath("$.aluno.nome", is("Ana")))
                .andExpect(jsonPath("$.curso.nome", is("Spring Boot")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long id = mapper.readTree(json).get("id").asLong();

        mvc.perform(get("/matriculas")).andExpect(jsonPath("$.content", hasSize(1)));

        enviar("PUT", "/matriculas/" + id, Map.of("status", "CONCLUIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONCLUIDA")));

        enviar("PUT", "/matriculas/" + id, Map.of("status", "QUALQUER"))
                .andExpect(status().isBadRequest());

        mvc.perform(delete("/matriculas/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/matriculas/" + id)).andExpect(jsonPath("$.status", is("CANCELADA")));
    }

    @Test
    void naoMatriculaEmCursoInativo() throws Exception {
        long aluno = criarAluno("Ana", 22);
        long curso = criarCurso("Curso antigo", false);

        matricular(aluno, curso)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", is("Não é possível matricular em um curso inativo")));
    }

    @Test
    void matriculaComAlunoOuCursoInexistenteRetorna404() throws Exception {
        long aluno = criarAluno("Ana", 22);
        long curso = criarCurso("Spring Boot", true);

        matricular(999, curso).andExpect(status().isNotFound());
        matricular(aluno, 999).andExpect(status().isNotFound());
    }

    @Test
    void naoRemoveCursoOuAlunoComMatriculas() throws Exception {
        long aluno = criarAluno("Ana", 22);
        long curso = criarCurso("Spring Boot", true);
        matricular(aluno, curso).andExpect(status().isCreated());

        mvc.perform(delete("/cursos/" + curso))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Não é possível remover um curso que possui matrículas")));
        mvc.perform(delete("/alunos/" + aluno))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Não é possível remover um aluno que possui matrículas")));
    }
}
