package com.metanonia.web3jSample.run;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;


public class Web3jAccountsSample {
    public static void main(String[] args) {
        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));
        try {
            EthAccounts accounts = web3j.ethAccounts().send();
            if(accounts != null) {
                List<String> accountList = accounts.getAccounts();
                for(String account:accountList) {
                    System.out.println(account);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Admin admin = Admin.build(new HttpService("http://localhost:7545"));
        try {
            PersonalListAccounts accounts = admin.personalListAccounts().send();
            if(accounts != null) {
                List<String> accountIds = accounts.getAccountIds();
                for(String account: accountIds) {
                    System.out.println(account);
                }
                PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(accountIds.get(0), "").send();
                Boolean check = personalUnlockAccount.accountUnlocked();
                System.out.println("Unlock Result:" + check.toString());
                if(check) {
                    String to = "0xbaf2dd6d00f968ff14bfe4c446a8294251b20863";
                    BigInteger value = Convert.toWei("15", Convert.Unit.ETHER).toBigInteger();
                    BigInteger nonce = admin.ethGetTransactionCount(accountIds.get(0), DefaultBlockParameterName.LATEST)
                            .send().getTransactionCount();
                    BigInteger gasPrice = admin.ethGasPrice().send().getGasPrice();
                    BigInteger gasLimit = admin.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true)
                            .send().getBlock().getGasLimit();
                    Transaction transaction = Transaction.createEtherTransaction(accountIds.get(0), nonce, gasPrice, gasLimit, to, value);
                    String txHash = admin.personalSendTransaction(transaction, "").send().getTransactionHash();
                    System.out.println(txHash);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
