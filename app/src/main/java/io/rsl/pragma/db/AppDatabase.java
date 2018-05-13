package io.rsl.pragma.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import io.rsl.pragma.db.dao.ContractsDao;
import io.rsl.pragma.db.dao.CredentialsDao;
import io.rsl.pragma.db.dao.TransactionsDao;
import io.rsl.pragma.db.dao.UserDao;
import io.rsl.pragma.db.dbmodels.DBContract;
import io.rsl.pragma.db.dbmodels.DBCredential;
import io.rsl.pragma.db.dbmodels.DBTransaction;
import io.rsl.pragma.db.dbmodels.DBUser;

@Database(entities = {DBCredential.class, DBContract.class, DBTransaction.class, DBUser.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CredentialsDao getCredentialsDao();
    public abstract ContractsDao getContractsDao();
    public abstract TransactionsDao getTransactionsDao();
    public abstract UserDao getUserDao();
}