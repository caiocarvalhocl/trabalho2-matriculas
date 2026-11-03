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
import utfpr.matriculas.dtos.request.CursoRequest;
import utfpr.matriculas.dtos.response.CursoResponse;
import utfpr.matriculas.models.Curso;
import utfpr.matriculas.services.CursoService;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    public ResponseEntity<CursoResponse> criar(@Valid @RequestBody CursoRequest request) {
        Curso curso = cursoService.criar(request);
        return ResponseEntity.created(URI.create("/cursos/" + curso.getId())).body(CursoResponse.toResponse(curso));
    }

    @GetMapping
    public ResponseEntity<Page<CursoResponse>> listar(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(cursoService.listar(pageable).map(CursoResponse::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(CursoResponse.toResponse(cursoService.buscar(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CursoRequest request) {
        return ResponseEntity.ok(CursoResponse.toResponse(cursoService.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        cursoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
