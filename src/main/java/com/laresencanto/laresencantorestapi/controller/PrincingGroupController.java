package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.pricingGroup.PricingGroupResponseDTO;
import com.laresencanto.laresencantorestapi.service.PricingGroupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pricing-groups")
public class PrincingGroupController {

    private final PricingGroupService pricingGroupService;

    public PrincingGroupController(PricingGroupService pricingGroupService) {
        this.pricingGroupService = pricingGroupService;
    }

    @GetMapping
    public ResponseDTO<PricingGroupResponseDTO> getAllPricingGroups() {
        return pricingGroupService.getAllPricingGroups();
    }
}
