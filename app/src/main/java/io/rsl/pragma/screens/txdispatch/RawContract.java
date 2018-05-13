package io.rsl.pragma.screens.txdispatch;

import android.os.Parcel;
import android.os.Parcelable;

import io.rsl.pragma.utils.web3.ParcelableRawTransaction;

public class RawContract implements Parcelable {

    private ParcelableRawTransaction parcelableRawTransaction;
    private String jsonData;
    private int type;

    public RawContract(ParcelableRawTransaction parcelableRawTransaction, String jsonData, int type) {
        this.parcelableRawTransaction = parcelableRawTransaction;
        this.jsonData = jsonData;
        this.type = type;
    }

    public ParcelableRawTransaction getParcelableRawTransaction() {
        return parcelableRawTransaction;
    }

    public String getJsonData() {
        return jsonData;
    }

    public int getType() {
        return type;
    }

    protected RawContract(Parcel in) {
        parcelableRawTransaction = (ParcelableRawTransaction) in.readValue(ParcelableRawTransaction.class.getClassLoader());
        jsonData = in.readString();
        type = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(parcelableRawTransaction);
        dest.writeString(jsonData);
        dest.writeInt(type);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RawContract> CREATOR = new Parcelable.Creator<RawContract>() {
        @Override
        public RawContract createFromParcel(Parcel in) {
            return new RawContract(in);
        }

        @Override
        public RawContract[] newArray(int size) {
            return new RawContract[size];
        }
    };
}