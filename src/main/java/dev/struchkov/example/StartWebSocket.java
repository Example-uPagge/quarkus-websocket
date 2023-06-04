package dev.struchkov.example;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint(
        value = "/chat/{chatId}",
        decoders = ChatMessageDecoder.class,
        encoders = ChatMessageEncoder.class
)
@RequiredArgsConstructor
public class StartWebSocket {

    public static final ThreadLocal<UUID> CURRENT_USER = new ThreadLocal<>();
    private final Map<String, List<Session>> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("chatId") String chatId) {
        System.out.println("onOpen> " + chatId);
        sessions.computeIfAbsent(chatId, key -> new ArrayList<>()).add(session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("chatId") String chatId) {
        System.out.println("onClose> " + chatId);
        closeSession(session, chatId);
    }

    @OnError
    public void onError(Session session, @PathParam("chatId") String chatId, Throwable throwable) {
        System.out.println("onError> " + chatId + ": " + throwable);
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("chatId") String chatId, ChatInputMessage message) {
        System.out.println("onMessage> " + chatId + ": " + message);
        sendMessage(session, chatId, message);
    }

    private void sendMessage(Session session, String chatId, ChatInputMessage message) {
        final List<Session> chatSessions = sessions.get(chatId);
        for (Session chatSession : chatSessions) {
            if (session.getId().equals(chatSession.getId())) {
                continue;
            }
            final UUID fromUserId = CURRENT_USER.get();
            final ChatOutputMessage outputMessage = new ChatOutputMessage(fromUserId, message.getText());
            chatSession.getAsyncRemote().sendObject(outputMessage);
            CURRENT_USER.remove();
        }
    }

    private void closeSession(Session session, String chatId) {
        final List<Session> chatSessions = sessions.get(chatId);
        final Iterator<Session> sessionIterator = chatSessions.iterator();
        while (sessionIterator.hasNext()) {
            final Session chatSession = sessionIterator.next();
            if (session.getId().equals(chatSession.getId())) {
                sessionIterator.remove();
                break;
            }
        }
    }

}
