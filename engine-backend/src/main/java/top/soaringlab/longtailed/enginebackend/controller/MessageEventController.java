package top.soaringlab.longtailed.enginebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.soaringlab.longtailed.enginebackend.domain.MessageEvent;
import top.soaringlab.longtailed.enginebackend.service.MessageEventService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/message-event")
public class MessageEventController {

    @Autowired
    private MessageEventService messageEventService;

    @GetMapping(value = "/find-all")
    public List<MessageEvent> findAll() {
        return messageEventService.findAll();
    }
}
