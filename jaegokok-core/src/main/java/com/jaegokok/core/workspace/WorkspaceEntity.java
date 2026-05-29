package com.jaegokok.core.workspace;

import com.jaegokok.core.BaseEntity;
import com.jaegokok.core.member.MemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workspaces")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private MemberEntity owner;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspacePlan plan = WorkspacePlan.FREE;


    @Builder
    private WorkspaceEntity(MemberEntity owner, String name, String description, WorkspacePlan plan) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.plan = plan;
    }

    public static WorkspaceEntity from(MemberEntity owner, String name, String description, WorkspacePlan plan) {
        return WorkspaceEntity.builder()
                .owner(owner)
                .name(name)
                .description(description)
                .plan(plan)
                .build();
    }

    public void updatePlan(WorkspacePlan plan) {
        this.plan = plan;
    }





}
