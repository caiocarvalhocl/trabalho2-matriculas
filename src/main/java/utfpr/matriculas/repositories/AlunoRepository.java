package utfpr.matriculas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utfpr.matriculas.models.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}
