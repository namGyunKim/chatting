package kr.namgyun.chatting.dto;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ChatRoom {
    private String roomId;
    private String roomName;


    public static ChatRoom create(ChatMessage chatMessage) {
        ChatRoom room = new ChatRoom();
//        room.roomId = UUID.randomUUID().toString();
        room.roomId = chatMessage.getRoomId();
        room.roomName = chatMessage.getRoomName();
        return room;
    }
}
