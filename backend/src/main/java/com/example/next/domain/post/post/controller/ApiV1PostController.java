package com.example.next.domain.post.post.controller;

import com.example.next.domain.member.member.entity.Member;
import com.example.next.domain.member.member.service.MemberService;
import com.example.next.domain.post.post.dto.PageDto;
import com.example.next.domain.post.post.dto.PostWithContentDto;
import com.example.next.domain.post.post.entity.Post;
import com.example.next.domain.post.post.service.PostService;
import com.example.next.global.Rq;
import com.example.next.global.dto.RsData;
import com.example.next.global.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ApiV1PostController", description = "글 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {

    private final PostService postService;
    private final Rq rq;
    private final MemberService memberService;


    record StatisticsResBody(long postCount, long postPublishedCount, long postListedCount) {
    }

    @Operation(
            summary = "통계 조회"
    )
    @GetMapping("/statistics")
    public RsData<StatisticsResBody> getStatistics() {

        return new RsData<>(
                "200-1",
                "통계 조회가 완료되었습니다.",
                new StatisticsResBody(
                        10,
                        10,
                        10
                )
        );
    }

    @Operation(
            summary = "글 목록 조회",
            description = "페이징 처리와 검색 가능"
    )
    @GetMapping
    @Transactional(readOnly = true)
    public RsData<PageDto> getItems(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "3") int pageSize,
                                    @RequestParam(defaultValue = "title") SearchKeywordType keywordType,
                                    @RequestParam(defaultValue = "") String keyword) {
        Page<Post> postPage = postService.getListedItems(page, pageSize, keywordType, keyword);

        return new RsData<>(
                "200-1",
                "글 목록 조회가 완료되었습니다.",
                new PageDto(postPage)
        );

    }


    @Operation(
            summary = "내 글 목록 조회",
            description = "페이징 처리와 검색 가능"
    )
    @GetMapping("/mine")
    @Transactional(readOnly = true)
    public RsData<PageDto> getMines(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int pageSize,
            @RequestParam(defaultValue = "title") SearchKeywordType keywordType,
            @RequestParam(defaultValue = "") String keyword
    ) {

        Member actor = rq.getActor();
        Page<Post> pagePost = postService.getMines(actor, page, pageSize, keywordType, keyword);

        return new RsData<>("200-1",
                "내 글 목록 조회가 완료되었습니다.",
                new PageDto(pagePost)
        );

    }

    @Operation(
            summary = "글 단건 조회",
            description = "비밀글은 작성자만 조회 가능"
    )
    @GetMapping("{id}")
    @Transactional(readOnly = true)
    public RsData<PostWithContentDto> getItem(@PathVariable long id) {

        Post post = postService.getItem(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글입니다.")
        );

        if (!post.isPublished()) {
            Member actor = rq.getActor();
            post.canRead(actor);
        }

        return new RsData<>(
                "200-1",
                "%d번 글을 조회하였습니다.".formatted(id),
                new PostWithContentDto(post)
        );
    }

    record WriteReqBody(@NotBlank String title,
                        @NotBlank String content,
                        boolean published,
                        boolean listed) {
    }

    @Operation(
            summary = "글 작성",
            description = "로그인 한 사용자만 글 작성 가능"
    )
    @PostMapping
    @Transactional
    public RsData<PostWithContentDto> write(@RequestBody @Valid WriteReqBody reqBody) {

        Member actor = rq.getActor();
        Member realActor = rq.getRealActor(actor);

        Post post = postService.write(realActor, reqBody.title(), reqBody.content(), reqBody.published(), reqBody.listed());

        return new RsData<>(
                "201-1",
                "%d번 글 작성이 완료되었습니다.".formatted(post.getId()),
                new PostWithContentDto(post)
        );
    }

    record ModifyReqBody(@NotBlank String title, @NotBlank String content) {
    }

    @Operation(
            summary = "글 수정",
            description = "작성자와 관리자만 글 수정 가능"
    )
    @PutMapping("{id}")
    @Transactional
    public RsData<PostWithContentDto> modify(@PathVariable long id, @RequestBody @Valid ModifyReqBody reqBody) {

        Member actor = rq.getActor(); // 야매

        Post post = postService.getItem(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글입니다.")
        );

        post.canModify(actor);

        postService.modify(post, reqBody.title(), reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 글 수정이 완료되었습니다.".formatted(id),
                new PostWithContentDto(post)
        );
    }

    @Operation(
            summary = "글 삭제",
            description = "작성자와 관리자만 글 삭제 가능"
    )
    @DeleteMapping("{id}")
    @Transactional
    public RsData<Void> delete(@PathVariable long id) {

        Member actor = rq.getActor();

        Post post = postService.getItem(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글입니다.")
        );

        post.canDelete(actor);
        postService.delete(post);

        return new RsData<>(
                "200-1",
                "%d번 글 삭제가 완료되었습니다.".formatted(id)
        );

    }


}