package io.rsl.pragma;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import io.rsl.pragma.di.AppComponent;
import io.rsl.pragma.di.DaggerAppComponent;
import io.rsl.pragma.di.modules.ContextModule;
import timber.log.Timber;

public class App extends Application {

    public static App get(Activity activity) {
        return (App) activity.getApplication();
    }

    public static App get(Context activityContext) {
        if(activityContext instanceof Activity) {
            return (App) ((Activity) activityContext).getApplication();
        } else throw new IllegalArgumentException(activityContext.toString() + " must be Activity context");
    }

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        component = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public AppComponent getComponent() {
        return component;
    }
}
