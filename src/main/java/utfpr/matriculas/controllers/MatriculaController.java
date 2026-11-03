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
import utfpr.matriculas.dtos.request.MatriculaRequest;
import utfpr.matriculas.dtos.request.StatusMatriculaRequest;
import utfpr.matriculas.dtos.response.MatriculaResponse;
import utfpr.matriculas.models.Matricula;
import utfpr.matriculas.services.MatriculaService;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    public ResponseEntity<MatriculaResponse> matricular(@Valid @RequestBody MatriculaRequest request) {
        Matricula matricula = matriculaService.matricular(request);
        return ResponseEntity.created(URI.create("/matriculas/" + matricula.getId()))
                .body(MatriculaResponse.toResponse(matricula));
    }

    @GetMapping
    public ResponseEntity<Page<MatriculaResponse>> listar(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(matriculaService.listar(pageable).map(MatriculaResponse::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(MatriculaResponse.toResponse(matriculaService.buscar(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatriculaResponse> atualizarStatus(
            @PathVariable Long id, @Valid @RequestBody StatusMatriculaRequest request) {
        return ResponseEntity.ok(MatriculaResponse.toResponse(matriculaService.atualizarStatus(id, request.status())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        matriculaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
