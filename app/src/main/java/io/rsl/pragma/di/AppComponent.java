package io.rsl.pragma.di;

import dagger.Component;
import io.rsl.pragma.api.QService;
import io.rsl.pragma.di.modules.QServiceModule;
import io.rsl.pragma.di.modules.RepositoryModule;
import io.rsl.pragma.di.modules.Web3Module;
import io.rsl.pragma.di.scopes.AppScope;
import io.rsl.pragma.screens.contracts.ContractsViewModel;
import io.rsl.pragma.screens.history.HistoryViewModel;
import io.rsl.pragma.screens.login.LoginActivity;
import io.rsl.pragma.screens.poll.PollCreateFragment;
import io.rsl.pragma.screens.process.ProcessingViewModel;
import io.rsl.pragma.screens.txdispatch.CredentialsArrayAdapter;
import io.rsl.pragma.screens.txdispatch.DispatchViewModel;
import io.rsl.pragma.screens.wallets.CredentialsViewModel;
import io.rsl.pragma.screens.wallets.create.CredentialImportViewModel;

@AppScope
@Component(modules = {QServiceModule.class, Web3Module.class, RepositoryModule.class})
public interface AppComponent {

    void inject(LoginActivity loginActivity);

    void inject(PollCreateFragment pollCreateFragment);

    void inject(ContractsViewModel contractsViewModel);
    void inject(CredentialsViewModel credentialsViewModel);
    void inject(CredentialImportViewModel credentialImportViewModel);
    void inject(DispatchViewModel dispatchViewModel);
    void inject(ProcessingViewModel processingViewModel);
    void inject(HistoryViewModel historyViewModel);
}