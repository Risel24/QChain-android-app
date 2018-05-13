package io.rsl.pragma.di.modules;

import android.util.Log;

import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import io.rsl.pragma.api.QService;
import io.rsl.pragma.di.scopes.AppScope;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module(includes = {NetworkModule.class, GsonModule.class})
public class QServiceModule {

    private static final String API_URL = "http://165.227.135.82";

    @AppScope
    @Provides
    public QService qService(Retrofit retrofit) {
        return retrofit.create(QService.class);
    }

    @AppScope
    @Provides
    public Retrofit retrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .baseUrl(API_URL)
                .build();
    }

}
