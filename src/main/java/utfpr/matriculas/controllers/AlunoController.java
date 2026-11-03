package utfpr.matriculas.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.matriculas.dtos.request.AlunoRequest;
import utfpr.matriculas.dtos.response.AlunoResponse;
import utfpr.matriculas.models.Aluno;
import utfpr.matriculas.services.AlunoService;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody AlunoRequest request) {
        Aluno aluno = alunoService.criar(request);
        return ResponseEntity.created(URI.create("/alunos/" + aluno.getId())).body(AlunoResponse.toResponse(aluno));
    }

    @GetMapping
    public ResponseEntity<Page<AlunoResponse>> listar(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(alunoService.listar(pageable).map(AlunoResponse::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(AlunoResponse.toResponse(alunoService.buscar(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) {
        return ResponseEntity.ok(AlunoResponse.toResponse(alunoService.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        alunoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
