package utfpr.matriculas.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StatusMatriculaRequest(
        @NotBlank(message = "O status é obrigatório")
        @Pattern(regexp = "ATIVA|CONCLUIDA|TRANCADA|CANCELADA",
                message = "Status inválido. Use ATIVA, CONCLUIDA, TRANCADA ou CANCELADA")
        String status) {
}
