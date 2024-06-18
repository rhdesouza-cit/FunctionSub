package com.poc.function.businessclient.gateway;

import com.poc.function.businessclient.dto.BusinessClientDTO;
import com.poc.function.businessclient.feignclient.BusinessClientMS;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BusinessClientGatewayImpl implements BusinessClientGateway {

    private final BusinessClientMS client;

    BusinessClientGatewayImpl(BusinessClientMS businessClientMS) {
        client = businessClientMS;
    }

    @Override
    public Optional<BusinessClientDTO> getBusinessClientById(final Long id) {
        try {
            return Optional.of(client.getContractByBusinessClient(id));
        } catch (final FeignException.NotFound exception) {
            throw new RuntimeException("Business client id %s not found".formatted(id), null);
        }
    }

}
