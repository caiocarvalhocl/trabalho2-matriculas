package utfpr.matriculas.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import utfpr.matriculas.models.Curso;

public record CursoRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 5, message = "O nome deve ter no mínimo 5 caracteres")
        String nome,
        @NotNull(message = "A carga horária é obrigatória")
        @Positive(message = "A carga horária deve ser maior que 0")
        Integer cargaHoraria,
        @NotBlank(message = "A categoria é obrigatória")
        String categoria,
        @NotNull(message = "O campo ativo é obrigatório")
        Boolean ativo) {

    public Curso toEntity() {
        Curso curso = new Curso();
        aplicarEm(curso);
        return curso;
    }

    public void aplicarEm(Curso curso) {
        curso.setNome(nome);
        curso.setCargaHoraria(cargaHoraria);
        curso.setCategoria(categoria);
        curso.setAtivo(ativo);
    }
}
