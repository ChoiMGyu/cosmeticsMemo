package com.example.groupProject.controller;

import com.example.groupProject.controller.chat.ChatController;
import com.example.groupProject.dto.chat.ChatMessageDto;
import com.example.groupProject.dto.chat.MessageSubDto;
import com.example.groupProject.service.chat.ChatService;
import com.example.groupProject.service.chat.RedisPublisher;
import com.example.groupProject.service.chat.RedisSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class ChatControllerTest {

    private static final String SOCKET_URL = "ws://127.0.0.1:8080/ws-stomp";
    private static StompSession session;
    private static CountDownLatch latch;

    @BeforeEach
    public void beforeEach() throws ExecutionException, InterruptedException {
        session = setSession();
    }

    private StompSession setSession() throws ExecutionException, InterruptedException {
        //Client 설정
        WebSocketClient transport = new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))
        );
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

//        // Header 설정
//        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
//        StompHeaders stompHeaders = new StompHeaders();
//        stompHeaders.add(HttpHeaders.AUTHORIZATION, getAccessToken());

        // 서버 연결
//        return stompClient.connect(SOCKET_URL, webSocketHttpHeaders, stompHeaders,
//                getTestStompFrameHandler()).get();
        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        return stompClient.connectAsync(SOCKET_URL, sessionHandler).get();
    }

    private class MyStompSessionHandler extends StompSessionHandlerAdapter {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            System.out.println("Received message: " + payload);
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("After Connected");
        }
    }

    @Test
    @DisplayName("WebSocket 연결 테스트")
    public void 서버_연결() throws Exception {
        assertThat(session.isConnected()).isTrue();
    }

    @Test
    @DisplayName("WebSocket 연결 상태 유지 테스트")
    public void 연결_상태_유지() throws Exception {
        //given
        assertThat(session.isConnected()).isTrue();

        //when
        //연결이 계속 유지되는지 확인을 위해 2초간 Thread를 sleep하였다가 connect를 확인
        Thread.sleep(2000);

        //then
        assertThat(session.isConnected()).isTrue();
    }

    @Test
    @DisplayName("유저가 채팅방에 구독하면 메시지를 발행하고 수신할 수 있다")
    public void redis_websocket_publish() throws Exception {
        // RedisPublisher를 통해 메시지를 발행할 수 있도록 설정
        String message = "Hello from WebSocket!";
        String roomId = "1L";
        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .roomId(roomId)
                .message(message)
                .build();
        MessageSubDto messageSubDto = MessageSubDto.builder()
                .chatMessageDto(chatMessageDto)
                .build();

        latch = new CountDownLatch(1);

        session.subscribe("/sub/chat/room" + roomId, new MyStompSessionHandler());

        session.send("/pub/chat/message", chatMessageDto);

        latch.await(2, TimeUnit.SECONDS);

        //RedisSubscriber log가 출력되면 성공하는 테스트 -> 추후 assertThat으로 변경 필요
    }

}
