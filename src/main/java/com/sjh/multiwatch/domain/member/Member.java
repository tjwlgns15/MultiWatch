package com.sjh.multiwatch.domain.member;

import com.sjh.multiwatch.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private Member(Long organizationId, String email, String passwordHash, MemberRole role) {
        this.organizationId = organizationId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public static Member registerAdmin(Long organizationId, String email, String passwordHash) {
        return new Member(organizationId, email, passwordHash, MemberRole.ADMIN);
    }

    public static Member registerViewer(Long organizationId, String email, String passwordHash) {
        return new Member(organizationId, email, passwordHash, MemberRole.VIEWER);
    }

    public boolean isAdmin() {
        return this.role == MemberRole.ADMIN;
    }
}
