package chat.controller;

import chat.Message;
import chat.UserMessage;
import chat.events.WebSocketEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MessageController {
    public List<UserMessage> messages;
    public MessageController(){
        messages = new ArrayList<>();
    }
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public UserMessage send(UserMessage message) throws Exception {
        messages.add(message);
        return message;
    }
    @Autowired
    WebSocketEvents webSocketEvents;
    @MessageMapping("/chat.newUser")
    @SendTo("/topic/users")
    public UserMessage newUser(@Payload UserMessage webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getName());
        webSocketEvents.users.add(webSocketChatMessage.getName());
        return webSocketChatMessage;
    }
    @PostMapping("/addMessage")
    public void AddMessage(@RequestBody UserMessage message){
        messages.add(message);
    }
    @RequestMapping(value = "/allMessages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<UserMessage> AllMessages(){
        return messages.stream().filter(p->p.getRecipient().equals("")).collect(Collectors.toList());
    }
    @RequestMapping(value = "/allMessages/{recipient1}/{recipient2}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<UserMessage> AllMessages2(@PathVariable("recipient1") String r1, @PathVariable("recipient2") String r2){
        return messages.stream().filter(p->(p.getRecipient().equals(r1)&&p.getName().equals(r2))||(p.getRecipient().equals(r2))&&p.getName().equals(r1)).collect(Collectors.toList());
    }
    @RequestMapping(value = "/allUsers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<String> AllUsers(){
        return webSocketEvents.users;
    }
}
