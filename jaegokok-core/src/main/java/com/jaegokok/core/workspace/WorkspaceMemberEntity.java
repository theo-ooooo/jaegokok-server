package com.jaegokok.core.workspace;


import com.jaegokok.core.member.MemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workspace_members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_workspace_members_workspace_member", columnNames = {"workspace_id", "member_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceMemberRole role = WorkspaceMemberRole.EMPLOYEE;

    @Builder
    private WorkspaceMemberEntity(WorkspaceEntity workspace, MemberEntity member, WorkspaceMemberRole role) {
        this.workspace = workspace;
        this.member = member;
        this.role = role;
    }

    public static WorkspaceMemberEntity from(WorkspaceEntity workspace, MemberEntity member, WorkspaceMemberRole role) {
        return WorkspaceMemberEntity.builder()
                .workspace(workspace)
                .member(member)
                .role(role)
                .build();
    }


}
