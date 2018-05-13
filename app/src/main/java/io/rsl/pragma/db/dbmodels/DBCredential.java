package io.rsl.pragma.db.dbmodels;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity
public class DBCredential implements Parcelable {

    @PrimaryKey
    @NonNull
    private String address;

    private String publicKey;
    private String privateKey;

    public DBCredential(@NonNull String address, String publicKey, String privateKey) {
        this.address = address;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Ignore
    public DBCredential(@NonNull String address) {
        this.address = address;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isPrivateNotImported() {
        return privateKey == null || publicKey == null;
    }

    protected DBCredential(Parcel in) {
        address = in.readString();
        privateKey = in.readString();
        publicKey = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(privateKey);
        dest.writeString(publicKey);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DBCredential> CREATOR = new Parcelable.Creator<DBCredential>() {
        @Override
        public DBCredential createFromParcel(Parcel in) {
            return new DBCredential(in);
        }

        @Override
        public DBCredential[] newArray(int size) {
            return new DBCredential[size];
        }
    };
}