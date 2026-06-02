package com.jaegokok.domain.workspace;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.image.ImageEntityType;
import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.email.EmailPort;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final MemberRepository memberRepository;
    private final FileUploadPort fileUploadPort;
    private final ImageRepository imageRepository;
    private final WorkspaceTrialRepository workspaceTrialRepository;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;
    private final EmailPort emailPort;

    @Transactional(readOnly = true)
    public WorkspaceResponse getMyWorkspace(Long memberId) {
        WorkspaceMember membership = workspaceMemberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        Workspace workspace = workspaceRepository.findById(membership.workspaceId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        return WorkspaceResponse.from(workspace, workspaceTrialRepository.findByWorkspaceId(workspace.id()));
    }

    @Transactional(readOnly = true)
    public List<WorkspaceMemberResponse> listMembers(Long requesterId) {
        WorkspaceMember membership = workspaceMemberRepository.findByMemberId(requesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        return workspaceMemberRepository.findByWorkspaceId(membership.workspaceId()).stream()
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
        if (request.role() == WorkspaceMemberRole.OWNER) {
            throw new CustomException(ErrorCode.CANNOT_CHANGE_OWNER_ROLE);
        }
        if (wm.role() == WorkspaceMemberRole.OWNER) {
            throw new CustomException(ErrorCode.CANNOT_CHANGE_OWNER_ROLE);
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
        if (wm.memberId().equals(requesterId)) {
            throw new CustomException(ErrorCode.CANNOT_REMOVE_SELF);
        }
        if (wm.role() == WorkspaceMemberRole.OWNER) {
            throw new CustomException(ErrorCode.CANNOT_CHANGE_OWNER_ROLE);
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
        return WorkspaceResponse.from(workspace, Optional.empty());
    }

    @Transactional
    public void inviteMember(Long inviterId, String email) {
        Workspace workspace = workspaceRepository.findByOwnerId(inviterId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        if (workspaceMemberRepository.existsByWorkspaceIdAndEmail(workspace.id(), email)) {
            throw new CustomException(ErrorCode.WORKSPACE_MEMBER_ALREADY_EXISTS);
        }
        WorkspaceInvitation invitation = workspaceInvitationRepository.save(workspace.id(), email);
        String inviteUrl = "https://jaegokok.com/signup?invite=" + invitation.token();
        emailPort.sendInvitation(email, inviteUrl);
    }

    @Transactional
    public void acceptInvitation(String token, Long memberId) {
        WorkspaceInvitation invitation = workspaceInvitationRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVITATION_NOT_FOUND));
        if (invitation.used()) {
            throw new CustomException(ErrorCode.INVITATION_ALREADY_USED);
        }
        if (invitation.isExpired()) {
            throw new CustomException(ErrorCode.INVITATION_EXPIRED);
        }
        Member member = memberRepository.findById(memberId);
        if (!invitation.email().equalsIgnoreCase(member.email())) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        if (!workspaceMemberRepository.existsByWorkspaceIdAndMemberId(invitation.workspaceId(), memberId)) {
            workspaceMemberRepository.save(invitation.workspaceId(), memberId, WorkspaceMemberRole.EMPLOYEE);
        }
        workspaceInvitationRepository.markUsed(invitation.id());
    }

    @Transactional
    public WorkspaceResponse updateProfile(Long memberId, UpdateWorkspaceProfileRequest request) {
        Workspace workspace = workspaceRepository.updateProfile(
                memberId, request.name(), request.businessNumber(), request.address(), request.phone());
        Optional<WorkspaceTrial> trial = workspaceTrialRepository.findByWorkspaceId(workspace.id());
        return WorkspaceResponse.from(workspace, trial);
    }

    @Transactional
    public WorkspaceResponse uploadLogo(Long memberId, String originalFilename, byte[] content, String contentType) {
        Workspace workspace = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        String originalPath = fileUploadPort.upload("workspaces/" + workspace.id() + "/logo", originalFilename, content, contentType);
        imageRepository.deleteByEntity(ImageEntityType.WORKSPACE, workspace.id());
        imageRepository.save(ImageEntityType.WORKSPACE, workspace.id(), originalPath, null, fileUploadPort.getBucket());
        Workspace updated = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        Optional<WorkspaceTrial> trial = workspaceTrialRepository.findByWorkspaceId(updated.id());
        return WorkspaceResponse.from(updated, trial);
    }

    @Transactional(readOnly = true)
    public WorkspacePlan getEffectivePlan(Long workspaceId) {
        Optional<WorkspaceTrial> trial = workspaceTrialRepository.findByWorkspaceId(workspaceId);
        if (trial.isPresent() && !trial.get().isActive()) {
            // Trial expired — downgrade back to FREE on the fly
            return WorkspacePlan.FREE;
        }
        return workspaceRepository.findById(workspaceId)
                .map(Workspace::plan)
                .orElse(WorkspacePlan.FREE);
    }

    @Transactional
    public WorkspaceResponse startTrial(Long memberId) {
        Workspace workspace = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        if (workspaceTrialRepository.findByWorkspaceId(workspace.id()).isPresent()) {
            throw new CustomException(ErrorCode.TRIAL_ALREADY_STARTED);
        }
        if (workspace.plan() != WorkspacePlan.FREE) {
            throw new CustomException(ErrorCode.ALREADY_ON_PAID_PLAN);
        }
        WorkspaceTrial trial = workspaceTrialRepository.save(workspace.id());
        workspaceRepository.updatePlan(workspace.id(), WorkspacePlan.PRO);
        return WorkspaceResponse.from(workspace, Optional.of(trial));
    }
}
