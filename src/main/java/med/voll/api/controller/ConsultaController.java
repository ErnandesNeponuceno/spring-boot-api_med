package med.voll.api.controller;

import jakarta.transaction.Transactional;
import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.consulta.ConsultaService;
import med.voll.api.domain.consulta.DadosConsulta;
import med.voll.api.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private ConsultaRepository consultaRepository;

    @GetMapping
    public List<Consulta> listar(){
        return consultaRepository.findAll();
    }

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody DadosConsulta dados) {
        Consulta consulta = consultaService.agendarConsulta(dados);
        return ResponseEntity.ok(consulta);
    }
}
