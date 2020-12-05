package com.metanonia.web3jtest.run;

import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
public class WalletLoadSample {
    public static void main(String[] args) {
        String passwd = "password!!";
        String walletPath = "./UTC--2020-12-05T14-06-20.57564000Z--baf2dd6d00f968ff14bfe4c446a8294251b20863.json";
        String address = "";

        try {
            Credentials credentials = WalletUtils.loadCredentials(passwd, walletPath);
            address = credentials.getAddress();
            System.out.println("Address:" + address);
            System.out.println("PrivateKey: 0x" + credentials.getEcKeyPair().getPrivateKey().toString(16));
            System.out.println("PublicKey: 0x" + credentials.getEcKeyPair().getPublicKey().toString(16));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }

        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));
        try {
            BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
            System.out.println("Balance:"+ balance.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
