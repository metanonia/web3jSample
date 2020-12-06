package com.metanonia.web3jSample.run;

import com.metanonia.web3jSample.contracts.HelloWorld;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ContractsSample {
    public static void main(String[] args) {
        String passwd = "password!!";
        String walletPath = "./UTC--2020-12-05T14-06-20.57564000Z--baf2dd6d00f968ff14bfe4c446a8294251b20863.json";
        String adminContract = null;
        String walletContract = null;

        Admin admin = Admin.build(new HttpService("http://localhost:7545"));
        // Deploy
        try {
            PersonalListAccounts accounts = admin.personalListAccounts().send();
            if(accounts != null) {
                List<String> accountIds = accounts.getAccountIds();
                String from = accountIds.get(0);
                PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(accountIds.get(0), "").send();
                if(personalUnlockAccount.accountUnlocked()) {
                    BigInteger value = Convert.toWei("0", Convert.Unit.ETHER).toBigInteger();
                    BigInteger nonce = admin.ethGetTransactionCount(accountIds.get(0), DefaultBlockParameterName.LATEST)
                            .send().getTransactionCount();
                    BigInteger gasPrice = admin.ethGasPrice().send().getGasPrice();
                    BigInteger gasLimit = admin.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true)
                            .send().getBlock().getGasLimit();
                    String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String("metanonia")));
                    Transaction transaction = Transaction.createContractTransaction(from, nonce, gasPrice, gasLimit, value, HelloWorld.BINARY+encodedConstructor);

                    String hash = admin.personalSendTransaction(transaction, "").send().getTransactionHash();
                    EthGetTransactionReceipt receipt = admin.ethGetTransactionReceipt(hash).send();
                    if (receipt.getTransactionReceipt().isPresent()) {
                        adminContract = receipt.getResult().getContractAddress();
                        System.out.println("Contract Address = " + adminContract);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));
        try {
            Credentials credentials = WalletUtils.loadCredentials(passwd, walletPath);
            String from = credentials.getAddress();
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            BigInteger gasLimit = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true)
                    .send().getBlock().getGasLimit();
            StaticGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
            walletContract = HelloWorld.deploy(web3j, credentials, gasProvider, "Metanonia").send().getContractAddress();
            System.out.println("Wallet Contract Address = " + walletContract);

            // Load
            HelloWorld hello = HelloWorld.load(adminContract, web3j, credentials, gasProvider);
            String greeting = hello.greeting().send();
            System.out.println("greeting = "+ greeting);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
