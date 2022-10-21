package kr.namgyun.chatting.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    public enum MessageType {
        ENTER, TALK
    }

    private MessageType type;
    //채팅방 ID
    private String roomId;
    private String roomName;
    //보내는 사람
    private String sender;
    //내용
    private String message;
}
