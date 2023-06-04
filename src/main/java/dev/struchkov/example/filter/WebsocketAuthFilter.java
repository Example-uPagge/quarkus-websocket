package dev.struchkov.example.filter;

import dev.struchkov.example.WebSocket;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class WebsocketAuthFilter {

    @RouteFilter(100)
    void authFilter(RoutingContext rc) {
        final HttpServerRequest currentRequest = rc.request();

        if (isWebsocketRequest(currentRequest)) {
            final Cookie authCookie = currentRequest.getCookie("sessionId");
            if (authCookie == null) {
                throw new HttpException(401, "Не передан параметр авторизации.");
            }

            final String authValue = authCookie.getValue();
            if (!authLogic(authValue)) {
                throw new HttpException(403, "Пользователь не авторизован.");
            }
        }

        rc.next();
    }

    private static boolean isWebsocketRequest(HttpServerRequest currentRequest) {
        return currentRequest.headers().contains("Upgrade")
               && "websocket".equals(currentRequest.getHeader("Upgrade"));
    }

    private boolean authLogic(String sessionId) {
        // your auth logic here
        if (sessionId.equals("user1")) {
            WebSocket.CURRENT_USER.set(UUID.fromString("09e429de-a302-40b6-9d10-6b113ab9e89d"));
            return true;
        } else if (sessionId.equals("user2")) {
            WebSocket.CURRENT_USER.set(UUID.fromString("f84dbae1-f9a9-4c37-8922-4eb207103676"));
            return true;
        } else {
            return false;
        }
    }

}
