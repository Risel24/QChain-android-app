package io.rsl.pragma.utils.web3;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.Contract;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.rsl.pragma.screens.poll.eth.Ballot;

public class Web3Manager {

    private Map<String, Contract> contracts = new HashMap<>();


    private QFastRawTransactionManager transactionManager;

    public Web3Manager() {
    }

    public ParcelableRawTransaction getRawFromDeploy(List<byte[]> proposalNames) {

        ParcelableRawTransaction rawTransaction = null;

        BigInteger gasPrice = null;//web3.ethGasPrice().sendAsync().get().getGasPrice();
        BigInteger gasLimit = Contract.GAS_LIMIT;

        List<Bytes32> bytes32List = new ArrayList<>();
        for (byte[] b : proposalNames) {
            bytes32List.add(new Bytes32(b));
        }

        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new DynamicArray<Bytes32>(bytes32List)));

        rawTransaction = ParcelableRawTransaction.createTransaction(null, gasPrice, gasLimit, null, BigInteger.ZERO, Ballot.BINARY + encodedConstructor);

/*
        EthSendTransaction ethSendTransaction = transactionManager.signAndSend(rawTransaction);
        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(web3, 5000, 12);
        TransactionReceipt receipt = transactionReceiptProcessor.waitForTransactionReceipt(ethSendTransaction.getTransactionHash());

        Contract ballot = new Ballot(receipt.getContractAddress(), web3, transactionManager, gasPrice, gasLimit);
        ballot.setTransactionReceipt(receipt);*/

        return rawTransaction;
    }

    public void getContract(String address, int type) {
        if(address == null) {
            Contract contract;
            switch (type) {
                case 1:

                    break;
                default:
                    break;
            }
        }
    }

}
