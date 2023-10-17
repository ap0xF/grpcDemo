package com.aadit.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(6565)
                .addService(new BankService()) /* BankService has been added to the server, meaning BankService will keep running from the server
                 It is the class that is being extended from BankServiceGrpc which is the java class created by the protoc under generated*/
                .build();

        server.start();
        System.out.println("Server Started");
        server.awaitTermination(); // keeps running the server until stopped manually
    }
}
