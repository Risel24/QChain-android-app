package io.rsl.pragma.di.modules;

import android.content.Context;

import java.io.File;

import dagger.Module;
import dagger.Provides;
import io.rsl.pragma.di.scopes.AppScope;
import io.rsl.pragma.repositories.UserRepository;
import io.rsl.pragma.utils.TokenInterceptor;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module(includes = {ContextModule.class, DatabaseModule.class})
public class NetworkModule {

    @AppScope
    @Provides
    public OkHttpClient okHttpClient(TokenInterceptor tokenInterceptor, HttpLoggingInterceptor httpLoggingInterceptor, Cache cache) {
        return new OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .cache(cache)
                .build();
    }

    @AppScope
    @Provides
    public HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Timber.i(message));
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    @AppScope
    @Provides
    public File cacheFile(Context context) {
        File cacheFile = new File(context.getCacheDir(), "okhttp.cache");
        cacheFile.mkdirs();
        return cacheFile;
    }

    @AppScope
    @Provides
    public Cache cache(File cacheFile) {
        return new Cache(cacheFile, 15728640); // 15 megabytes
    }

    @AppScope
    @Provides
    public TokenInterceptor tokenInterceptor(UserRepository userRepository) {
        return new TokenInterceptor(userRepository);
    }
}