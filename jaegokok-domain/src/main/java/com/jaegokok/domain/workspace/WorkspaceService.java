package com.jaegokok.domain.workspace;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.core.workspace.WorkspacePlan;
import com.jaegokok.domain.workspace.dto.CreateWorkspaceRequest;
import com.jaegokok.domain.workspace.dto.WorkspaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Transactional
    public WorkspaceResponse create(Long memberId, CreateWorkspaceRequest request) {
        if (workspaceRepository.existsByOwnerId(memberId)) {
            throw new CustomException(ErrorCode.WORKSPACE_ALREADY_EXISTS);
        }
        Workspace workspace = workspaceRepository.save(memberId, request.name(), request.description(), WorkspacePlan.FREE);
        workspaceMemberRepository.save(workspace.id(), memberId, WorkspaceMemberRole.OWNER);
        return WorkspaceResponse.from(workspace);
    }
}
