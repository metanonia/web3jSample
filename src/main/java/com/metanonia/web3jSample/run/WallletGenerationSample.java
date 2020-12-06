package com.metanonia.web3jSample.run;


import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class WallletGenerationSample {
    public static void main(String[] args) {
        String passwd = "password!!";
        String walletPath = ".";

        try {
            String wallet = WalletUtils.generateNewWalletFile(passwd, new File(walletPath));
            System.out.println("WalletFile:" + wallet);
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }
}
