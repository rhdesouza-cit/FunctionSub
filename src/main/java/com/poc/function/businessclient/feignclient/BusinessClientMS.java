package com.poc.function.businessclient.feignclient;

import com.poc.function.businessclient.dto.BusinessClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "bullla-ms-core-business-client", url = "${br.com.bullla.business-client.url}")
public interface BusinessClientMS {

    @GetMapping(value = "/v1/business_clients/{id}")
    BusinessClientDTO getContractByBusinessClient(@PathVariable("id") Long id);
}