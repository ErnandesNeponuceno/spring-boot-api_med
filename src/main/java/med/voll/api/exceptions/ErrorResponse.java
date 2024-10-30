package med.voll.api.exceptions;

import org.springframework.validation.FieldError;

public record ErrorResponse(String campo, String mensagem) {
    public ErrorResponse(FieldError erro) {
        this(erro.getField(), erro.getDefaultMessage());
    }
}
