package utfpr.matriculas.dtos.response;

import java.time.LocalDate;
import utfpr.matriculas.models.Matricula;

public record MatriculaResponse(
        Long id, LocalDate dataMatricula, String status, AlunoResponse aluno, CursoResponse curso) {

    public static MatriculaResponse toResponse(Matricula matricula) {
        return new MatriculaResponse(
                matricula.getId(),
                matricula.getDataMatricula(),
                matricula.getStatus(),
                AlunoResponse.toResponse(matricula.getAluno()),
                CursoResponse.toResponse(matricula.getCurso()));
    }
}
