package utfpr.matriculas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utfpr.matriculas.dtos.request.MatriculaRequest;
import utfpr.matriculas.exceptions.RegraNegocioException;
import utfpr.matriculas.models.Aluno;
import utfpr.matriculas.models.Curso;
import utfpr.matriculas.repositories.AlunoRepository;
import utfpr.matriculas.repositories.CursoRepository;
import utfpr.matriculas.repositories.MatriculaRepository;
import utfpr.matriculas.services.MatriculaService;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    MatriculaRepository matriculaRepository;

    @Mock
    AlunoRepository alunoRepository;

    @Mock
    CursoRepository cursoRepository;

    @InjectMocks
    MatriculaService service;

    private final MatriculaRequest request = new MatriculaRequest(1L, 2L, LocalDate.of(2026, 3, 1), null);

    private Aluno aluno(int idade) {
        Aluno aluno = new Aluno();
        aluno.setIdade(idade);
        return aluno;
    }

    private Curso curso(boolean ativo) {
        Curso curso = new Curso();
        curso.setAtivo(ativo);
        return curso;
    }

    @Test
    void rejeitaAlunoMenorDe16Anos() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno(15)));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso(true)));

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.matricular(request));

        assertEquals("Não é possível matricular um aluno menor de 16 anos", ex.getMessage());
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void rejeitaCursoInativo() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno(20)));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso(false)));

        assertThrows(RegraNegocioException.class, () -> service.matricular(request));
        verify(matriculaRepository, never()).save(any());
    }
}
