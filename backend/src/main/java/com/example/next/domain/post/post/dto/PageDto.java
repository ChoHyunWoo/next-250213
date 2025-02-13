package com.example.next.domain.post.post.dto;

import com.example.next.domain.post.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageDto {

    List<PostDto> items;
    int totalPages;
    int totalItems;
    int currentPageNo;
    int pageSize;

    public PageDto(Page<Post> postPage) {

        this.items = postPage.getContent().stream()
                .map(PostDto::new)
                .toList();

        this.totalPages = postPage.getTotalPages();
        this.totalItems = (int) postPage.getTotalElements();
        this.currentPageNo = postPage.getNumber() + 1;
        this.pageSize = postPage.getSize();
    }

}