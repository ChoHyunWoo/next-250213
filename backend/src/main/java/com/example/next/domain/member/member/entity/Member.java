package com.example.next.domain.member.member.entity;

import com.example.next.global.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseTime {

    @Column(length = 100, unique = true)
    private String username;
    @Column(length = 100)
    private String password;
    @Column(length = 100, unique = true)
    private String apiKey;
    @Column(length = 100)
    private String nickname;

    public boolean isAdmin() {
        return username.equals("admin");
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return getMemberAuthoritesAsString()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

    }

    public List<String> getMemberAuthoritesAsString() {

        List<String> authorities = new ArrayList<>();

        if(isAdmin()) {
            authorities.add("ROLE_ADMIN");
        }

        return authorities;
    }

}