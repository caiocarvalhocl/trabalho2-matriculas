package utfpr.matriculas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.matriculas.dtos.request.AlunoRequest;
import utfpr.matriculas.exceptions.ConflitoException;
import utfpr.matriculas.exceptions.RecursoNaoEncontradoException;
import utfpr.matriculas.models.Aluno;
import utfpr.matriculas.repositories.AlunoRepository;
import utfpr.matriculas.repositories.MatriculaRepository;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;

    public AlunoService(AlunoRepository alunoRepository, MatriculaRepository matriculaRepository) {
        this.alunoRepository = alunoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    public Aluno criar(AlunoRequest request) {
        return alunoRepository.save(request.toEntity());
    }

    @Transactional(readOnly = true)
    public Page<Aluno> listar(Pageable pageable) {
        return alunoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Aluno buscar(Long id) {
        return alunoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Aluno", id));
    }

    @Transactional
    public Aluno atualizar(Long id, AlunoRequest request) {
        Aluno aluno = buscar(id);
        request.aplicarEm(aluno);
        return alunoRepository.save(aluno);
    }

    @Transactional
    public void remover(Long id) {
        Aluno aluno = buscar(id);
        if (matriculaRepository.existsByAlunoId(id)) {
            throw new ConflitoException("Não é possível remover um aluno que possui matrículas");
        }
        alunoRepository.delete(aluno);
    }
}
