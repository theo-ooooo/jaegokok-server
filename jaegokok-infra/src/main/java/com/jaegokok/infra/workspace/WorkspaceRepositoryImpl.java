package com.jaegokok.infra.workspace;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.member.MemberEntity;
import com.jaegokok.core.workspace.WorkspaceEntity;
import com.jaegokok.core.workspace.WorkspaceImageEntity;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.workspace.Workspace;
import com.jaegokok.domain.workspace.WorkspaceImage;
import com.jaegokok.domain.workspace.WorkspaceRepository;
import com.jaegokok.infra.member.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkspaceRepositoryImpl implements WorkspaceRepository {
    private final WorkspaceJpaRepository workspaceJpaRepository;
    private final WorkspaceQueryRepository workspaceQueryRepository;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Workspace save(Long ownerId, String name, String description, WorkspacePlan plan) {
        MemberEntity member = memberJpaRepository.findById(ownerId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        WorkspaceEntity workspace = WorkspaceEntity.from(member, name, description, plan);

        return toWorkspace(workspaceJpaRepository.save(workspace));
    }

    @Override
    public Optional<Workspace> findById(Long id) {
        return workspaceJpaRepository.findById(id).map(this::toWorkspace);
    }

    @Override
    public Optional<Workspace> findByOwnerId(Long ownerId) {
        return workspaceJpaRepository.findByOwner_Id(ownerId).map(this::toWorkspace);
    }

    @Override
    public List<Workspace> findAllByMemberId(Long memberId) {
        return workspaceQueryRepository.findAllByMemberId(memberId)
                .stream().map(this::toWorkspace).toList();
    }

    @Override
    public boolean existsByOwnerId(Long ownerId) {
        return workspaceJpaRepository.existsByOwner_Id(ownerId);
    }

    @Override
    public Workspace updateProfile(Long ownerId, String companyName, String businessNumber, String address, String phone) {
        WorkspaceEntity entity = workspaceJpaRepository.findByOwner_Id(ownerId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        entity.updateProfile(companyName, businessNumber, address, phone);
        return toWorkspace(entity);
    }

    private Workspace toWorkspace(WorkspaceEntity e) {
        WorkspaceImage logo = e.getImages().isEmpty() ? null
                : toWorkspaceImage(e.getImages().get(0));
        return new Workspace(
                e.getId(),
                e.getOwner().getId(),
                e.getName(),
                e.getDescription(),
                e.getPlan(),
                e.getCompanyName(),
                e.getBusinessNumber(),
                e.getAddress(),
                e.getPhone(),
                logo,
                e.getCreatedAt()
        );
    }

    private WorkspaceImage toWorkspaceImage(WorkspaceImageEntity e) {
        return new WorkspaceImage(e.getId(), e.getWorkspace().getId(), e.getOriginalPath(), e.getWebpPath(), e.getBucket(), e.getCreatedAt());
    }

}
