package io.rsl.pragma.utils.datetime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.Calendar;
import java.util.Objects;

import io.rsl.pragma.R;

public class DateTimeDialog {

    public static final String NOW = "Now";
    public static final String MANUAL = "Manual finish";

    private final TabLayout tabLayout;
    private final AlertDialog alertDialog;
    private OnDateSetListener listener;

    private Calendar calendar;

    public DateTimeDialog(Context context, String neutralButtonText) {
        final View dialogView = View.inflate(context, R.layout.date_time_picker, null);
        ViewPager viewPager = dialogView.findViewById(R.id.viewpager);
        final DatetimePagerAdapter pagerAdapter = new DatetimePagerAdapter(context, listener());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = dialogView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("Ок", (dialog, which) -> {

                    if(listener != null) listener.onDateSet(calendar);
                    dialog.dismiss();
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .setNeutralButton(neutralButtonText, (dialog, which) -> {
                    listener.onDateSet(null);
                    dialog.dismiss();
                }).create();
    }

    public void showWithListener(OnDateSetListener listener) {
        this.listener = listener;
        alertDialog.show();
    }

    public void show() {
        alertDialog.show();
    }

    private DatetimePagerAdapter.OnDateSetListener listener() {
        return new DatetimePagerAdapter.OnDateSetListener() {
            @Override
            public void onDateSet(Calendar newCalendar) {
                calendar = newCalendar;
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(DateStringUtils.getDMY(calendar));
            }

            @Override
            public void onTimeSet(Calendar newCalendar) {
                calendar = newCalendar;
                Objects.requireNonNull(tabLayout.getTabAt(1)).setText(DateStringUtils.getHM(calendar));
            }

            @Override
            public void onInitComplete(Calendar newCalendar) {
                calendar = newCalendar;
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(DateStringUtils.getDMY(calendar));
                Objects.requireNonNull(tabLayout.getTabAt(1)).setText(DateStringUtils.getHM(calendar));
            }
        };
    }

    public interface OnDateSetListener {
        void onDateSet(Calendar calendar);
    }

}
