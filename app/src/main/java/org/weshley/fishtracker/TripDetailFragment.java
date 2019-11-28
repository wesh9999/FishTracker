package org.weshley.fishtracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TripDetailFragment
   extends AbstractFragment
   implements SelectedTripChangeListener
{
   private ViewGroup _rootView = null;

   public static String getLabel()
   {
      return MainActivity.getAppResources().getString(R.string.trip_detail_label);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      _rootView = (ViewGroup) inflater.inflate(
         R.layout.trip_detail_fragment, container, false);

      // set up the trip detail view with a simple linear layout and a custom adapter
/*
      RecyclerView recyclerView = (RecyclerView) _rootView.findViewById(R.id.trip_detail_list);
      recyclerView.setHasFixedSize(true);
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(_rootView.getContext());
      recyclerView.setLayoutManager(layoutManager);
 */

      initEditors();

      TripManager.instance().addSelectedTripChangeListener(this);

      updateUi();

      return _rootView;
   }

   public void selectedTripChanged(SelectedTripChangeEvent ev)
   {
      updateUi();
   }

   private void updateUi()
   {
      Trip t = getSelectedTrip();
      getTitleView().setText(getTripManager().getDisplayableTripLabel(t));
      getStartDateView().setText(Config.getDateFormat().format(t.getStart()));
      getStartTimeView().setText(Config.getTimeFormat().format(t.getStart()));
   }

   private Trip getSelectedTrip()
   {
      return TripManager.instance().getSelectedTrip();
   }

   private void initEditors()
   {
      initStartDateEditor();
      initStartTimeEditor();
   }

   private void initStartDateEditor()
   {
      final TextView startView = getStartDateView();

      startView.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
             final Date start = getSelectedTrip().getStart();
             final Calendar cal = Calendar.getInstance();
             cal.setTime(start);
             int day = cal.get(Calendar.DAY_OF_MONTH);
             int month = cal.get(Calendar.MONTH);
             int year = cal.get(Calendar.YEAR);

             // date picker dialog
             DatePickerDialog picker = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener()
                {
                   @Override
                   public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                   {
                      int hours = cal.get(Calendar.HOUR_OF_DAY);
                      int minutes = cal.get(Calendar.MINUTE);
                      int seconds = cal.get(Calendar.SECOND);
                      cal.set(year, monthOfYear, dayOfMonth, hours, minutes, seconds);
                      Date newStart = cal.getTime();
                      getSelectedTrip().setStart(newStart);
                      updateUi();
                   }
                },
                year, month, day);
                picker.show();
            }
        });
   }

   private void initStartTimeEditor()
   {
      final TextView startView = getStartTimeView();

      startView.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
             final Date start = getSelectedTrip().getStart();
             final Calendar cal = Calendar.getInstance();
             cal.setTime(start);
             int hours = cal.get(Calendar.HOUR_OF_DAY);
             int minutes = cal.get(Calendar.MINUTE);

             // date picker dialog
             TimePickerDialog picker = new TimePickerDialog(
                getActivity(),
                new TimePickerDialog.OnTimeSetListener()
                {
                   @Override
                   public void onTimeSet(TimePicker view, int hours, int minutes)
                   {
                      int day = cal.get(Calendar.DAY_OF_MONTH);
                      int month = cal.get(Calendar.MONTH);
                      int year = cal.get(Calendar.YEAR);
                      int seconds = cal.get(Calendar.SECOND);
                      cal.set(year, month, day, hours, minutes, seconds);
                      Date newStart = cal.getTime();
                      getSelectedTrip().setStart(newStart);
                      updateUi();
                   }
                },
                hours, minutes, DateFormat.is24HourFormat(getActivity()));
                picker.show();
            }
        });
   }

   private TextView getStartDateView()
   {
      return (TextView) _rootView.findViewById(R.id.detail_start_date);
   }

   private TextView getStartTimeView()
   {
      return (TextView) _rootView.findViewById(R.id.detail_start_time);
   }

   private TextView getTitleView()
   {
      return (TextView) _rootView.findViewById(R.id.trip_detail_title);
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }


}
