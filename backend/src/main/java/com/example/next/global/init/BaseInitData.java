package com.example.next.global.init;

import com.example.next.domain.member.member.entity.Member;
import com.example.next.domain.member.member.service.MemberService;
import com.example.next.domain.post.post.entity.Post;
import com.example.next.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final PostService postService;
    private final MemberService memberService;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            self.memberInit();
            self.postInit();
        };
    }

    @Transactional
    public void memberInit() {

        if(memberService.count() > 0) {
            return;
        }

        // 회원 샘플데이터 생성
        memberService.join("system", "system1234", "시스템");
        memberService.join("admin", "admin1234", "관리자");
        memberService.join("user1", "user11234", "유저1");
        memberService.join("user2", "user21234", "유저2");
        memberService.join("user3", "user31234", "유저3");

    }

    @Transactional
    public void postInit() {

        if(postService.count() > 0) {
            return;
        }

        Member user1 = memberService.findByUsername("user1").get();
        Member user2 = memberService.findByUsername("user2").get();

        Post post1 = postService.write(user1, "축구 하실분 모집합니다.", "저녁 6시까지 모여주세요.", true, true);
        post1.addComment(user1, "저 참석하겠습니다.");
        post1.addComment(user2, "공격수 자리 있나요?");

        Post post2 = postService.write(user1, "농구하실분?", "3명 모집", true, false);
        post2.addComment(user1, "저는 이미 축구하기로 함..");

        postService.write(user2, "title3", "content3", false, true);
        postService.write(user1, "title4", "content4", true, true);
        postService.write(user1, "title5", "content5", true, true);
        postService.write(user2, "title6", "content6", true, true);
        postService.write(user2, "title7", "content7", true, true);
        postService.write(user2, "title8", "content8", true, true);
        postService.write(user2, "title9", "content9", true, true);


    }



}