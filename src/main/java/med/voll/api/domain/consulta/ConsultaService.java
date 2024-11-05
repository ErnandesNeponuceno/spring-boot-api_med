package med.voll.api.domain.consulta;

import lombok.Builder;
import med.voll.api.domain.consulta.validacoes.ValidarAgendamento;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.exceptions.ValidacaoException;
import med.voll.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Builder
@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private ValidarAgendamento validar;

    public Consulta agendarConsulta(DadosConsulta dados) {

        Paciente paciente = pacienteRepository.findById(dados.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        if (dados.medicoId() != null && !medicoRepository.existsById(dados.medicoId())) {
            throw new ValidacaoException("Id do médico informado não existe!");
        }

        Medico medico = escolherMedicoAleatorio(dados);
        if (medico == null) {
            throw new ValidacaoException("Não existe médico disponível nessa data!");
        }

        validar.validarRegrasDeNegocio(dados);

        Consulta consulta = new Consulta(dados);
            consulta.setMedicoId(medico.getId());
            consulta.setPacienteId(paciente.getId());
            consulta.setDataHora(dados.dataHora());
        return consultaRepository.save(consulta);

    }

    private Medico escolherMedicoAleatorio(DadosConsulta dados) {
        if (dados.medicoId() != null) {
            return medicoRepository.getReferenceById(dados.medicoId());
        }

        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória quando médico não for escolhido!");
        }
        return medicoRepository.findMedicosDisponiveis(dados.especialidade(), dados.dataHora());

    }
}
