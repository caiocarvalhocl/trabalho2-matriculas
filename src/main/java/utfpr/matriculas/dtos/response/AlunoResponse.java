package utfpr.matriculas.dtos.response;

import utfpr.matriculas.models.Aluno;

public record AlunoResponse(Long id, String nome, String email, Integer idade, String telefone) {

    public static AlunoResponse toResponse(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getIdade(), aluno.getTelefone());
    }
}
