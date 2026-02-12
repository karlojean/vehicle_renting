package dev.jeankarlo.vehiclerenting.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionPatchDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionResponseDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.inspectionMedia.InspectionMediaResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.service.InspectionMediaService;
import dev.jeankarlo.vehiclerenting.service.InspectionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/inspections")
@PreAuthorize("hasRole('PARTNER')")
public class InspectionController {

    private final InspectionService inspectionService;
    private final InspectionMediaService inspectionMediaService;

    public InspectionController(InspectionService inspectionService, InspectionMediaService inspectionMediaService) {
        this.inspectionService = inspectionService;
        this.inspectionMediaService = inspectionMediaService;
    }

    @PostMapping
    public ResponseEntity<InspectionResponseDTO> initInspection(
            @RequestBody @Valid InspectionInitDTO inspectionInitDTO,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionService.initInspection(inspectionInitDTO, partnerId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InspectionResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid InspectionPatchDTO inspectionPatchDTO,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionService.updateById(id, inspectionPatchDTO, partnerId));
    }

    @PostMapping("/{id}/medias")
    public ResponseEntity<InspectionMediaResponseDTO> uploadInspectionMedia(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionMediaService.uploadMedia(id, partnerId, file));
    }

    @GetMapping("/{id}/medias")
    public ResponseEntity<List<InspectionMediaResponseDTO>> getInspectionMediasById(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionMediaService.getMediasByInspectionId(id, partnerId));
    }

    @DeleteMapping("/{id}/medias/{mediaId}")
    public ResponseEntity<Void> deleteInspectionMedia(
            @PathVariable Long id,
            @PathVariable UUID mediaId,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        inspectionMediaService.deleteMedia(id, mediaId, partnerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<InspectionResponseDTO> completeInspection(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionService.completeInspection(id, partnerId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<InspectionResponseDTO> cancelInspection(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionService.cancelInspection(id, partnerId));
    }

    @GetMapping
    public ResponseEntity<List<InspectionResponseDTO>> getInspectionByBookingId(
            @RequestParam Long bookingId,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionService.getInspectionsByBookingId(bookingId, partnerId));
    }
}
