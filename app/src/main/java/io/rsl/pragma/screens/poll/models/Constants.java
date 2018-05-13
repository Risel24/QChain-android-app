package io.rsl.pragma.screens.poll.models;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static io.rsl.pragma.screens.poll.models.Constants.ViewType.DATETIME;
import static io.rsl.pragma.screens.poll.models.Constants.ViewType.TITLE;
import static io.rsl.pragma.screens.poll.models.Constants.ViewType.TYPE_SELECTOR;
import static io.rsl.pragma.screens.poll.models.Constants.ViewType.VOTE_EDITTEXT;

public class Constants {

    @IntDef({TITLE, TYPE_SELECTOR, VOTE_EDITTEXT, DATETIME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewType {
        int TITLE = 100;
        int TYPE_SELECTOR = 200;
        int VOTE_EDITTEXT = 300;
        int DATETIME = 400;
    }
}
