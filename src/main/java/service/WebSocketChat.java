package service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@ServerEndpoint(value = "/chattt")
public class WebSocketChat {
    /**
     * 새로운 클라이언트가 접속할 때마다 클라이언트의 세션 관련 정보를 정적형으로 저장하여 1:N의 통신이 가능
     */
    private static Set<Session> clients =
            Collections.synchronizedSet(new HashSet<Session>());
    /**
     * 메시지가 수신되었을 때
     */
    @OnMessage
    public void onMessage(String msg,Session session) throws Exception{
        log.info("메시지가 들어옴 :"+msg);
        for(Session s : clients) {
            log.info("보내는 메시지 : " + msg);
            s.getBasicRemote().sendText(msg);

        }
    }
    /**
     * 클라이언트가 접속할 때 발생하는 이벤트
     */
    @OnOpen
    public void onOpen(Session session){
        log.info("클라이언트 접속 :"+session.toString());
        if(!clients.contains(session)) {
            clients.add(session);
            System.out.println("클라이언트 연결 : " + session);
        }else {
            System.out.println("이미 연결된 클라이언트");
        }
    }
    /**
     * 클라이언트가 접속 종료할 때 발생하는 이벤트
     */
    @OnClose
    public void onClose(Session session){
        log.info("클라이언트 접속 종료 :"+session);
        clients.remove(session);

    }
}
