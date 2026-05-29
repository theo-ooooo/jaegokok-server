package com.jaegokok.infra.member;

import com.jaegokok.core.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<MemberEntity> findAllByIdIn(List<Long> ids);
}
