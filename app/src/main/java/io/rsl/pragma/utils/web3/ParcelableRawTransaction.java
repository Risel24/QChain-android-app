package io.rsl.pragma.utils.web3;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.tx.RawTransactionManager;
import org.web3j.utils.Numeric;

public class ParcelableRawTransaction implements Parcelable {

    private BigInteger nonce;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private String to;
    private BigInteger value;
    private String data;

    private ParcelableRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data) {
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.to = to;
        this.value = value;

        if (data != null) {
            this.data = Numeric.cleanHexPrefix(data);
        }
    }

    public static ParcelableRawTransaction createContractTransaction(
            BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, BigInteger value,
            String init) {

        return new ParcelableRawTransaction(nonce, gasPrice, gasLimit, "", value, init);
    }

    public static ParcelableRawTransaction createEtherTransaction(
            BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value) {

        return new ParcelableRawTransaction(nonce, gasPrice, gasLimit, to, value, "");

    }

    public static ParcelableRawTransaction createTransaction(
            BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, String data) {
        return createTransaction(nonce, gasPrice, gasLimit, to, BigInteger.ZERO, data);
    }

    public static ParcelableRawTransaction createTransaction(
            BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data) {

        return new ParcelableRawTransaction(nonce, gasPrice, gasLimit, to, value, data);
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getValue() {
        return value;
    }

    public String getData() {
        return data;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public Transaction getTransaction(String from) {
        return Transaction.createContractTransaction(from, nonce, gasPrice, gasLimit, value, data);
    }

    private ParcelableRawTransaction(Parcel in) {
        nonce = (BigInteger) in.readValue(BigInteger.class.getClassLoader());
        gasPrice = (BigInteger) in.readValue(BigInteger.class.getClassLoader());
        gasLimit = (BigInteger) in.readValue(BigInteger.class.getClassLoader());
        to = in.readString();
        value = (BigInteger) in.readValue(BigInteger.class.getClassLoader());
        data = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(nonce);
        dest.writeValue(gasPrice);
        dest.writeValue(gasLimit);
        dest.writeString(to);
        dest.writeValue(value);
        dest.writeString(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ParcelableRawTransaction> CREATOR = new Parcelable.Creator<ParcelableRawTransaction>() {
        @Override
        public ParcelableRawTransaction createFromParcel(Parcel in) {
            return new ParcelableRawTransaction(in);
        }

        @Override
        public ParcelableRawTransaction[] newArray(int size) {
            return new ParcelableRawTransaction[size];
        }
    };
}