package com.poc.function.external.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "external-client", url = "${br.com.external.url}")
public interface ExternalClientMS {

    @PostMapping("/received")
    void postMessageToMs(String message);

}
