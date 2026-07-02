package com.sjh.multiwatch.infrastructure.security;

import com.sjh.multiwatch.domain.member.Member;
import com.sjh.multiwatch.domain.member.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class MemberPrincipal implements UserDetails {

    private final Long memberId;
    private final Long organizationId;
    private final String email;
    private final String passwordHash;
    private final MemberRole role;


    private MemberPrincipal(Member member) {
        this.memberId = member.getId();
        this.organizationId = member.getOrganizationId();
        this.email = member.getEmail();
        this.passwordHash = member.getPasswordHash();
        this.role = member.getRole();
    }

    public static MemberPrincipal from(Member member) {
        return new MemberPrincipal(member);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
