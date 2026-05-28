package com.jaegokok.core.member;

import com.jaegokok.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Builder
    private MemberEntity(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = MemberRole.USER;
        this.status = MemberStatus.ACTIVE;
    }

    public static MemberEntity from(String email, String encodedPassword, String nickname) {
        return MemberEntity.builder().email(email).password(encodedPassword).nickname(nickname).build();
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
    }
}
