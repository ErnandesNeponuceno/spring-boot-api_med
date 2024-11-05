package med.voll.api.repository;

import med.voll.api.domain.medico.Especialidade;
import med.voll.api.domain.medico.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Page<Medico> findAllByStatusTrue(Pageable paginacao);

    @Query("""
            select m from Medico m
            where
            m.status = true
            and
            m.especialidade = :especialidade
            and
            m.id not in(
                select c.medicoId from Consulta c
                where
                c.dataHora = :data
            )
            order by rand()
            limit 1
        """)
    Medico findMedicosDisponiveis(Especialidade especialidade, LocalDateTime data);
}
