package com.jaegokok.domain.workspace;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.image.ImageEntityType;
import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.file.FileUploadPort;
import com.jaegokok.domain.image.ImageRepository;
import com.jaegokok.domain.member.Member;
import com.jaegokok.domain.member.MemberRepository;
import com.jaegokok.domain.workspace.dto.CreateWorkspaceRequest;
import com.jaegokok.domain.workspace.dto.UpdateMemberRoleRequest;
import com.jaegokok.domain.workspace.dto.UpdateWorkspaceProfileRequest;
import com.jaegokok.domain.workspace.dto.WorkspaceMemberResponse;
import com.jaegokok.domain.workspace.dto.WorkspaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final MemberRepository memberRepository;
    private final FileUploadPort fileUploadPort;
    private final ImageRepository imageRepository;

    public WorkspaceResponse getMyWorkspace(Long memberId) {
        return WorkspaceResponse.from(workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND)));
    }

    public List<WorkspaceMemberResponse> listMembers(Long requesterId) {
        Workspace workspace = workspaceRepository.findByOwnerId(requesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        return workspaceMemberRepository.findByWorkspaceId(workspace.id()).stream()
                .map(wm -> {
                    Member member = memberRepository.findById(wm.memberId());
                    return new WorkspaceMemberResponse(
                            wm.id(),
                            member.id(),
                            member.nickname(),
                            member.email(),
                            wm.role(),
                            member.status()
                    );
                })
                .toList();
    }

    @Transactional
    public WorkspaceMemberResponse updateMemberRole(Long requesterId, Long workspaceMemberId, UpdateMemberRoleRequest request) {
        Workspace workspace = workspaceRepository.findByOwnerId(requesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        WorkspaceMember wm = workspaceMemberRepository.findById(workspaceMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        if (!wm.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        WorkspaceMember updated = workspaceMemberRepository.updateRole(workspaceMemberId, request.role());
        Member member = memberRepository.findById(updated.memberId());
        return new WorkspaceMemberResponse(
                updated.id(),
                member.id(),
                member.nickname(),
                member.email(),
                updated.role(),
                member.status()
        );
    }

    @Transactional
    public void removeMember(Long requesterId, Long workspaceMemberId) {
        Workspace workspace = workspaceRepository.findByOwnerId(requesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        WorkspaceMember wm = workspaceMemberRepository.findById(workspaceMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        if (!wm.workspaceId().equals(workspace.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        workspaceMemberRepository.deleteById(workspaceMemberId);
    }

    @Transactional
    public WorkspaceResponse create(Long memberId, CreateWorkspaceRequest request) {
        if (workspaceRepository.existsByOwnerId(memberId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ALREADY_EXISTS);
        }
        Workspace workspace = workspaceRepository.save(memberId, request.name(), request.description(), WorkspacePlan.FREE);
        workspaceMemberRepository.save(workspace.id(), memberId, WorkspaceMemberRole.OWNER);
        return WorkspaceResponse.from(workspace);
    }

    @Transactional
    public WorkspaceResponse updateProfile(Long memberId, UpdateWorkspaceProfileRequest request) {
        return WorkspaceResponse.from(workspaceRepository.updateProfile(
                memberId, request.companyName(), request.businessNumber(), request.address(), request.phone()));
    }

    @Transactional
    public WorkspaceResponse uploadLogo(Long memberId, String originalFilename, byte[] content, String contentType) {
        Workspace workspace = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        String originalPath = fileUploadPort.upload("workspaces/" + workspace.id() + "/logo", originalFilename, content, contentType);
        imageRepository.deleteByEntity(ImageEntityType.WORKSPACE, workspace.id());
        imageRepository.save(ImageEntityType.WORKSPACE, workspace.id(), originalPath, null, fileUploadPort.getBucket());
        return WorkspaceResponse.from(workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND)));
    }
}
