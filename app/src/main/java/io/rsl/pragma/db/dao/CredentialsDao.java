package io.rsl.pragma.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.rsl.pragma.db.dbmodels.DBCredential;

@Dao
public interface CredentialsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(DBCredential... credentials);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(DBCredential credentials);

    @Delete
    void delete(DBCredential dbCredential);

    @Query("SELECT * FROM DBCredential")
    List<DBCredential> getAllCredentials();

    @Query("SELECT * FROM DBCredential WHERE address = :address")
    DBCredential getCredentials(String address);
}