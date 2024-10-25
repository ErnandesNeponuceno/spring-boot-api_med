package med.voll.api.medico;

import jakarta.validation.constraints.NotNull;
import med.voll.api.endereco.DadosEndereco;
import med.voll.api.endereco.Endereco;

public record DadoAtualizacaoMedico(
        @NotNull Long id,
        String nome,
        String telefone,
        DadosEndereco endereco)
{
}

