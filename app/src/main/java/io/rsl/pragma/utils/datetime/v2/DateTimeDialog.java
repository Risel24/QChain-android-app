package io.rsl.pragma.utils.datetime.v2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Objects;

import io.rsl.pragma.R;
import io.rsl.pragma.utils.datetime.DateStringUtils;
import io.rsl.pragma.utils.datetime.v2.DatetimePagerAdapter;

public class DateTimeDialog extends DialogFragment {

    private OnDateSetListener listener;

    private String neutralButtonText;

    private TabLayout tabLayout;
    private Calendar calendar;

    public DateTimeDialog() {

    }

    @SuppressLint("ValidFragment")
    public DateTimeDialog(String neutralButtonText) {
        this.listener = listener;
        this.neutralButtonText = neutralButtonText;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View dialogView = inflater.inflate(R.layout.date_time_picker, container, false);
        ViewPager viewPager = dialogView.findViewById(R.id.viewpager);
        final DatetimePagerAdapter pagerAdapter = new DatetimePagerAdapter(getContext(), listener());
        viewPager.setAdapter(pagerAdapter);

        tabLayout = dialogView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return dialogView;
    }

    /*@NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Set", (dialog, which) -> {
                    if(listener != null) listener.onDateSet(calendar);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNeutralButton(neutralButtonText, (dialog, which) -> {
                    listener.onDateSet(null);
                    dialog.dismiss();
                });

        return adb.create();
    }*/

    public void showWithListener(FragmentManager fragmentManager,OnDateSetListener listener) {
        this.listener = listener;
        this.show(fragmentManager,"TAG123");
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
