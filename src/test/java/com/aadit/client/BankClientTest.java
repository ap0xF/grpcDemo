package com.aadit.client;

import com.aadit.models.BalacneCheckRequest;
import com.aadit.models.Balance;
import com.aadit.models.BankServiceGrpc;
import com.aadit.models.WithdrawRequest;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) //
// if this is not added it will ask to create static variable and methods

public class BankClientTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;

    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup(){
        // will be discussed in the next section.
        ManagedChannel managedChannel= ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

       this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
       this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void balanceTest(){
        BalacneCheckRequest balacneCheckRequest = BalacneCheckRequest.newBuilder()
                .setAccountNumber(3)
                .build();
        Balance balance = this.blockingStub.getBalance(balacneCheckRequest);
        System.out.println(
                "Received: " + balance.getAmount()
        );
    }

//    @Test
//    public void withdrawTest(){
//         WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
//                .setAccountNumber(2)
//                .setAmount(10)
//                .build();
//        this.blockingStub.withdraw(withdrawRequest)
//                .forEachRemaining(money -> System.out.println("Received " + money.getValue()));
//    }

    @Test
    public void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(2)
                .setAmount(20)
                .build();

        this.bankServiceStub.withdraw(withdrawRequest, new MoneyStreamingResponse(latch));
        latch.await();
    }
}
