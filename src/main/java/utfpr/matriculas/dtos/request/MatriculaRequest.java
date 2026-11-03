package utfpr.matriculas.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record MatriculaRequest(
        @NotNull(message = "O aluno é obrigatório")
        Long alunoId,
        @NotNull(message = "O curso é obrigatório")
        Long cursoId,
        @NotNull(message = "A data da matrícula é obrigatória")
        LocalDate dataMatricula,
        @Pattern(regexp = "ATIVA|CONCLUIDA|TRANCADA|CANCELADA",
                message = "Status inválido. Use ATIVA, CONCLUIDA, TRANCADA ou CANCELADA")
        String status) {
}
