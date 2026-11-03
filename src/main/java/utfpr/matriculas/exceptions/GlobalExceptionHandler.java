package utfpr.matriculas.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(erro -> campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        Map<String, Object> corpo = corpo(HttpStatus.BAD_REQUEST, "Dados inválidos");
        corpo.put("campos", campos);
        return ResponseEntity.badRequest().body(corpo);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleRegraNegocio(RegraNegocioException ex) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler({ConflitoException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, Object>> handleConflito(RuntimeException ex) {
        String mensagem = ex instanceof ConflitoException ? ex.getMessage() : "Operação viola a integridade dos dados";
        return resposta(HttpStatus.CONFLICT, mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleCorpoInvalido(HttpMessageNotReadableException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição ausente ou malformado");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + ex.getName() + "'");
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Map<String, Object>> handleOrdenacaoInvalida(PropertyReferenceException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Campo de ordenação inválido: " + ex.getPropertyName());
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(corpo(status, mensagem));
    }

    private Map<String, Object> corpo(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", status.value());
        corpo.put("error", status.getReasonPhrase());
        corpo.put("message", mensagem);
        return corpo;
    }
}
