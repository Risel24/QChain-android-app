package io.rsl.pragma.screens.poll.eth;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.response.NoOpProcessor;

import io.rsl.pragma.screens.poll.models.core.CorePollModel;
import io.rsl.pragma.repositories.CredentialsRepository;

public class EthPollManager {

    private CorePollModel poll;

    private CredentialsRepository credentialsRepository;

    private Web3j web3;


    public EthPollManager(CorePollModel poll) {
        this.poll = poll;

        HttpService httpService = new HttpService();

        Credentials credentials = Credentials.create("");
        credentials.getAddress();

        FastRawTransactionManager fastRawTransactionManager = new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3));

        web3 = Web3jFactory.build(httpService);


    }
/*
    public void deploy(OnDeployListener onDeployListener) {

        Ballot ballot = Ballot.deploy();
        web3.ethSendTransaction(Transaction.createContractTransaction())

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();

        RawTransaction.createContractTransaction()

        try {
            ballot.delegate("tx").sendAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }*/

    public interface OnDeployListener {
        void onDeploy(TransactionReceipt transactionReceipt);
    }
}
