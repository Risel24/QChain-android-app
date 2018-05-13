package io.rsl.pragma.di.modules;

import android.arch.persistence.room.Room;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.rsl.pragma.db.AppDatabase;
import io.rsl.pragma.db.dao.ContractsDao;
import io.rsl.pragma.db.dao.CredentialsDao;
import io.rsl.pragma.db.dao.TransactionsDao;
import io.rsl.pragma.db.dao.UserDao;
import io.rsl.pragma.di.scopes.AppScope;

@Module(includes = ContextModule.class)
public class DatabaseModule {

    private static final String DATABASE_NAME = "room_data";

    @AppScope
    @Provides
    public AppDatabase appDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
    }

    @AppScope
    @Provides
    public ContractsDao contractsDao(AppDatabase appDatabase) {
        return appDatabase.getContractsDao();
    }

    @AppScope
    @Provides
    public CredentialsDao credentialsDao(AppDatabase appDatabase) {
        return appDatabase.getCredentialsDao();
    }

    @AppScope
    @Provides
    public TransactionsDao transactionsDao(AppDatabase appDatabase) {
        return appDatabase.getTransactionsDao();
    }

    @AppScope
    @Provides
    public UserDao userDao(AppDatabase appDatabase) {
        return appDatabase.getUserDao();
    }
}