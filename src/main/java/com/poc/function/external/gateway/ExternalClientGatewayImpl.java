package com.poc.function.external.gateway;

import com.poc.function.external.feignclient.ExternalClientMS;
import org.springframework.stereotype.Component;

@Component
public class ExternalClientGatewayImpl implements ExternalClientGateway {

    private final ExternalClientMS externalClientMS;

    public ExternalClientGatewayImpl(ExternalClientMS externalClientMS) {
        this.externalClientMS = externalClientMS;
    }

    @Override
    public void postMessage(final String message) {
        externalClientMS.postMessageToMs(message);
    }

}
