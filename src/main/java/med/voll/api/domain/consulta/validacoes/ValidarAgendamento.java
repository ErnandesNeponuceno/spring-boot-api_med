package med.voll.api.domain.consulta.validacoes;

import med.voll.api.domain.consulta.DadosConsulta;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.repository.ConsultaRepository;
import med.voll.api.repository.MedicoRepository;
import med.voll.api.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ValidarAgendamento {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public void validarRegrasDeNegocio(DadosConsulta dados) {

        LocalDateTime dataHora = dados.dataHora();
        Long medicoId = dados.medicoId();
        Long pacienteId = dados.pacienteId();
        LocalTime abertura = LocalTime.of(7, 0);
        LocalTime fechamento = LocalTime.of(19, 0);
        var domingo = dataHora.getDayOfWeek().equals(DayOfWeek.SUNDAY);

        if (pacienteId == null) {
            throw new IllegalArgumentException("O ID do paciente não pode ser nulo.");
        }

        if (dataHora == null) {
            throw new IllegalArgumentException("É preciso informar data e hora.");
        }

        // Validação de horário da clínica
        if (dataHora.toLocalTime().isBefore(abertura) || dataHora.toLocalTime().isAfter(fechamento) || domingo) {
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

}
