package io.rsl.pragma.screens.poll.eth;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

import io.rsl.pragma.utils.web3.ParcelableRawTransaction;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.3.1.
 */
public class Ballot extends Contract {
    public static final String BINARY = "6060604052341561000f57600080fd5b6040516107733803806107738339810160405280805160008054600160a060020a03191633600160a060020a039081169190911780835516815260016020819052604082205592019190505b81518110156100d057600280546001810161007683826100d7565b91600052602060002090600202016000604080519081016040528086868151811061009d57fe5b906020019060200201518152600060209091015291905081518155602082015160019182015592909201915061005b9050565b505061012f565b815481835581811511610103576002028160020283600052602060002091820191016101039190610108565b505050565b61012c91905b80821115610128576000808255600182015560020161010e565b5090565b90565b6106358061013e6000396000f30060606040526004361061008d5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630121b93f8114610092578063013cf08b146100aa5780632e4176cf146100d85780635c19a95c14610114578063609ff1bd146101405780639e7b8d6114610165578063a3ec138d14610191578063e2ba53f0146101ff575b600080fd5b341561009d57600080fd5b6100a8600435610212565b005b34156100b557600080fd5b6100c0600435610292565b60405191825260208201526040908101905180910390f35b34156100e357600080fd5b6100eb6102be565b60405173ffffffffffffffffffffffffffffffffffffffff909116815260200160405180910390f35b341561011f57600080fd5b6100a873ffffffffffffffffffffffffffffffffffffffff600435166102da565b341561014b57600080fd5b610153610478565b60405190815260200160405180910390f35b341561017057600080fd5b6100a873ffffffffffffffffffffffffffffffffffffffff600435166104e2565b341561019c57600080fd5b6101bd73ffffffffffffffffffffffffffffffffffffffff6004351661059b565b604051938452911515602084015273ffffffffffffffffffffffffffffffffffffffff1660408084019190915260608301919091526080909101905180910390f35b341561020a57600080fd5b6101536105dc565b73ffffffffffffffffffffffffffffffffffffffff331660009081526001602081905260409091209081015460ff161561024b57600080fd5b6001818101805460ff191690911790556002808201839055815481549091908490811061027457fe5b60009182526020909120600160029092020101805490910190555050565b60028054829081106102a057fe5b60009182526020909120600290910201805460019091015490915082565b60005473ffffffffffffffffffffffffffffffffffffffff1681565b73ffffffffffffffffffffffffffffffffffffffff3316600090815260016020819052604082209081015490919060ff161561031557600080fd5b3373ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415151561035057600080fd5b73ffffffffffffffffffffffffffffffffffffffff83811660009081526001602081905260409091200154610100900416156103cc5773ffffffffffffffffffffffffffffffffffffffff92831660009081526001602081905260409091200154610100900483169233168314156103c757600080fd5b610350565b506001818101805460ff191682177fffffffffffffffffffffff0000000000000000000000000000000000000000ff1661010073ffffffffffffffffffffffffffffffffffffffff86169081029190911790915560009081526020829052604090209081015460ff161561046b5781546002828101548154811061044c57fe5b6000918252602090912060016002909202010180549091019055610473565b815481540181555b505050565b600080805b6002548110156104dd578160028281548110151561049757fe5b90600052602060002090600202016001015411156104d55760028054829081106104bd57fe5b90600052602060002090600202016001015491508092505b60010161047d565b505090565b6000543373ffffffffffffffffffffffffffffffffffffffff9081169116148015610537575073ffffffffffffffffffffffffffffffffffffffff81166000908152600160208190526040909120015460ff16155b8015610566575073ffffffffffffffffffffffffffffffffffffffff8116600090815260016020526040902054155b151561057157600080fd5b73ffffffffffffffffffffffffffffffffffffffff16600090815260016020819052604090912055565b600160208190526000918252604090912080549181015460029091015460ff821691610100900473ffffffffffffffffffffffffffffffffffffffff169084565b600060026105e8610478565b815481106105f257fe5b9060005260206000209060020201600001549050905600a165627a7a72305820f0890d80742eeb7d2ec32fe63b9ffdb09eb4a46705d84b2192be66b6f44719a80029";

    public Ballot(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public Ballot(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> vote(BigInteger proposal) {
        final Function function = new Function(
                "vote", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(proposal)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple2<byte[], BigInteger>> proposals(BigInteger param0) {
        final Function function = new Function("proposals", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<byte[], BigInteger>>(
                new Callable<Tuple2<byte[], BigInteger>>() {
                    @Override
                    public Tuple2<byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<String> chairperson() {
        final Function function = new Function("chairperson", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> delegate(final Function function) {
        return executeRemoteCallTransaction(function);
    }

    public String delegateFun(String to) {
        final Function function = new Function(
                "delegate",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to)),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }

    public RemoteCall<BigInteger> winningProposal() {
        final Function function = new Function("winningProposal", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> giveRightToVote(String voter) {
        final Function function = new Function(
                "giveRightToVote", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(voter)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<BigInteger, Boolean, String, BigInteger>> voters(String param0) {
        final Function function = new Function("voters", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple4<BigInteger, Boolean, String, BigInteger>>(
                new Callable<Tuple4<BigInteger, Boolean, String, BigInteger>>() {
                    @Override
                    public Tuple4<BigInteger, Boolean, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, Boolean, String, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<byte[]> winnerName() {
        final Function function = new Function("winnerName", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public static RemoteCall<Ballot> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, List<byte[]> proposalNames) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(proposalNames, org.web3j.abi.datatypes.generated.Bytes32.class))));
        return deployRemoteCall(Ballot.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<Ballot> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, List<byte[]> proposalNames) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(proposalNames, org.web3j.abi.datatypes.generated.Bytes32.class))));
        return deployRemoteCall(Ballot.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static ParcelableRawTransaction getRawDeploy(List<byte[]> proposalNames) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Collections.singletonList(new DynamicArray<>(Utils.typeMap(proposalNames, Bytes32.class))));
        return ParcelableRawTransaction.createTransaction(null, null, Contract.GAS_LIMIT, null, BigInteger.ZERO, Ballot.BINARY + encodedConstructor);
    }

    public static Ballot load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Ballot(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Ballot load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Ballot(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
