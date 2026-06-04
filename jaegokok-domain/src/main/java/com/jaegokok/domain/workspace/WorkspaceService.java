package com.jaegokok.domain.workspace;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.util.Filenames;
import com.jaegokok.core.image.ImageEntityType;
import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.app.AppConfigPort;
import com.jaegokok.domain.email.EmailPort;
import com.jaegokok.domain.subscription.SubscriptionPlan;
import com.jaegokok.domain.subscription.SubscriptionPlanRepository;
import com.jaegokok.domain.file.FileUploadPort;
import com.jaegokok.domain.file.ImageEncoderPort;
import com.jaegokok.domain.image.ImageRepository;
import com.jaegokok.domain.member.Member;
import com.jaegokok.domain.member.MemberRepository;
import com.jaegokok.domain.workspace.dto.CreateWorkspaceRequest;
import com.jaegokok.domain.workspace.dto.UpdateMemberRoleRequest;
import com.jaegokok.domain.workspace.dto.UpdateWorkspaceProfileRequest;
import com.jaegokok.domain.workspace.dto.WorkspaceMemberResponse;
import com.jaegokok.domain.workspace.dto.WorkspaceResponse;
import com.jaegokok.domain.workspace.dto.WorkspaceSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final MemberRepository memberRepository;
    private final FileUploadPort fileUploadPort;
    private final ImageEncoderPort imageEncoderPort;
    private final ImageRepository imageRepository;
    private final WorkspaceTrialRepository workspaceTrialRepository;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;
    private final EmailPort emailPort;
    private final AppConfigPort appConfigPort;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Transactional(readOnly = true)
    public WorkspaceResponse getMyWorkspace(Long memberId) {
        WorkspaceMember membership = workspaceMemberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        Workspace workspace = workspaceRepository.findById(membership.workspaceId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        return WorkspaceResponse.from(workspace, workspaceTrialRepository.findByWorkspaceId(workspace.id()), membership, fileUploadPort);
    }

    @Transactional(readOnly = true)
    public com.jaegokok.domain.workspace.dto.PublicWorkspaceResponse getPublicWorkspaceBySlug(String slug) {
        Workspace workspace = workspaceRepository.findBySlug(slug)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        return com.jaegokok.domain.workspace.dto.PublicWorkspaceResponse.from(workspace, fileUploadPort);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspaceBySlug(Long requesterId, String slug) {
        Workspace workspace = workspaceRepository.findBySlug(slug)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        if (!workspaceMemberRepository.existsByWorkspaceIdAndMemberId(workspace.id(), requesterId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
        Optional<WorkspaceTrial> trial = workspaceTrialRepository.findByWorkspaceId(workspace.id());
        return WorkspaceResponse.from(workspace, trial, fileUploadPort);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceSummaryResponse> listMyWorkspaces(Long memberId) {
        return workspaceMemberRepository.findAllByMemberId(memberId).stream()
                .map(wm -> workspaceRepository.findById(wm.workspaceId())
                        .map(ws -> new WorkspaceSummaryResponse(ws.id(), ws.name(), ws.slug(), wm.role().name()))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
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
        String slug = resolveSlug(request.slug(), request.name());
        Workspace workspace = workspaceRepository.saveWithSlug(memberId, request.name(), request.description(), WorkspacePlan.FREE, slug);
        workspaceMemberRepository.save(workspace.id(), memberId, WorkspaceMemberRole.OWNER);
        return WorkspaceResponse.from(workspace, Optional.empty(), fileUploadPort);
    }

    @Transactional
    public void inviteMember(Long inviterId, String email, WorkspaceMemberRole role) {
        Workspace workspace = workspaceRepository.findByOwnerId(inviterId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED));
        if (workspaceMemberRepository.existsByWorkspaceIdAndEmail(workspace.id(), email)) {
            throw new CustomException(ErrorCode.WORKSPACE_MEMBER_ALREADY_EXISTS);
        }
        SubscriptionPlan plan = subscriptionPlanRepository.findByPlanKey(
                getEffectivePlan(workspace.id()).name()).orElse(null);
        if (plan != null && plan.memberLimit() >= 0) {
            long currentCount = workspaceMemberRepository.countByWorkspaceId(workspace.id());
            if (currentCount >= plan.memberLimit()) {
                throw new CustomException(ErrorCode.MEMBER_LIMIT_EXCEEDED);
            }
        }
        // 이미 가입된 계정이면 바로 워크스페이스 멤버로 추가
        Optional<com.jaegokok.domain.member.Member> existingMember = memberRepository.findByEmail(email);
        if (existingMember.isPresent()) {
            workspaceMemberRepository.save(workspace.id(), existingMember.get().id(), role);
            return;
        }
        // 미가입 계정이면 초대 이메일 발송 (기존 초대 삭제 후 재발송)
        workspaceInvitationRepository.deleteByWorkspaceIdAndEmail(workspace.id(), email);
        WorkspaceInvitation invitation = workspaceInvitationRepository.save(workspace.id(), email, role);
        String inviteUrl = appConfigPort.getBaseUrl() + "/signup?invite=" + invitation.token();
        emailPort.sendInvitation(email, inviteUrl);
    }

    @Transactional(readOnly = true)
    public void validateInvitation(String token, String email) {
        WorkspaceInvitation invitation = workspaceInvitationRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVITATION_NOT_FOUND));
        if (invitation.used()) throw new CustomException(ErrorCode.INVITATION_ALREADY_USED);
        if (invitation.isExpired()) throw new CustomException(ErrorCode.INVITATION_EXPIRED);
        if (!invitation.email().equalsIgnoreCase(email)) throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
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
        if (!workspaceInvitationRepository.markUsedByToken(token)) {
            throw new CustomException(ErrorCode.INVITATION_ALREADY_USED);
        }
        if (!workspaceMemberRepository.existsByWorkspaceIdAndMemberId(invitation.workspaceId(), memberId)) {
            workspaceMemberRepository.save(invitation.workspaceId(), memberId, invitation.role());
        }
    }

    @Transactional
    public WorkspaceResponse updateProfile(Long memberId, UpdateWorkspaceProfileRequest request) {
        Workspace workspace = workspaceRepository.updateProfile(
                memberId, request.name(), request.businessNumber(), request.address(), request.phone());
        Optional<WorkspaceTrial> trial = workspaceTrialRepository.findByWorkspaceId(workspace.id());
        return WorkspaceResponse.from(workspace, trial, fileUploadPort);
    }

    @Transactional
    public WorkspaceResponse uploadLogo(Long memberId, String originalFilename, byte[] content, String contentType) {
        Workspace workspace = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        String originalKey = fileUploadPort.upload("workspaces/" + workspace.id() + "/logo", originalFilename, content, contentType);
        String webpKey = tryConvertAndUploadWebp("workspaces/" + workspace.id() + "/logo-webp", originalFilename, content);
        imageRepository.deleteByEntity(ImageEntityType.WORKSPACE, workspace.id());
        imageRepository.save(ImageEntityType.WORKSPACE, workspace.id(), originalKey, webpKey, fileUploadPort.getBucket());
        Workspace updated = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        Optional<WorkspaceTrial> trial = workspaceTrialRepository.findByWorkspaceId(updated.id());
        return WorkspaceResponse.from(updated, trial, fileUploadPort);
    }

    @Transactional(readOnly = true)
    public boolean isSlugAvailable(String slug) {
        if (!slug.matches("^[a-z0-9][a-z0-9-]{1,28}[a-z0-9]$")) return false;
        return !workspaceRepository.existsBySlug(slug);
    }

    @Transactional
    public WorkspaceResponse updateSlug(Long memberId, String newSlug) {
        Workspace workspace = workspaceRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        if (!newSlug.matches("^[a-z0-9][a-z0-9-]{1,28}[a-z0-9]$")) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        if (!newSlug.equals(workspace.slug()) && workspaceRepository.existsBySlug(newSlug)) {
            throw new CustomException(ErrorCode.WORKSPACE_SLUG_ALREADY_EXISTS);
        }
        Workspace updated = workspaceRepository.updateSlug(workspace.id(), newSlug);
        Optional<WorkspaceTrial> trial = workspaceTrialRepository.findByWorkspaceId(updated.id());
        return WorkspaceResponse.from(updated, trial, fileUploadPort);
    }

    private String tryConvertAndUploadWebp(String directory, String originalFilename, byte[] content) {
        try {
            byte[] webpBytes = imageEncoderPort.toWebp(content);
            return fileUploadPort.upload(directory, Filenames.stripExtension(originalFilename) + ".webp", webpBytes, "image/webp");
        } catch (RuntimeException e) {
            log.warn("WebP conversion/upload failed, falling back to original-only", e);
            return null;
        }
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
        return WorkspaceResponse.from(workspace, Optional.of(trial), fileUploadPort);
    }

    /**
     * Resolves the slug to use: uses the provided slug if valid and unique,
     * otherwise auto-generates from the workspace name.
     */
    private String resolveSlug(String requestedSlug, String name) {
        if (requestedSlug != null && !requestedSlug.isBlank()) {
            if (workspaceRepository.existsBySlug(requestedSlug)) {
                throw new CustomException(ErrorCode.WORKSPACE_SLUG_ALREADY_EXISTS);
            }
            return requestedSlug;
        }
        return generateUniqueSlug(name);
    }

    /**
     * Auto-generates a URL-safe slug from a workspace name.
     * Normalizes Unicode, lowercases, replaces spaces/special chars with hyphens, truncates to 30 chars.
     * Appends a numeric suffix if a collision is found.
     */
    private String generateUniqueSlug(String name) {
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.isEmpty()) base = "workspace";
        if (base.length() > 30) base = base.substring(0, 30).replaceAll("-+$", "");

        String candidate = base;
        int suffix = 2;
        while (workspaceRepository.existsBySlug(candidate)) {
            String suffixStr = "-" + suffix;
            int maxBase = 30 - suffixStr.length();
            candidate = (base.length() > maxBase ? base.substring(0, maxBase) : base) + suffixStr;
            suffix++;
        }
        return candidate;
    }
}
