package com.example.backend.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Service
public class LambdaInvocationService {

    private final LambdaClient lambdaClient;

    public LambdaInvocationService() {
        this.lambdaClient = LambdaClient.builder()
                .region(Region.US_EAST_1) // change if different region
                .build();
    }

    public String invoke(String functionName, String payload) {

        InvokeRequest request = InvokeRequest.builder()
                .functionName(functionName)
                .payload(software.amazon.awssdk.core.SdkBytes.fromUtf8String(payload))
                .build();

        InvokeResponse response = lambdaClient.invoke(request);

        return response.payload().asUtf8String();
    }
}
