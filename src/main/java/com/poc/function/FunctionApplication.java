package com.poc.function;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.AcknowledgeRequest;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
import com.poc.function.external.gateway.ExternalClientGateway;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.catalog.FunctionTypeUtils;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SpringBootApplication
@EnableFeignClients
public class FunctionApplication implements ApplicationContextInitializer<GenericApplicationContext> {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.pubsub.emulator-host}")
    private String emulatorHost;

    private static final Log log = LogFactory.getLog(FunctionApplication.class);

    private final ExternalClientGateway externalClientGateway;

    FunctionApplication(ExternalClientGateway externalClientGateway) {
        this.externalClientGateway = externalClientGateway;
    }

    public static void main(String[] args) {
        SpringApplication.run(FunctionApplication.class, args);
    }


    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean("demo", FunctionRegistration.class,
            () -> new FunctionRegistration<>(consumer())
                .type(FunctionTypeUtils.discoverFunctionTypeFromClass(String.class)));
    }

    @Bean
    public Consumer<String> consumer() {
        return subscription -> {
            log.info("subscription: " + subscription);
            try {
                subscribeSyncExample(subscription, 1000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public void subscribeSyncExample(String subscriptionId, Integer numOfMessages) throws IOException {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(emulatorHost).usePlaintext().build();
        TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
        CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

        SubscriberStubSettings subscriberStubSettings = SubscriberStubSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(credentialsProvider)
            .build();

        try (SubscriberStub subscriber = GrpcSubscriberStub.create(subscriberStubSettings)) {
            String subscriptionName = ProjectSubscriptionName.format(projectId, subscriptionId);
            PullRequest pullRequest = PullRequest.newBuilder()
                .setMaxMessages(numOfMessages)
                .setSubscription(subscriptionName)
                .setReturnImmediately(true)
                .build();

            // Use pullCallable().futureCall to asynchronously perform this operation.
            PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);

            List<String> ackIds = new ArrayList<>();
            for (ReceivedMessage message : pullResponse.getReceivedMessagesList()) {
                externalClientGateway.postMessage(message.getMessage().getData().toStringUtf8());
                ackIds.add(message.getAckId());
            }

            if (!ackIds.isEmpty()) {
                // Acknowledge received messages.
                AcknowledgeRequest acknowledgeRequest = AcknowledgeRequest.newBuilder()
                    .setSubscription(subscriptionName)
                    .addAllAckIds(ackIds)
                    .build();
                // Use acknowledgeCallable().futureCall to asynchronously perform this operation.
                subscriber.acknowledgeCallable().call(acknowledgeRequest);
            }
            log.info("Messages received: " + pullResponse.getReceivedMessagesList());
            log.info("Total messages: " + pullResponse.getReceivedMessagesList().size());
        }
    }

}
