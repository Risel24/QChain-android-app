package io.rsl.pragma.di.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import io.rsl.pragma.di.scopes.AppScope;

@Module
public class GsonModule {

    @AppScope
    @Provides
    public Gson gson(GsonBuilder gsonBuilder) {
        return gsonBuilder.create();
    }

    @AppScope
    @Provides
    public GsonBuilder gsonBuilder() {
        GsonBuilder builder = new GsonBuilder();
        //builder.excludeFieldsWithoutExposeAnnotation();
        return new GsonBuilder();
    }
}