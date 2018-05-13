package io.rsl.pragma.di.modules;

import dagger.Module;
import dagger.Provides;
import io.rsl.pragma.api.QService;
import io.rsl.pragma.db.dao.ContractsDao;
import io.rsl.pragma.db.dao.CredentialsDao;
import io.rsl.pragma.db.dao.TransactionsDao;
import io.rsl.pragma.db.dao.UserDao;
import io.rsl.pragma.di.scopes.AppScope;
import io.rsl.pragma.repositories.ContractsRepository;
import io.rsl.pragma.repositories.CredentialsRepository;
import io.rsl.pragma.repositories.TransactionsRepository;
import io.rsl.pragma.repositories.UserRepository;

@Module(includes = {QServiceModule.class, DatabaseModule.class})
public class RepositoryModule {

    @AppScope
    @Provides
    public ContractsRepository contractsRepository(ContractsDao contractsDao, QService qService, CredentialsRepository credentialsRepository) {
        return new ContractsRepository(contractsDao, qService, credentialsRepository);
    }

    @AppScope
    @Provides
    public CredentialsRepository credentialsRepository(CredentialsDao credentialsDao) {
        return new CredentialsRepository(credentialsDao);
    }

    @AppScope
    @Provides
    public TransactionsRepository transactionsRepository(TransactionsDao transactionsDao) {
        return new TransactionsRepository(transactionsDao);
    }

    @AppScope
    @Provides
    public UserRepository userRepository(UserDao userDao) {
        return new UserRepository(userDao);
    }
}