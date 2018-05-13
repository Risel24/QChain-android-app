package io.rsl.pragma.utils;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import io.rsl.pragma.repositories.UserRepository;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private UserRepository userRepository;

    public TokenInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        boolean updated = false;

        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();


        if (request.header("User-Required") != null) {
            int index = request.url().pathSegments().indexOf("_idholder_");
            if(index >= 0) {
                HttpUrl url = request.url().newBuilder()
                        .setPathSegment(index, userRepository.getUser().getUserID())
                        .build();
                requestBuilder.url(url);
                requestBuilder.removeHeader("User-Required");
                updated = true;
            }
        }

        if(request.header("No-Authentication") == null) {
            requestBuilder.addHeader("Authorization", "Bearer " + userRepository.getUser().getToken());
            updated = true;
        }
        return chain.proceed(updated ? requestBuilder.build() : request);
    }
}
