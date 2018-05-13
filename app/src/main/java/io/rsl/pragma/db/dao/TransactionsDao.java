package io.rsl.pragma.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.rsl.pragma.db.dbmodels.DBTransaction;

@Dao
public interface TransactionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(DBTransaction... transactions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DBTransaction transaction);

    @Delete
    void delete(DBTransaction transaction);

    @Query("SELECT * FROM dbtransaction")
    List<DBTransaction> getAllTransactions();

    @Query("SELECT * FROM dbtransaction WHERE txHash = :hash")
    DBTransaction getTransaction(String hash);

    @Query("UPDATE dbtransaction SET 'from' = :from, 'to' = :to, gasUsed = :gasUsed, blockNum = :blockNum WHERE txHash = :hash")
    void setReceipt(String hash, String from, String to, String gasUsed, String blockNum);

}
