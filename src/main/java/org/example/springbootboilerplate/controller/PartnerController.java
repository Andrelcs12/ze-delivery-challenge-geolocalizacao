package org.example.springbootboilerplate.controller;

import jakarta.validation.Valid;
import org.example.springbootboilerplate.dto.PartnerRequestDTO;
import org.example.springbootboilerplate.dto.PartnerResponseDTO;
import org.example.springbootboilerplate.service.PartnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @PostMapping
    public ResponseEntity<PartnerResponseDTO> createPartner(@Valid @RequestBody PartnerRequestDTO requestDTO) {
        PartnerResponseDTO responseDTO = partnerService.createPartner(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerResponseDTO> getPartnerById(@PathVariable Long id) {
        PartnerResponseDTO responseDTO = partnerService.getPartnerById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<PartnerResponseDTO> searchNearestPartner(
            @RequestParam("lng") double lng,
            @RequestParam("lat") double lat) {
        PartnerResponseDTO responseDTO = partnerService.searchNearestPartner(lng, lat);
        return ResponseEntity.ok(responseDTO);
    }


}