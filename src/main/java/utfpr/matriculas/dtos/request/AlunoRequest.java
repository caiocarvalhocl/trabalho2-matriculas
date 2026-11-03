package utfpr.matriculas.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import utfpr.matriculas.models.Aluno;

public record AlunoRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail é inválido")
        String email,
        @NotNull(message = "A idade é obrigatória")
        @Min(value = 16, message = "A idade mínima é 16 anos")
        Integer idade,
        String telefone) {

    public Aluno toEntity() {
        Aluno aluno = new Aluno();
        aplicarEm(aluno);
        return aluno;
    }

    public void aplicarEm(Aluno aluno) {
        aluno.setNome(nome);
        aluno.setEmail(email);
        aluno.setIdade(idade);
        aluno.setTelefone(telefone);
    }
}
