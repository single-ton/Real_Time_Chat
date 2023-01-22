package chat.events;

import chat.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebSocketEvents {
    public WebSocketEvents(){
        users = new ArrayList<>();
    }
    public List<String> users;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @EventListener
    public void connect(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        ;
    }

    @EventListener
    public void disconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {

            UserMessage chatMessage = new UserMessage();
            chatMessage.setContent("Leave");
            chatMessage.setName(username);

            messagingTemplate.convertAndSend("/topic/users", chatMessage);
        }
    }
}
