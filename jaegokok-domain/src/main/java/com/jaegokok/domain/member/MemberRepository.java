package com.jaegokok.domain.member;

import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findByEmail(String email);

    Member register(String email, String encodedPassword, String nickname);

    Member findById(Long id);

    void withdraw(Long id);

    boolean existsByEmail(String email);

    // 로그인 비밀번호 검증용 — 해시된 비밀번호 반환
    String findEncodedPasswordByEmail(String email);
}
