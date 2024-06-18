package com.poc.function.businessclient.gateway;


import com.poc.function.businessclient.dto.BusinessClientDTO;

import java.util.Optional;

public interface BusinessClientGateway {
    Optional<BusinessClientDTO> getBusinessClientById(Long id);
}
