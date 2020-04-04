package org.weshley.fishtracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.Map;

public class TripDetailFragment
   extends AbstractFragment
   implements SelectedTripChangeListener, TripLocationsChangeListener,
              TripListChangeListener
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
      initListeners();
      updateUi();

      return _rootView;
   }
   @Override
   public void selectedTripChanged(SelectedTripChangeEvent ev)
   {
      updateUi();
   }

   @Override
   public void tripListChanged(TripListChangeEvent e)
   {
      // we can get this when an active trip is ended.  need to update end date/time
      if(e.getChangedTrip() == getSelectedTrip())
      {
         updateEndTripControls();
         updateTripLabel();
      }
   }

   @Override
   public void tripLocationsChanged(TripLocationsChangeEvent ev)
   {
      initLocationEditor();
      setLocationSelection(ev.getLocation());
   }

   private void updateTripLabel()
   {
      Trip t = getSelectedTrip();
      if(null != t)
         getTitleView().setText(getTripManager().getDisplayableTripLabel(t));
   }

   private void initListeners()
   {
      getTripManager().addSelectedTripChangeListener(this);
      getTripManager().addTripLocationsChangeListener(this);
      getTripManager().addTripListChangeListener(this);

      getTrackingAirTempControl().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
         @Override
         public void onCheckedChanged(CompoundButton compoundButton, boolean b)
         {
            getSelectedTrip().setTrackingLocation(b);
         }
      });

      getLakeLevelControl().addTextChangedListener(new TextWatcher()
      {
         @Override
         public void afterTextChanged(Editable e)
         {
            String s = e.toString();
            if(s.length() > 0)
              getSelectedTrip().setLakeLevel(Integer.parseInt(s));
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {}
      });

      getAirTempControl().addTextChangedListener(new TextWatcher()
      {
         @Override
         public void afterTextChanged(Editable e)
         {
            String s = e.toString();
            if(s.length() > 0)
               getSelectedTrip().setAirTemp(new Temperature(Integer.parseInt(s)));
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {}
      });

      getWaterTempControl().addTextChangedListener(new TextWatcher()
      {
         @Override
         public void afterTextChanged(Editable e)
         {
            String s = e.toString();
            if(s.length() > 0)
               getSelectedTrip().setWaterTemp(new Temperature(Integer.parseInt(s)));
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {}
      });

      getWindSpeedControl().addTextChangedListener(new TextWatcher()
      {
         @Override
         public void afterTextChanged(Editable e)
         {
            String s = e.toString();
            if(s.length() > 0)
               getSelectedTrip().setWindSpeed(Integer.parseInt(s));
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {}
      });

      getNotesControl().addTextChangedListener(new TextWatcher()
      {
         @Override
         public void afterTextChanged(Editable e)
         {
            String s = e.toString();
            getSelectedTrip().setNotes(s);
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {}
      });

      getAddAudioNoteButton().setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            addNewAudioNote();
         }
      });
   }

   private void addNewAudioNote()
   {
      String label = Calendar.getInstance().getTime().toString();
      AudioNote note = new AudioNote(label, null);
      getSelectedTrip().addAudioNote(note);
      Spinner spinner = getAudioNoteControl();
      ArrayAdapter<AudioNote> adapter = (ArrayAdapter<AudioNote>) spinner.getAdapter();
      adapter.add(note);
      spinner.setSelection(adapter.getCount() - 1);
      updateAudioNoteButtonState();
   }

   private void updateUi()
   {
      Trip t = getSelectedTrip();
      if(null == t)
         return;

      updateTripLabel();
      getStartDateView().setText(Config.getDateFormat().format(t.getStart()));
      getStartTimeView().setText(Config.getTimeFormat().format(t.getStart()));

      updateEndTripControls();

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

   private void updateEndTripControls()
   {
      Trip t = getSelectedTrip();
      if(null == t)
         return;

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

   private void setWindStrength(Trip.WindStrength strength)
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
      initAudioNoteList();
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
      editor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}

         @Override
         public void onItemSelected(
           AdapterView<?> parent, View view, int pos, long id)
         {
            Trip.Direction dir = (Trip.Direction) parent.getItemAtPosition(pos);
            getSelectedTrip().setWindDirection(dir);
         }
      });
   }

   private void initWindStrengthEditor()
   {
      Spinner editor = getWindStrengthControl();
      List<Trip.WindStrength> itemList = new ArrayList<>();
      for(Trip.WindStrength str : getTripManager().getAllWindStrengths())
         itemList.add(str);
      ArrayAdapter<Trip.WindStrength> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, itemList);
      editor.setAdapter(spinnerArrayAdapter);
      editor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}

         @Override
         public void onItemSelected(
           AdapterView<?> parent, View view, int pos, long id)
         {
            Trip.WindStrength str = (Trip.WindStrength) parent.getItemAtPosition(pos);
            getSelectedTrip().setWindStrength(str);
         }
      });
   }

   private void initPrecipEditor()
   {
      Spinner editor = getPrecipitationControl();
      List<Trip.Precipitation> itemList = new ArrayList<>();
      for(Trip.Precipitation precip : getTripManager().getAllPrecipitations())
         itemList.add(precip);
      ArrayAdapter<Trip.Precipitation> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, itemList);
      editor.setAdapter(spinnerArrayAdapter);
      editor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}

         @Override
         public void onItemSelected(
           AdapterView<?> parent, View view, int pos, long id)
         {
            Trip.Precipitation precip = (Trip.Precipitation) parent.getItemAtPosition(pos);
            getSelectedTrip().setPrecipitation(precip);
         }
      });
   }

   private void initAudioNoteList()
   {
      Spinner spinner = getAudioNoteControl();
      List<AudioNote> noteList = new ArrayList<>();
      // Map<String, AudioNote> getAudioNotes
      for(AudioNote note : getSelectedTrip().getAudioNotes().values())
         noteList.add(note);
      ArrayAdapter<AudioNote> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, noteList);
      spinner.setAdapter(spinnerArrayAdapter);
      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent)
         {
            updateAudioNoteButtonState();
         }

         @Override
         public void onItemSelected(
            AdapterView<?> parent, View view, int pos, long id)
         {
            updateAudioNoteButtonState();
         }
     });

     updateAudioNoteButtonState();
   }

   private void updateAudioNoteButtonState()
   {
      Spinner spinner = getAudioNoteControl();
      getEditAudioNoteButton().setEnabled(spinner.getSelectedItem() != null);
      getPlayAudioNoteButton().setEnabled(spinner.getSelectedItem() != null);
   }

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
      locationEditor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}

         @Override
         public void onItemSelected(
            AdapterView<?> parent, View view, int pos, long id)
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
     });
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

   private Spinner getAudioNoteControl()
   {
      return (Spinner) _rootView.findViewById(R.id.detail_audio_notes_list);
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

   private Button getAddAudioNoteButton()
   {
      return (Button) _rootView.findViewById(R.id.details_add_audio_note_button);
   }

   private Button getEditAudioNoteButton()
   {
      return (Button) _rootView.findViewById(R.id.details_edit_audio_note_button);
   }

   private Button getPlayAudioNoteButton()
   {
      return (Button) _rootView.findViewById(R.id.details_play_audio_note_button);
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
