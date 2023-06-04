package dev.struchkov.example;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

import java.util.List;
import java.util.Map;

public class CustomConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        final Map<String, List<String>> headers = request.getHeaders();
        final List<String> cookies = headers.get("cookie");

        String sessionId = parseCookies(cookies); // ваша реализация парсинга кук
        config.getUserProperties().put("sessionId", sessionId);
    }

    public String parseCookies(List<String> cookies) {
        if (cookies != null) {
            for (String cookie : cookies) {
                String[] singleCookie = cookie.split(";");
                for (String part : singleCookie) {
                    part = part.trim();
                    if (part.startsWith("sessionId")) {
                        return part.substring("sessionId".length() + 1).trim();
                    }
                }
            }
        }
        return null;
    }

}
