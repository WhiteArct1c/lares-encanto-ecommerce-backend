package com.laresencanto.laresencantorestapi.strategy.impl.address;

import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidateRequiredData implements IStrategy<AddressRequestDTO> {
    @Override
    public String validate(AddressRequestDTO data) {
        return null;
    }
}
