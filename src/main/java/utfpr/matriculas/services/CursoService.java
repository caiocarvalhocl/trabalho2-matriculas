package utfpr.matriculas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.matriculas.dtos.request.CursoRequest;
import utfpr.matriculas.exceptions.ConflitoException;
import utfpr.matriculas.exceptions.RecursoNaoEncontradoException;
import utfpr.matriculas.models.Curso;
import utfpr.matriculas.repositories.CursoRepository;
import utfpr.matriculas.repositories.MatriculaRepository;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;

    public CursoService(CursoRepository cursoRepository, MatriculaRepository matriculaRepository) {
        this.cursoRepository = cursoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    public Curso criar(CursoRequest request) {
        return cursoRepository.save(request.toEntity());
    }

    @Transactional(readOnly = true)
    public Page<Curso> listar(Pageable pageable) {
        return cursoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Curso buscar(Long id) {
        return cursoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Curso", id));
    }

    @Transactional
    public Curso atualizar(Long id, CursoRequest request) {
        Curso curso = buscar(id);
        request.aplicarEm(curso);
        return cursoRepository.save(curso);
    }

    @Transactional
    public void remover(Long id) {
        Curso curso = buscar(id);
        if (matriculaRepository.existsByCursoId(id)) {
            throw new ConflitoException("Não é possível remover um curso que possui matrículas");
        }
        cursoRepository.delete(curso);
    }
}
