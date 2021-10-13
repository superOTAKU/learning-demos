package com.example;

import com.example.service.HelloServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ServerBootstrap {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(8081)
                .addService(new HelloServiceImpl())
                .build();
        server.start();
        System.out.printf("server listen at %s", 8081);
        server.awaitTermination();
    }
}
