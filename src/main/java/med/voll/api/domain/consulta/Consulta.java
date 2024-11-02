package med.voll.api.domain.consulta;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "consulta")
@Entity(name = "Consulta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id")
    private Long pacienteId;

    @Column(name = "medico_id")
    private Long medicoId;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    public Consulta(DadosConsulta dados) {
        this.pacienteId = dados.pacienteId();
        this.medicoId = dados.medicoId();
        this.dataHora = dados.dataHora();
    }


}
