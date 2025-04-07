package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.product.PricingGroup;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.pricingGroup.PricingGroupResponseDTO;
import com.laresencanto.laresencantorestapi.repository.PricingGroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PricingGroupService {

    private final PricingGroupRepository pricingGroupRepository;

    public PricingGroupService(PricingGroupRepository pricingGroupRepository) {
        this.pricingGroupRepository = pricingGroupRepository;
    }

    public ResponseDTO<PricingGroupResponseDTO> getAllPricingGroups() {
        List<PricingGroup> pricingGroups = pricingGroupRepository.findAll();
        List<PricingGroupResponseDTO> pricingGroupResponseDTOS = new ArrayList<>();

        for (PricingGroup pricingGroup : pricingGroups) {
            pricingGroupResponseDTOS.add(new PricingGroupResponseDTO(pricingGroup.getId(), pricingGroup.getName(), pricingGroup.getProfitMargin()));
        }

        return new ResponseDTO<>(
                String.valueOf(HttpStatus.OK.value()),
                "Lista de grupo de precificação retornados com sucesso",
                pricingGroupResponseDTOS
        );
    }
}
