package io.rsl.pragma.db.dbmodels;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@Entity
public class DBTransaction implements Parcelable {

    @NonNull
    @PrimaryKey
    private String txHash;

    private String from;
    private String to;
    private String gasUsed;
    private String gasPrice;
    private String blockNum;
    private String error;

    public DBTransaction(@NonNull String txHash, String from, String to, String gasUsed, String gasPrice, String blockNum, String error) {
        this.txHash = txHash;
        this.from = from;
        this.to = to;
        this.gasUsed = gasUsed;
        this.gasPrice = gasPrice;
        this.blockNum = blockNum;
        this.error = error;
    }

    @Ignore
    public DBTransaction(EthSendTransaction ethSendTransaction, BigInteger gasPrice) {
        this.txHash = ethSendTransaction.getTransactionHash();
        this.gasPrice = gasPrice.toString();
        if(ethSendTransaction.hasError()) {
            this.error = ethSendTransaction.getError().getMessage();
        }
    }

    @Ignore
    public DBTransaction(TransactionReceipt receipt) {
        this.txHash = receipt.getTransactionHash();
        this.from = receipt.getFrom();
        this.to = receipt.getTo();
        this.gasUsed = receipt.getGasUsed().toString();
        this.blockNum = receipt.getBlockNumber().toString();
    }

    public boolean isPending() {
        return blockNum == null;
    }

    public boolean hasError() {
        return error != null;
    }

    @NonNull
    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(@NonNull String txHash) {
        this.txHash = txHash;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(String blockNum) {
        this.blockNum = blockNum;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    protected DBTransaction(Parcel in) {
        txHash = in.readString();
        from = in.readString();
        to = in.readString();
        gasUsed = in.readString();
        gasPrice = in.readString();
        blockNum = in.readString();
        error = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(txHash);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(gasUsed);
        dest.writeString(gasPrice);
        dest.writeString(blockNum);
        dest.writeString(error);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DBTransaction> CREATOR = new Parcelable.Creator<DBTransaction>() {
        @Override
        public DBTransaction createFromParcel(Parcel in) {
            return new DBTransaction(in);
        }

        @Override
        public DBTransaction[] newArray(int size) {
            return new DBTransaction[size];
        }
    };
}