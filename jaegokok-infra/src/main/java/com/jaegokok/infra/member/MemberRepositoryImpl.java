package com.jaegokok.infra.member;

import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.ErrorCode;
import com.jaegokok.core.member.MemberEntity;
import com.jaegokok.core.member.MemberStatus;
import com.jaegokok.domain.member.Member;
import com.jaegokok.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberJpaRepository.findByEmail(email)
                .filter(e -> e.getStatus() != MemberStatus.WITHDRAWN)
                .map(this::toMember);
    }

    @Override
    public Member register(String email, String encodedPassword, String nickname) {
        return toMember(memberJpaRepository.save(MemberEntity.from(email, encodedPassword, nickname)));
    }

    @Override
    public Member findById(Long id) {
        MemberEntity entity = memberJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        if (entity.getStatus() == MemberStatus.WITHDRAWN) {
            throw new CustomException(ErrorCode.WITHDRAWN_MEMBER);
        }
        return toMember(entity);
    }

    @Override
    public void withdraw(Long id) {
        MemberEntity entity = memberJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        entity.withdraw();
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public String findEncodedPasswordByEmail(String email) {
        return memberJpaRepository.findByEmail(email)
                .map(MemberEntity::getPassword)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Member toMember(MemberEntity entity) {
        return new Member(entity.getId(), entity.getEmail(), entity.getNickname(), entity.getRole(), entity.getStatus(), entity.getCreatedAt());
    }
}
