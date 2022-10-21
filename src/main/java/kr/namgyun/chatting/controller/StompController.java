package kr.namgyun.chatting.controller;


import kr.namgyun.chatting.dto.ChatMessage;
import kr.namgyun.chatting.dto.ChatRoom;
import kr.namgyun.chatting.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@Slf4j
public class StompController {
    private SimpMessagingTemplate simpMessagingTemplate;
    private ChatService chatService;

    public StompController(SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
    }

    @RequestMapping("/")
    public ModelAndView index(ModelAndView mv){
        mv.setViewName("chat");
        return mv;
    }


    /**
     * a구독중인사람들에게 메시지 전달
     * @param msg
     */
    @MessageMapping("/user")
    public void showUsers(String msg){

      log.info("메시지 :"+msg);
        Map<String , Object> dataMap = new HashMap<>();
        dataMap.put("name",msg);
        simpMessagingTemplate.convertAndSend("/topic/a",dataMap);

    }

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }
    // 모든 채팅방 목록 반환
    @RequestMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {

        List<ChatRoom> chatRoomList =chatService.findAllRoom();
        log.info("모든 채팅방 목록 :"+chatRoomList);
        return chatRoomList;
    }
    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(ChatMessage chatMessage) {
        log.info("채팅룸 생성"+chatMessage);
        ChatRoom room = chatService.createRoom(chatMessage);
        log.info("생성된 채팅룸 :"+room);
        return room;
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }
    // 특정 채팅방 조회
    @RequestMapping("/roomInfo")
    @ResponseBody
    public ChatRoom roomInfo(ChatMessage chatMessage) {
        log.info("들어온 채팅메시지:"+chatMessage);
        ChatRoom chatRoom =chatService.findById(chatMessage.getRoomId());
        log.info("채팅룸 :"+chatRoom);
        return chatRoom;
    }
}
