package med.voll.api.controller;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/test")
public class HelloController {

    @GetMapping
    public String ola(){
        return "E aiiiii mundo!";
    }
}
