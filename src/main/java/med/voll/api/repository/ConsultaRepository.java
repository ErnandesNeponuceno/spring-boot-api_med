package med.voll.api.repository;

import med.voll.api.consulta.Consulta;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDateTime;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    boolean existsByPacienteIdAndDataHoraBetween(Long pacienteId, LocalDateTime inicio, LocalDateTime fim);
    boolean existsByMedicoIdAndDataHora(Long medicoId, LocalDateTime dataHora);
}
