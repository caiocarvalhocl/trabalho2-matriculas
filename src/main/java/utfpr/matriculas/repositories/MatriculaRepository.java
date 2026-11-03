package utfpr.matriculas.repositories;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import utfpr.matriculas.models.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    @Override
    @EntityGraph(attributePaths = {"aluno", "curso"})
    Page<Matricula> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"aluno", "curso"})
    Optional<Matricula> findById(Long id);

    boolean existsByCursoId(Long cursoId);

    boolean existsByAlunoId(Long alunoId);
}
