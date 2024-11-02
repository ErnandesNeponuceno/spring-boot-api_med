package med.voll.api.domain.consulta;

import lombok.Builder;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Builder
@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    // Regras de Negócio para Agendamento de Consultas
    public Consulta agendarConsulta(DadosConsulta dados) {
        validarRegrasDeNegocio(dados);

        Paciente paciente = pacienteRepository.findById(dados.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Medico medico = (dados.medicoId() != null)
                ? medicoRepository.findById(dados.medicoId()).orElseThrow(() -> new RuntimeException("Médico não encontrado"))
                : escolherMedicoAleatorio(dados.dataHora());

        Consulta consulta = new Consulta(dados);
            consulta.setMedicoId(medico.getId());
            consulta.setPacienteId(paciente.getId());
            consulta.setDataHora(dados.dataHora());
        return consultaRepository.save(consulta);
    }

    private void validarRegrasDeNegocio(DadosConsulta dados) {
        LocalDateTime dataHora = dados.dataHora();
        Long medicoId = dados.medicoId();
        Long pacienteId = dados.pacienteId();
        LocalTime abertura = LocalTime.of(7, 0);
        LocalTime fechamento = LocalTime.of(19, 0);

        if (pacienteId == null) {
            throw new IllegalArgumentException("O ID do paciente não pode ser nulo.");
        }

        if (medicoId == null) {
            throw new IllegalArgumentException("O ID do médico não pode ser nulo.");
        }

        if (dataHora == null) {
            throw new IllegalArgumentException("Data e hora não podem ser nulos.");
        }


        // Validação de horário da clínica
        if (dataHora.toLocalTime().isBefore(abertura) || dataHora.toLocalTime().isAfter(fechamento)) {
            throw new IllegalArgumentException("Horário fora do horário de funcionamento da clínica.");
        }

        // Validação de antecedência mínima de 30 minutos
        if (dataHora.isBefore(LocalDateTime.now().plusMinutes(30))) {
            throw new IllegalArgumentException("A consulta deve ser agendada com pelo menos 30 minutos de antecedência.");
        }

        // Validação de paciente ativo
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));
        if (!paciente.getStatus()) {
            throw new IllegalArgumentException("Paciente inativo.");
        }

        // Validação de médico ativo (caso médico seja especificado)

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado."));
        if (!medico.getStatus()) {
            throw new IllegalArgumentException("Médico inativo.");
        }


        // Validação de consulta duplicada para o paciente no mesmo dia
        if (consultaRepository.existsByPacienteIdAndDataHoraBetween(pacienteId, dataHora.toLocalDate().atStartOfDay(), dataHora.toLocalDate().atTime(23, 59))) {
            throw new IllegalArgumentException("O paciente já possui uma consulta agendada para este dia.");
        }

        // Validação de conflito de horário para o médico
        if(consultaRepository.existsByMedicoIdAndDataHora(medicoId, dataHora)){
            throw new IllegalArgumentException("O médico já possui uma consulta agendada neste horário.");
        }
    }

    private Medico escolherMedicoAleatorio(LocalDateTime dataHora) {
        List<Medico> medicosDisponiveis = medicoRepository.findMedicosDisponiveis(dataHora);
        if (medicosDisponiveis.isEmpty()) {
            throw new IllegalArgumentException("Não há médicos disponíveis para este horário.");
        }
        return medicosDisponiveis.get(new Random().nextInt(medicosDisponiveis.size()));
    }
}
