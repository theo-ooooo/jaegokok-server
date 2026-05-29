package com.jaegokok.domain.workspace;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.member.Member;
import com.jaegokok.domain.member.MemberRepository;
import com.jaegokok.domain.workspace.dto.AddEmployeeRequest;
import com.jaegokok.domain.workspace.dto.AddEmployeeResponse;
import com.jaegokok.domain.workspace.dto.CreateWorkspaceRequest;
import com.jaegokok.domain.workspace.dto.UpdateWorkspaceRequest;
import com.jaegokok.domain.workspace.dto.WorkspaceMemberResponse;
import com.jaegokok.domain.workspace.dto.WorkspaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public WorkspaceResponse create(Long memberId, CreateWorkspaceRequest request) {
        if (workspaceRepository.existsByOwnerId(memberId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ALREADY_EXISTS);
        }
        Workspace workspace = workspaceRepository.save(memberId, request.name(), request.description(), WorkspacePlan.FREE);
        workspaceMemberRepository.save(workspace.id(), memberId, WorkspaceMemberRole.OWNER);
        return WorkspaceResponse.from(workspace);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponse getMyWorkspace(Long memberId) {
        return workspaceRepository.findByOwnerId(memberId)
                .map(WorkspaceResponse::from)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
    }

    @Transactional
    public WorkspaceResponse updateMyWorkspace(Long memberId, UpdateWorkspaceRequest request) {
        return WorkspaceResponse.from(workspaceRepository.update(memberId, request.name(), request.description()));
    }

    @Transactional
    public AddEmployeeResponse addEmployee(Long workspaceId, Long requesterId, AddEmployeeRequest request) {
        checkOwner(workspaceId, requesterId);
        if (memberRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        Member member = memberRepository.register(request.email(), passwordEncoder.encode(request.temporaryPassword()), request.nickname());
        workspaceMemberRepository.save(workspaceId, member.id(), WorkspaceMemberRole.EMPLOYEE);
        return AddEmployeeResponse.from(member);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceMemberResponse> getMembers(Long workspaceId, Long requesterId) {
        checkOwner(workspaceId, requesterId);
        return workspaceMemberRepository.findByWorkspaceId(workspaceId).stream()
                .map(wm -> WorkspaceMemberResponse.of(wm, memberRepository.findById(wm.memberId())))
                .toList();
    }

    @Transactional
    public void deleteEmployee(Long workspaceId, Long requesterId, Long targetMemberId) {
        checkOwner(workspaceId, requesterId);
        if (requesterId.equals(targetMemberId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        WorkspaceMember wm = workspaceMemberRepository.findByWorkspaceIdAndMemberId(workspaceId, targetMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED));
        workspaceMemberRepository.deleteById(wm.id());
    }

    private void checkOwner(Long workspaceId, Long memberId) {
        WorkspaceMember wm = workspaceMemberRepository.findByWorkspaceIdAndMemberId(workspaceId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED));
        if (wm.role() != WorkspaceMemberRole.OWNER) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
    }
}
