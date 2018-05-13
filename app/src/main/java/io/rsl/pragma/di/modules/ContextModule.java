package io.rsl.pragma.di.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.rsl.pragma.di.scopes.AppScope;

@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @AppScope
    @Provides
    public Context context() {
        return context;
    }
}