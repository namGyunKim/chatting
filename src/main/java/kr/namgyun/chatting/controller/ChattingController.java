package kr.namgyun.chatting.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@Slf4j
public class ChattingController {

    @RequestMapping("/myChat")
    public ModelAndView myChat(ModelAndView mv){
        log.info("들어옴");
        mv.setViewName("chatting");
        return mv;
    }
    /*@RequestMapping("/chatt")
    public void aaa(){
        log.info("챗");
    }*/

    @GetMapping("/user")
    public String user(){
        log.info("들어옴");
        return "user";
    }

}
