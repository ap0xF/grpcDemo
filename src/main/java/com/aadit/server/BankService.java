package com.aadit.server;

import com.aadit.models.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.aadit.server.AccountDatabase;
public class BankService extends BankServiceGrpc.BankServiceImplBase {

    // for unary operation
    @Override
    public void getBalance(BalacneCheckRequest request, StreamObserver<Balance> responseObserver) {
        int accountNumber = request.getAccountNumber();
        Balance balance = Balance.newBuilder()
                .setAmount(AccountDatabase.getBalanceFromDB(accountNumber)) // get the balance that is present in DB.
                .build();

        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

//     for server streaming
    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        int accountNumber = request.getAccountNumber();
        int amount = request.getAmount();
        float availableBalance = AccountDatabase.getBalanceFromDB(accountNumber);
        if((availableBalance >= amount) && amount % 10 == 0 ){
            for(int i = 0; i < (amount/10); i ++ ){
                Money money = Money.newBuilder()
                                .setValue(10)
                                .build();
                responseObserver.onNext(money);
                AccountDatabase.deductBalanceToDB(accountNumber, 10);
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    System.out.println("exception");
                }
            }
        } else if(availableBalance < amount){
            Status status = Status.FAILED_PRECONDITION.withDescription("Not enough balance to withdraw\n Available Balance: " + availableBalance);
            responseObserver.onError(status.asRuntimeException()); // .asRunTimeException makes status throwable.
        } else if (amount % 10 != 0) {
            Status status = Status.FAILED_PRECONDITION.withDescription("Please enter the amount in multiple of 10");
            responseObserver.onError(status.asRuntimeException());
        } else{
            Status status = Status.UNKNOWN.withDescription("Unknown Error Occured Please Contact Bank For Further Support");
            responseObserver.onError(status.asRuntimeException());
        }
        responseObserver.onCompleted();
    }


//     for client side streaming
    @Override
    public StreamObserver<DepositRequest> deposit(final StreamObserver<Money> responseObserver){
        return new StreamObserver<DepositRequest>() {
            int accountNumber;
            boolean getAccountNumber = false;
            int amount;
            int totalAmount = 0;
            @Override
            public void onNext(DepositRequest depositRequest) {
                accountNumber = depositRequest.getAccountNumber();
                amount = depositRequest.getAmount();
                getAccountNumber = AccountDatabase.Map.containsKey(accountNumber);
                if(getAccountNumber){
                    AccountDatabase.addBalanceToDB(accountNumber, amount);
                    totalAmount += amount;
                } else {
                    responseObserver.onError(Status.NOT_FOUND.withDescription("Account Number " + accountNumber + " Does not exist").asRuntimeException());
                    responseObserver.onCompleted();
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                Money money = Money.newBuilder().setValue(totalAmount).build();
                responseObserver.onNext(money);
                responseObserver.onCompleted();
            }
        };
    }


    // bidirectional Streaming
    @Override
    public StreamObserver<DepositRequest> depositBiDir(final StreamObserver<DepositResponse> depositResponseStreamObserver){
        return new StreamObserver<DepositRequest>() {
            int accountNumber;
            boolean getAccountNumber = false;
            int amount;
            @Override
            public void onNext(DepositRequest value) {
                accountNumber = value.getAccountNumber();
                amount = value.getAmount();
                getAccountNumber = AccountDatabase.Map.containsKey(accountNumber);

                if(getAccountNumber){

                    AccountDatabase.addBalanceToDB(accountNumber, amount);
                    DepositResponse depositResponse = DepositResponse.newBuilder()
                            .setAccountNumber(accountNumber)
                            .setAmount(AccountDatabase.getBalanceFromDB(accountNumber))
                            .setDepositStatusCheck("Deposit Complete")
                            .build();
                    depositResponseStreamObserver.onNext(depositResponse);
                    System.out.println(AccountDatabase.getBalanceFromDB(accountNumber));
                }
                else{
                    depositResponseStreamObserver.onError(Status.NOT_FOUND.withDescription("Account Number " + accountNumber + " Does not exist").asRuntimeException());
                }
            }

            @Override
            public void onError(Throwable t) {
                depositResponseStreamObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                depositResponseStreamObserver.onCompleted();
            }
        };
    }


}

