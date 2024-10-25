package med.voll.api.repository;

import med.voll.api.paciente.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {


}
