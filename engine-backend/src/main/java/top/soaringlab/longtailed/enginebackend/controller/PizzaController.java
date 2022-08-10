package top.soaringlab.longtailed.enginebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.soaringlab.longtailed.enginebackend.dto.Message;
import top.soaringlab.longtailed.enginebackend.service.PizzaService;

@RestController
@RequestMapping(value = "/api/pizza")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;

    @GetMapping(value = "/query-epidemic")
    public Message queryEpidemic(@RequestBody Message message) {
        return new Message(String.valueOf(pizzaService.queryEpidemic(message.getMessage())));
    }

    @GetMapping(value = "/make-free-pizza")
    public Message makeFreePizza() {
        pizzaService.makeFreePizza();
        return new Message("Success");
    }
}
