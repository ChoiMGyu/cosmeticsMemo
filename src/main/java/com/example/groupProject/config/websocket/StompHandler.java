package com.example.groupProject.config.websocket;

import com.example.groupProject.config.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    /**
     * websocket을 통해 들어온 요청이 처리 되기전 실행된다.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            final String authorization = jwtUtil.extractJwt(accessor);

            jwtUtil.validateToken(authorization);

            if (accessor.getSessionAttributes() == null) {
                accessor.setSessionAttributes(new HashMap<>());
            }

            accessor.getSessionAttributes().put("accessToken", authorization);
        }

        return message;
    }
}
