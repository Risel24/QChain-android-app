package io.rsl.pragma.utils.datetime.v2;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import io.rsl.pragma.R;

public class DatetimePagerAdapter extends PagerAdapter {

    private OnDateSetListener listener;
    private Calendar calendar;

    private String tabTitles[] = new String[] {"Date", "Time"};
    private Context context;

    private DatePicker datePicker;
    private TimePicker timePicker;

    DatetimePagerAdapter(Context context, OnDateSetListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout;
        if (position == 0) {
            layout = (ViewGroup) inflater.inflate(R.layout.fragment_date, container, false);
            instantiateDate(layout);
        } else {
            layout = (ViewGroup) inflater.inflate(R.layout.fragment_time, container, false);
            instantiateTime(layout);
        }

        container.addView(layout);
        return layout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return Objects.equals(view, object);
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    private void instantiateDate(ViewGroup viewGroup) {
        datePicker = viewGroup.findViewById(R.id.date_picker);
        if(timePicker != null) createCalendar();

        DatePicker.OnDateChangedListener onDateChangedListener = (view, year, monthOfYear, dayOfMonth) -> {

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            listener.onDateSet(calendar);
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(onDateChangedListener);
        } else {
            datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), onDateChangedListener);
        }
    }

    private void instantiateTime(ViewGroup viewGroup) {
        timePicker = viewGroup.findViewById(R.id.time_picker);
        if(datePicker != null) createCalendar();

        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            listener.onTimeSet(calendar);
        });
    }

    private void createCalendar() {
        calendar = new GregorianCalendar(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(),
                timePicker.getCurrentMinute()
        );
        listener.onInitComplete(calendar);
    }

    public interface OnDateSetListener {
        void onDateSet(Calendar calendar);
        void onTimeSet(Calendar calendar);
        void onInitComplete(Calendar calendar);
    }

}