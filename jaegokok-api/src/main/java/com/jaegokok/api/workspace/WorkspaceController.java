package com.jaegokok.api.workspace;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.api.util.FileValidator;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.workspace.WorkspaceService;
import com.jaegokok.domain.workspace.dto.CreateWorkspaceRequest;
import com.jaegokok.domain.workspace.dto.UpdateWorkspaceProfileRequest;
import com.jaegokok.domain.workspace.dto.WorkspaceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @GetMapping("/me")
    public GlobalResponse<WorkspaceResponse> getMyWorkspace(@AuthenticationPrincipal UserPrincipal principal) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.getMyWorkspace(principal.getId()));
    }

    @GetMapping("/@{slug}")
    public GlobalResponse<WorkspaceResponse> getBySlug(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String slug) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.getWorkspaceBySlug(principal.getId(), slug));
    }

    @PostMapping
    public GlobalResponse<WorkspaceResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateWorkspaceRequest request
    ) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), workspaceService.create(principal.getId(), request));
    }

    @PatchMapping("/me/profile")
    public GlobalResponse<WorkspaceResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateWorkspaceProfileRequest request
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.updateProfile(principal.getId(), request));
    }

    @GetMapping("/public/@{slug}")
    public GlobalResponse<com.jaegokok.domain.workspace.dto.PublicWorkspaceResponse> getPublicWorkspace(
            @PathVariable String slug
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.getPublicWorkspaceBySlug(slug));
    }

    @GetMapping("/check-slug")
    public GlobalResponse<Boolean> checkSlugAvailable(@RequestParam String slug) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.isSlugAvailable(slug));
    }

    @PatchMapping("/me/slug")
    public GlobalResponse<WorkspaceResponse> updateSlug(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String slug
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), workspaceService.updateSlug(principal.getId(), slug));
    }

    @PostMapping(value = "/me/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponse<WorkspaceResponse> uploadLogo(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart MultipartFile file
    ) throws IOException {
        FileValidator.validateImage(file);
        return GlobalResponse.success(HttpStatus.OK.value(),
                workspaceService.uploadLogo(principal.getId(), FileValidator.safeFilename(file), file.getBytes(), file.getContentType()));
    }
}
