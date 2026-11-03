package utfpr.matriculas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utfpr.matriculas.models.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
