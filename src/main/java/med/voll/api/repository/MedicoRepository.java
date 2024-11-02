package med.voll.api.repository;

import med.voll.api.domain.medico.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Page<Medico> findAllByStatusTrue(Pageable paginacao);

    @Query(value = """
    SELECT m FROM medico m
    WHERE m.status = true
    AND m.id NOT IN (SELECT c.medico.id FROM consulta c WHERE c.dataHora = :dataHora)""", nativeQuery = true)
    List<Medico> findMedicosDisponiveis(LocalDateTime dataHora);
}
