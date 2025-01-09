package com.example.groupProject.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
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
    private RedissonClient redissonClient;
    private static final CountDownLatch latch = new CountDownLatch(1);

    @BeforeEach
    public void beforeEach() throws ExecutionException, InterruptedException {
        session = setSession();
        redissonClient = createRedissonClient();
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
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("connect 완료");
        }
    }

    private RedissonClient createRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        return org.redisson.Redisson.create(config);
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
    @DisplayName("Redis Pub/Sub을 이용한 WebSocket 통신 테스트")
    public void redis_websocket_통신() throws Exception {
        RTopic topic = redissonClient.getTopic("chatChannel");

        topic.addListener(String.class, (channel, msg) -> {
            System.out.println("Received message from Redis: " + msg);
            latch.countDown(); // 메시지 수신 시 테스트 진행을 위한 latch 카운트다운
        });

        // WebSocket을 통해 메시지 발행
        session.send("/app/chat", "Hello from WebSocket!");

        // Redis에 메시지 발행
        topic.publish("Hello from Redis!");

        // 메시지가 수신될 때까지 대기 (최대 5초)
        boolean messageReceived = latch.await(5, java.util.concurrent.TimeUnit.SECONDS);

        assertThat(messageReceived).isTrue(); // 메시지 수신이 성공적으로 이루어졌는지 확인
    }

}
