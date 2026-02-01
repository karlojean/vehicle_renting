package dev.jeankarlo.vehiclerenting.controller;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionPatchDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionResponseDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.inspectionImage.InspectionImageRespondeDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.service.InspectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/inspections")
@PreAuthorize("hasRole('PARTNER')")
public class InspectionController {

    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @PostMapping
    public ResponseEntity<InspectionResponseDTO> initInspection(
            @RequestBody @Valid InspectionInitDTO inspectionInitDTO,
            @AuthenticationPrincipal Account account
            ) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionService.initInspection(inspectionInitDTO, partnerId));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<InspectionImageRespondeDTO> uploadInspectionImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionService.uploadInspectionImage(id, file, partnerId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InspectionResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid InspectionPatchDTO inspectionPatchDTO,
            @AuthenticationPrincipal Account account) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(inspectionService.updateById(id, inspectionPatchDTO, partnerId));
    }

//    @GetMapping("/{id}/images")
//    public ResponseEntity<List<InspectionImageRespondeDTO>> getInspectionImagesById(
//            @PathVariable Long id,
//            @AuthenticationPrincipal Account account) {
//        Long partnerId = account.getId();
//        return ResponseEntity.ok(inspectionService.getInspectionImagesById(id, partnerId));
//    }

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
