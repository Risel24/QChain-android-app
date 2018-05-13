package io.rsl.pragma.di.modules;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

import dagger.Module;
import dagger.Provides;
import io.rsl.pragma.di.scopes.AppScope;
import okhttp3.OkHttpClient;

@Module(includes = NetworkModule.class)
public class Web3Module {

    private static final String ETH_URL = "https://rinkeby.infura.io/fKPIhenrHLO5XS2DMQCn";

    @AppScope
    @Provides
    public Web3j web3j(HttpService httpService) {
        return Web3jFactory.build(httpService);
    }

    @AppScope
    @Provides
    public HttpService httpService(OkHttpClient okHttpClient) {
        return new HttpService(ETH_URL, okHttpClient, false);
    }
}