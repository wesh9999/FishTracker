package org.weshley.fishtracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TripDetailFragment
   extends AbstractFragment
   implements SelectedTripChangeListener, AdapterView.OnItemSelectedListener,
              TripLocationsChangeListener
{
   private ViewGroup _rootView = null;
   private HashMap<String,Integer> _locationMap;

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

      initEditors();
      getTripManager().addSelectedTripChangeListener(this);
      getTripManager().addTripLocationsChangeListener(this);
      updateUi();

      return _rootView;
   }

   public void selectedTripChanged(SelectedTripChangeEvent ev)
   {
      updateUi();
   }

   public void tripLocationsChanged(TripLocationsChangeEvent ev)
   {
      initLocationEditor();
      setLocationSelection(ev.getLocation());
   }

   public void onItemSelected(
      AdapterView<?> parent, View view, int pos, long id)
   {
      if(parent == getLocationControl())
      {
         String loc = (String) parent.getItemAtPosition(pos);
         if(Config.OTHER_LOCATION_LABEL.equals(loc))
         {
            final EditText input = new EditText(getContext());
            new AlertDialog.Builder(getContext())
               .setTitle("Enter New Trip Location")
               .setView(input)
               .setIcon(android.R.drawable.ic_dialog_alert)
               .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                  {
                     public void onClick(DialogInterface dialog, int whichButton)
                     {
                        String loc = input.getText().toString();
                        getSelectedTrip().setLocation(loc);
                     }
                  })
               .setNegativeButton(android.R.string.no, null).show();
         }
         else
         {
            getSelectedTrip().setLocation(loc);
         }
      }
   }

   public void onNothingSelected(AdapterView<?> parent)
   {
   }

   private void updateUi()
   {
      Trip t = getSelectedTrip();
      if(null == t)
         return;

      getTitleView().setText(getTripManager().getDisplayableTripLabel(t));
      getStartDateView().setText(Config.getDateFormat().format(t.getStart()));
      getStartTimeView().setText(Config.getTimeFormat().format(t.getStart()));

      boolean isActive = (getSelectedTrip() == getTripManager().getActiveTrip());
      getEndDateView().setEnabled(!isActive);
      getEndTimeView().setEnabled(!isActive);
      if(isActive)
      {
         getEndDateView().setText("< Active >");
         getEndTimeView().setText("");
      }
      else
      {
         getEndDateView().setText(Config.getDateFormat().format(t.getEnd()));
         getEndTimeView().setText(Config.getTimeFormat().format(t.getEnd()));
      }

      setLocationSelection(t.getLocation());
      setLakeLevel(t.getLakeLevel());
      setAirTemp(t.getAirTemp());
      setTrackingAirTemp(t.isTrackingTemp());
      setWaterTemp(t.getWaterTemp());
      setWindSpeed(t.getWindSpeed());
      setWindDirection(t.getWindDirection());
      setWindStrength(t.getWindStrength());
      setPrecipitation(t.getPrecipitation());
      setNotes(t.getNotes());
   }

   private void setWindSpeed(Integer speed)
   {
      String s = "";
      if(null != speed)
        s = speed.toString();
      getWindSpeedControl().setText(s);
   }

   private void setPrecipitation(Trip.Precipitation precip)
   {
      int pos = -1;
      if(null != precip)
         pos = precip.ordinal();
      getPrecipitationControl().setSelection(pos);
   }

   private void setWindStrength(Trip.Strength strength)
   {
      int pos = -1;
      if(null != strength)
         pos = strength.ordinal();
      getWindStrengthControl().setSelection(pos);
   }

   private void setWindDirection(Trip.Direction dir)
   {
      int pos = -1;
      if(null != dir)
         pos = dir.ordinal();
      getWindDirectionControl().setSelection(pos);
   }

   private void setNotes(String notes)
   {
      if(null == notes)
         getNotesControl().setText("");
      else
         getNotesControl().setText(notes);
   }

   private void setTrackingAirTemp(boolean b)
   {
      getTrackingAirTempControl().setChecked(b);
   }

   private void setLakeLevel(Integer level)
   {
      if(null == level)
         getLakeLevelControl().setText("");
      else
         getLakeLevelControl().setText(level.toString());
   }

   private void setAirTemp(Temperature temp)
   {
      String val = "";
      String units = Config.getDefaultTempUnits().toString();
      if(null != temp)
      {
         val =  "" + temp.getTemp();
         units = temp.getUnits().toString();
      }
      getAirTempControl().setText(val);
      getAirTempUnitsLabel();
   }

   private void setWaterTemp(Temperature temp)
   {
      String val = "";
      String units = Config.getDefaultTempUnits().toString();
      if(null != temp)
      {
         val =  "" + temp.getTemp();
         units = temp.getUnits().toString();
      }
      getWaterTempControl().setText(val);
      getWaterTempUnitsLabel();
   }

   private void setLocationSelection(String loc)
   {
      if(null == loc)
      {
         getLocationControl().setSelection(-1);
      }
      else
      {
         if(_locationMap.containsKey(loc))
         {
            int pos = _locationMap.get(loc);
            getLocationControl().setSelection(pos);
         }
         else
         {
            getTripManager().addLocation(loc);
               // event handling will trigger update of _locationMap and
               // set the spinner selection
         }
      }
   }

   private Trip getSelectedTrip()
   {
      return getTripManager().getSelectedTrip();
   }

   private void initEditors()
   {
      initStartDateEditor();
      initStartTimeEditor();
      initEndDateEditor();
      initEndTimeEditor();
      initLocationEditor();
      initWindDirectionEditor();
      initWindStrengthEditor();
      initPrecipEditor();
   }

   private void initWindDirectionEditor()
   {
     Spinner editor = getWindDirectionControl();
     List<Trip.Direction> itemList = new ArrayList<>();
     for(Trip.Direction dir : getTripManager().getAllWindDirections())
       itemList.add(dir);
     ArrayAdapter<Trip.Direction> spinnerArrayAdapter = new ArrayAdapter<>(
        this.getContext(), R.layout.spinner_item, itemList);
     editor.setAdapter(spinnerArrayAdapter);
     editor.setOnItemSelectedListener(this);
   }

   private void initWindStrengthEditor()
   {
     Spinner editor = getWindStrengthControl();
     List<Trip.Strength> itemList = new ArrayList<>();
     for(Trip.Strength str : getTripManager().getAllWindStrengths())
       itemList.add(str);
     ArrayAdapter<Trip.Strength> spinnerArrayAdapter = new ArrayAdapter<>(
        this.getContext(), R.layout.spinner_item, itemList);
     editor.setAdapter(spinnerArrayAdapter);
     editor.setOnItemSelectedListener(this);
   }

   private void initPrecipEditor()
   {
     Spinner editor = getPrecipitationControl();
     List<Trip.Precipitation> itemList = new ArrayList<>();
     for(Trip.Precipitation str : getTripManager().getAllPrecipitations())
       itemList.add(str);
     ArrayAdapter<Trip.Precipitation> spinnerArrayAdapter = new ArrayAdapter<>(
        this.getContext(), R.layout.spinner_item, itemList);
     editor.setAdapter(spinnerArrayAdapter);
     editor.setOnItemSelectedListener(this);   }

   private void initLocationEditor()
   {
     Spinner locationEditor = getLocationControl();
     List<String> locList = new ArrayList<>();
     locList.add(Config.OTHER_LOCATION_LABEL);
     for(String loc : getTripManager().getAllLocations())
       locList.add(loc);
     _locationMap = new HashMap<String,Integer>();
     for(int i = 0; i < locList.size(); ++i)
        _locationMap.put(locList.get(i), i);
     ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
        this.getContext(), R.layout.spinner_item, locList);
     locationEditor.setAdapter(spinnerArrayAdapter);
     locationEditor.setOnItemSelectedListener(this);
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

   private void initEndDateEditor()
   {
      final TextView endView = getEndDateView();

      endView.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
             final Date end = getSelectedTrip().getEnd();
             final Calendar cal = Calendar.getInstance();
             cal.setTime(end);
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
                      Date newEnd = cal.getTime();
                      getSelectedTrip().setEnd(newEnd);
                      updateUi();
                   }
                },
                year, month, day);
                picker.show();
            }
        });
   }

   private void initEndTimeEditor()
   {
      final TextView endView = getEndTimeView();

      endView.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
             final Date end = getSelectedTrip().getEnd();
             final Calendar cal = Calendar.getInstance();
             cal.setTime(end);
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
                      Date newEnd = cal.getTime();
                      getSelectedTrip().setEnd(newEnd);
                      updateUi();
                   }
                },
                hours, minutes, DateFormat.is24HourFormat(getActivity()));
                picker.show();
            }
        });
   }

   private Spinner getLocationControl()
   {
      return (Spinner) _rootView.findViewById(R.id.detail_location_spinner);
   }

   private Spinner getWindDirectionControl()
   {
      return (Spinner) _rootView.findViewById(R.id.detail_wind_dir_spinner);
   }

   private Spinner getWindStrengthControl()
   {
      return (Spinner) _rootView.findViewById(R.id.detail_wind_strength_spinner);
   }

   private Spinner getPrecipitationControl()
   {
      return (Spinner) _rootView.findViewById(R.id.detail_precip_spinner);
   }

   private EditText getLakeLevelControl()
   {
      return (EditText) _rootView.findViewById(R.id.detail_lake_level_entry);
   }

   private CheckBox getTrackingAirTempControl()
   {
      return (CheckBox) _rootView.findViewById(R.id.detail_enable_temp_tracking_check);
   }

   private TextView getAirTempUnitsLabel()
   {
      return (TextView) _rootView.findViewById(R.id.detail_air_temp_units);
   }

   private TextView getWaterTempUnitsLabel()
   {
      return (TextView) _rootView.findViewById(R.id.detail_water_temp_units);
   }

   private EditText getAirTempControl()
   {
      return (EditText) _rootView.findViewById(R.id.detail_air_temp_entry);
   }

   private EditText getWaterTempControl()
   {
      return (EditText) _rootView.findViewById(R.id.detail_water_temp_entry);
   }

   private EditText getNotesControl()
   {
      return (EditText) _rootView.findViewById(R.id.detail_notes_field);
   }

   private EditText getWindSpeedControl()
   {
      return (EditText) _rootView.findViewById(R.id.detail_wind_speed_entry);
   }

   private TextView getStartDateView()
   {
      return (TextView) _rootView.findViewById(R.id.detail_start_date);
   }

   private TextView getStartTimeView()
   {
      return (TextView) _rootView.findViewById(R.id.detail_start_time);
   }

   private TextView getEndDateView()
   {
      return (TextView) _rootView.findViewById(R.id.detail_end_date);
   }

   private TextView getEndTimeView()
   {
      return (TextView) _rootView.findViewById(R.id.detail_end_time);
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
