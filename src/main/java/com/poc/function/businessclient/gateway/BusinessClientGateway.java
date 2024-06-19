package com.poc.function.businessclient.gateway;


import com.poc.function.businessclient.dto.BusinessClientDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

public interface BusinessClientGateway {
    Optional<BusinessClientDTO> getBusinessClientById(Long id);

    void postMessagesBusinessClient(@RequestBody String data);
}
