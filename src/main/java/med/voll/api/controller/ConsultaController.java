package med.voll.api.controller;

import jakarta.transaction.Transactional;
import med.voll.api.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import med.voll.api.consulta.*;
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

    @PostMapping("/agendar")
    @Transactional
    public void cadastrar(@RequestBody DadosConsulta dados) {
        Consulta consulta = consultaService.agendarConsulta(dados);
    }
}
