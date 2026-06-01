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
import com.jaegokok.domain.workspace.dto.InviteMemberRequest;
import com.jaegokok.domain.workspace.dto.InviteMemberResponse;
import com.jaegokok.domain.workspace.dto.UpdateWorkspaceProfileRequest;
import com.jaegokok.domain.workspace.dto.WorkspaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final MemberRepository memberRepository;
    private final FileUploadPort fileUploadPort;
    private final ImageRepository imageRepository;

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
    public InviteMemberResponse inviteMember(Long inviterId, InviteMemberRequest request) {
        Workspace workspace = workspaceRepository.findById(request.workspaceId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        if (!workspace.ownerId().equals(inviterId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        Member invitee = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        if (workspaceMemberRepository.existsByWorkspaceIdAndMemberId(request.workspaceId(), invitee.id())) {
            throw new CustomException(ErrorCode.WORKSPACE_MEMBER_ALREADY_EXISTS);
        }
        WorkspaceMember member = workspaceMemberRepository.save(request.workspaceId(), invitee.id(), WorkspaceMemberRole.EMPLOYEE);
        return InviteMemberResponse.from(member);
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
