package io.rsl.pragma.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.rsl.pragma.db.dbmodels.DBContract;

@Dao
public interface ContractsDao {

    @Insert
    void insertAll(DBContract... contracts);

    @Insert
    long insert(DBContract dbContract);

    @Delete
    void delete(DBContract dbContract);

    @Query("SELECT * FROM dbcontract")
    List<DBContract> getAllContracts();

    @Query("SELECT * FROM dbcontract WHERE address = :addressOrHash OR initHash = :addressOrHash")
    DBContract getContract(String addressOrHash);

    @Query("UPDATE dbcontract SET initHash = :hash, ownerAddress = :ownerAddress, status = :status WHERE id = :id")
    void setHash(long id, int status, String hash, String ownerAddress);

    @Query("UPDATE dbcontract SET address = :address, status = :status WHERE initHash = :hash")
    void setReceipt(String hash, int status, String address);
}