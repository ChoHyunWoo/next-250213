package com.example.next.domain.post.post.service;

import com.example.next.domain.member.member.entity.Member;
import com.example.next.domain.post.post.controller.SearchKeywordType;
import com.example.next.domain.post.post.entity.Post;
import com.example.next.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post write(Member author, String title, String content, boolean published, boolean listed) {

        return postRepository.save(
                Post
                        .builder()
                        .author(author)
                        .title(title)
                        .content(content)
                        .published(published)
                        .listed(listed)
                        .build()
        );
    }

    public List<Post> getItems() {
        return postRepository.findAll();
    }

    public Optional<Post> getItem(long id) {
        return postRepository.findById(id);
    }

    public long count() {
        return postRepository.count();
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Transactional
    public void modify(Post post, String title, String content) {
        post.setTitle(title);
        post.setContent(content);
    }

    public void flush() {
        postRepository.flush();
    }

    public Optional<Post> getLatestItem() {
        return postRepository.findTopByOrderByIdDesc();
    }

    public Page<Post> getListedItems(int page, int pageSize, SearchKeywordType keywordType, String keyword) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);

        String likeKeyword = "%" + keyword + "%";

        if(SearchKeywordType.CONTENT == keywordType) {
            return postRepository.findByListedAndContentLike(true, likeKeyword, pageRequest);
        }


        return postRepository.findByListedAndTitleLike(true, likeKeyword, pageRequest);
    }

    public Page<Post> getMines(Member author, int page, int pageSize, SearchKeywordType keywordType, String keyword) {

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        String likeKeyword = "%" + keyword + "%";

        if(SearchKeywordType.CONTENT == keywordType ) {
            return postRepository.findByAuthorAndContentLike(author, likeKeyword, pageRequest);
        }

        return postRepository.findByAuthorAndTitleLike(author, likeKeyword, pageRequest);

    }
}