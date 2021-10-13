package com.example.service;

import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import com.example.grpc.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse response = HelloResponse.newBuilder()
                .setGreeting(String.format("Hello, %s %s", request.getFirstName(), request.getLastName()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}
