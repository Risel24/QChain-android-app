package io.rsl.pragma.db.dbmodels;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

@Entity
public class DBContract implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @Nullable private String address;
    @Nullable private String initHash;
    @Nullable private String ownerAddress;
    private int     type;
    private String  title;
    private String  data;
    private int     status;
    private String  remoteId;

    public DBContract(@Nullable String address, @Nullable String initHash, @Nullable String ownerAddress, int type, String data, int status, String remoteId) {
        this.address = address;
        this.initHash = initHash;
        this.ownerAddress = ownerAddress;
        this.type = type;
        this.data = data;
        this.status = status;
        this.remoteId = remoteId;
    }

    @Ignore
    public DBContract(int type, int status, String title, String data) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public String getInitHash() {
        return initHash;
    }

    public void setInitHash(@Nullable String initHash) {
        this.initHash = initHash;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    protected DBContract(Parcel in) {
        address = in.readString();
        initHash = in.readString();
        ownerAddress = in.readString();
        type = in.readInt();
        title = in.readString();
        data = in.readString();
        status = in.readInt();
        remoteId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(initHash);
        dest.writeString(ownerAddress);
        dest.writeInt(type);
        dest.writeString(title);
        dest.writeString(data);
        dest.writeInt(status);
        dest.writeString(remoteId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DBContract> CREATOR = new Parcelable.Creator<DBContract>() {
        @Override
        public DBContract createFromParcel(Parcel in) {
            return new DBContract(in);
        }

        @Override
        public DBContract[] newArray(int size) {
            return new DBContract[size];
        }
    };
}