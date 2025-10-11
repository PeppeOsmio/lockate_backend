package com.peppeosmio.lockate.anonymous_group.websocket;

import com.peppeosmio.lockate.anonymous_group.security.AGMemberAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class AGSendLocationHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            // /api/ws/anonymous-groups/{anonymousGroupId}/send-location
            try {
                var path = request.getURI().getPath();
                var segments = path.split("/");
                var anonymousGroupId = UUID.fromString(segments[4]);
                attributes.put("authentication", auth);
                attributes.put("anonymousGroupId", anonymousGroupId);
            } catch (Exception e) {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return false;
            }
        } else {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {}
}
