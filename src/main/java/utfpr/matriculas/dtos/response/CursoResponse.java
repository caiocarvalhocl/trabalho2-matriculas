package utfpr.matriculas.dtos.response;

import utfpr.matriculas.models.Curso;

public record CursoResponse(Long id, String nome, Integer cargaHoraria, String categoria, Boolean ativo) {

    public static CursoResponse toResponse(Curso curso) {
        return new CursoResponse(
                curso.getId(), curso.getNome(), curso.getCargaHoraria(), curso.getCategoria(), curso.getAtivo());
    }
}
