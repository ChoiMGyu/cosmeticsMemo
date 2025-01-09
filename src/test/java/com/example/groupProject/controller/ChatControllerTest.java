package com.example.groupProject.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class ChatControllerTest {

    private static final String SOCKET_URL = "ws://127.0.0.1:8080/ws-stomp";
    private static StompSession session;

    @BeforeAll
    public static void beforeAll() throws ExecutionException, InterruptedException {
        session = setSession();
    }

    private static StompSession setSession() throws ExecutionException, InterruptedException {
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

    private static class MyStompSessionHandler extends StompSessionHandlerAdapter{
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("connect 완료");
        }
    }

    @Test
    @DisplayName("WebSocket 연결 테스트")
    public void 서버_연결() throws Exception {
        assertThat(session.isConnected()).isTrue();
    }

}
