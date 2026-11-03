package utfpr.matriculas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.matriculas.dtos.request.MatriculaRequest;
import utfpr.matriculas.exceptions.RecursoNaoEncontradoException;
import utfpr.matriculas.exceptions.RegraNegocioException;
import utfpr.matriculas.models.Aluno;
import utfpr.matriculas.models.Curso;
import utfpr.matriculas.models.Matricula;
import utfpr.matriculas.repositories.AlunoRepository;
import utfpr.matriculas.repositories.CursoRepository;
import utfpr.matriculas.repositories.MatriculaRepository;

@Service
public class MatriculaService {

    private static final int IDADE_MINIMA = 16;

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            AlunoRepository alunoRepository,
            CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public Matricula matricular(MatriculaRequest request) {
        Aluno aluno = alunoRepository
                .findById(request.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno", request.alunoId()));
        Curso curso = cursoRepository
                .findById(request.cursoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso", request.cursoId()));

        if (!Boolean.TRUE.equals(curso.getAtivo())) {
            throw new RegraNegocioException("Não é possível matricular em um curso inativo");
        }
        if (aluno.getIdade() < IDADE_MINIMA) {
            throw new RegraNegocioException("Não é possível matricular um aluno menor de 16 anos");
        }

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setCurso(curso);
        matricula.setDataMatricula(request.dataMatricula());
        matricula.setStatus(request.status() == null ? Matricula.ATIVA : request.status());
        return matriculaRepository.save(matricula);
    }

    @Transactional(readOnly = true)
    public Page<Matricula> listar(Pageable pageable) {
        return matriculaRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Matricula buscar(Long id) {
        return matriculaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Matrícula", id));
    }

    @Transactional
    public Matricula atualizarStatus(Long id, String status) {
        Matricula matricula = buscar(id);
        matricula.setStatus(status);
        return matriculaRepository.save(matricula);
    }

    @Transactional
    public void cancelar(Long id) {
        atualizarStatus(id, Matricula.CANCELADA);
    }
}
