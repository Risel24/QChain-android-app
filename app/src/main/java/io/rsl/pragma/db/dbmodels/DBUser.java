package io.rsl.pragma.db.dbmodels;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class DBUser {

    @NonNull
    @PrimaryKey
    private String userID;
    private String token;

    public DBUser(@NonNull String userID, String token) {
        this.userID = userID;
        this.token = token;
    }

    @NonNull
    public String getUserID() {
        return userID;
    }

    public void setUserID(@NonNull String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
