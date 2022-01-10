package at.operuit.restapi.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BridgeSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/push")
    public void send(BridgedMessage message) throws Exception {
        simpMessagingTemplate.convertAndSendToUser(message.getTarget(), "/queue/messages", message);
    }

    @Getter
    @AllArgsConstructor
    public static class BridgedMessage {
        private String sender;
        private String content;
        private String target;
    }

}
