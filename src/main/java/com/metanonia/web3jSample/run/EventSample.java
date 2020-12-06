package com.metanonia.web3jSample.run;

import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.Scanner;

@Slf4j
public class EventSample {
    public static void main(String[] args) {
        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));
        Disposable block = web3j.blockFlowable(false).subscribe(
                ethBlock -> {
                    log.info("got Block:" + ethBlock.getBlock().getHash().toString());
                }
        );
        Disposable tx = web3j.transactionFlowable().subscribe(
                transaction -> {
                    log.info("got Tx:" + transaction.getFrom().toString());
                }
        );
        Scanner sc = new Scanner(System.in);
        sc.next();
        block.dispose();
        tx.dispose();
    }
}
