package kr.namgyun.chatting.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@Slf4j
public class StompController {
    private SimpMessagingTemplate simpMessagingTemplate;

    public StompController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @RequestMapping("/")
    public ModelAndView index(ModelAndView mv){
        mv.setViewName("chat");
        return mv;
    }

    @MessageMapping("/user")
    public void showUsers(String msg){
      log.info("메시지 :"+msg);
        Map<String , Object> dataMap = new HashMap<>();
        dataMap.put("name",msg);
        simpMessagingTemplate.convertAndSend("/topic/a",dataMap);

    }
}
