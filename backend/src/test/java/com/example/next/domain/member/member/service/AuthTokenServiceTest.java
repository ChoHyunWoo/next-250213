package com.example.next.domain.member.member.service;

import com.example.next.domain.member.member.entity.Member;
import com.example.next.standard.util.Ut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthTokenServiceTest {

    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private MemberService memberService;

    @Value("${custom.jwt.secret-key}")
    private String keyString;

    @Value("${custom.jwt.expire-seconds}")
    private int expireSeconds;

    @Test
    @DisplayName("AuthTokenService 생성")
    void init() {
        assertThat(authTokenService).isNotNull();
    }

    @Test
    @DisplayName("jwt 생성")
    void createToken() {
        Map<String, Object> originPayload = Map.of("name", "john", "age", 23);

        String jwtStr = Ut.Jwt.createToken(keyString, expireSeconds, originPayload);
        assertThat(jwtStr).isNotBlank();
        Map<String, Object> parsedPayload = Ut.Jwt.getPayload(keyString, jwtStr);

        assertThat(parsedPayload).containsAllEntriesOf(originPayload);
    }

    @Test
    @DisplayName("user1 - access token 생성")
    void accessToken() {

        // jwt -> access token jwt
        Member member = memberService.findByUsername("user1").get();
        String accessToken = authTokenService.genAccessToken(member);

        assertThat(accessToken).isNotBlank();

        System.out.println("accessToken = " + accessToken);
    }

    @Test
    @DisplayName("jwt valid check")
    void checkValid() {

        Member member = memberService.findByUsername("user1").get();
        String accessToken = authTokenService.genAccessToken(member);
        boolean isValid = Ut.Jwt.isValidToken(keyString, accessToken);
        assertThat(isValid).isTrue();

        Map<String, Object> parsedPayload = authTokenService.getPayload(accessToken);

        assertThat(parsedPayload).containsAllEntriesOf(
                Map.of("id", member.getId(), "username", member.getUsername())
        );

    }

}