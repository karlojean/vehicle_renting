package dev.jeankarlo.vehiclerenting.controller;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.inspectionImage.InspectionImageRespondeDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.InspectionImage;
import dev.jeankarlo.vehiclerenting.service.InspectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @PostMapping
    public void initInspection(
            @RequestBody @Valid InspectionInitDTO inspectionInitDTO,
            @AuthenticationPrincipal Account account
            ) {
        Long ownerId = account.getId();
        inspectionService.initInspection(inspectionInitDTO, ownerId);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<InspectionImageRespondeDTO> uploadInspectionImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Account account) {
        Long ownerId = account.getId();
        return ResponseEntity.ok(inspectionService.uploadInspectionImage(id, file, ownerId));
    }
}
