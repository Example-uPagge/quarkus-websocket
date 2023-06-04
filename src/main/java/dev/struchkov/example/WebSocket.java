package dev.struchkov.example;

import dev.struchkov.example.converter.ChatMessageDecoder;
import dev.struchkov.example.converter.ChatMessageEncoder;
import dev.struchkov.example.domain.ChatInputMessage;
import dev.struchkov.example.domain.ChatOutputMessage;
import io.vertx.ext.web.handler.HttpException;
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
        encoders = ChatMessageEncoder.class,
        configurator = CustomConfigurator.class
)
@RequiredArgsConstructor
public class WebSocket {

    private final Map<String, List<Session>> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("chatId") String chatId) {
        System.out.println("onOpen> " + chatId);
        final String authCookieValue = (String) session.getUserProperties().get("sessionId");
        final UUID authUserId = getAuthUser(authCookieValue);
        session.getUserProperties().put("userId", authUserId);
        sessions.computeIfAbsent(chatId, key -> new ArrayList<>()).add(session);
    }

    private UUID getAuthUser(String authCookieValue) {
        // your auth logic here
        if (authCookieValue == null) throw new HttpException(401, "Не передан параметр авторизации.");
        if (authCookieValue.equals("user1")) return UUID.fromString("09e429de-a302-40b6-9d10-6b113ab9e89d");
        if (authCookieValue.equals("user2")) return UUID.fromString("f84dbae1-f9a9-4c37-8922-4eb207103676");
        throw new HttpException(403, "Пользователь не авторизован.");
    }

    @OnError
    public void onError(Session session, @PathParam("chatId") String chatId, Throwable throwable) {
        if (throwable instanceof HttpException httpException) {
            final int statusCode = httpException.getStatusCode();
            if (statusCode == 401) {
                session.getAsyncRemote().sendText("Вы не авторизованы.");
                closeSession(session, chatId);
                return;
            }
            if (statusCode == 403) {
                session.getAsyncRemote().sendText("Доступ запрещен.");
                closeSession(session, chatId);
                return;
            }
        }
        System.out.println("onError> " + chatId + ": " + throwable);
    }

    @OnClose
    public void onClose(Session session, @PathParam("chatId") String chatId) {
        System.out.println("onClose> " + chatId);
        closeSession(session, chatId);
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
            final UUID fromUserId = (UUID) session.getUserProperties().get("userId");
            final ChatOutputMessage outputMessage = new ChatOutputMessage(fromUserId, message.getText());
            chatSession.getAsyncRemote().sendObject(outputMessage);
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
