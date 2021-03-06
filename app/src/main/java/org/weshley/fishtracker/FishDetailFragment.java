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

public class FishDetailFragment
   extends AbstractFragment
   implements SelectedTripChangeListener, TripListChangeListener, SelectedFishChangeListener,
              SpeciesChangeListener, CoverChangeListener, LureTypeChangeListener,
              LureColorChangeListener, LureSizeChangeListener
{
   private ViewGroup _rootView = null;
   private HashMap<String,Integer> _speciesMap;
   private HashMap<String,Integer> _coverMap;
   private HashMap<String,Integer> _lureTypeMap;
   private HashMap<String,Integer> _lureColorMap;
   private HashMap<String,Integer> _lureSizeMap;

   public static String getLabel()
   {
      return MainActivity.getAppResources().getString(R.string.fish_detail_label);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      _rootView = (ViewGroup) inflater.inflate(
         R.layout.fish_detail_fragment, container, false);

      initEditors();
      initListeners();
      updateUi();

      return _rootView;
   }

   @Override
   public void selectedTripChanged(SelectedTripChangeEvent ev)
   {
      updateTripLabel();
   }

   @Override
   public void tripListChanged(TripListChangeEvent e)
   {
      // we can get this when an active trip is ended.  need to update end date/time
      if(e.getChangedTrip() == getSelectedTrip())
         updateTripLabel();
   }

   @Override
   public void selectedFishChanged(SelectedFishChangeEvent ev)
   {
      updateUi();
   }

   @Override
   public void speciesChanged(SpeciesChangeEvent ev)
   {
      initSpeciesEditor();
      setSpecies(ev.getSpecies());
   }

   @Override
   public void lureTypeChanged(LureTypeChangeEvent ev)
   {
      initLureTypeEditor();
      setLureType(ev.getType());
   }

   @Override
   public void lureColorChanged(LureColorChangeEvent ev)
   {
      initLureColorEditor();
      setLureColor(ev.getColor());
   }

   @Override
   public void lureSizeChanged(LureSizeChangeEvent ev)
   {
      initLureSizeEditor();
      setLureSize(ev.getSize());
   }

   @Override
   public void coverChanged(CoverChangeEvent ev)
   {
      initCoverEditor();
      setCover(ev.getCover());
   }

   private void updateUi()
   {
      Trip t = getSelectedTrip();
      if(null != t)
         updateTripLabel();

      // TODO - update all the controls from the selected fish
      Fish f = getSelectedFish();
      if(null == f)
         clearControls();
      else
         updateControls(f);
   }

   private void enableControls(boolean enabled)
   {
      getCaughtTimeControl().setEnabled(enabled);
      getCaughtDateControl().setEnabled(enabled);
      getSpeciesControl().setEnabled(enabled);
      getLengthControl().setEnabled(enabled);
      getWeightControl().setEnabled(enabled);
      getLureTypeControl().setEnabled(enabled);
      getLureSizeControl().setEnabled(enabled);
      getLureColorControl().setEnabled(enabled);
      getLandedControl().setEnabled(enabled);
      getAirTempControl().setEnabled(enabled);
      getWaterTempControl().setEnabled(enabled);
      getWaterDepthControl().setEnabled(enabled);
      getWindSpeedControl().setEnabled(enabled);
      getWindDirectionControl().setEnabled(enabled);
      getWindStrengthControl().setEnabled(enabled);
      getPrecipitationControl().setEnabled(enabled);
      getGpsControl().setEnabled(enabled);
      getCoverControl().setEnabled(enabled);
      getNotesControl().setEnabled(enabled);
      getPlayAudioNoteButton().setEnabled(enabled);
      getEditAudioNoteButton().setEnabled(enabled);
      getAddAudioNoteButton().setEnabled(enabled);
      getAudioNoteControl().setEnabled(enabled);
   }

   private void clearControls()
   {
      enableControls(false);
      setTitle("");
      setCaughtTime(null);
      setSpecies(null);
      setLength(null);
      setWeight(null);
      setLure(null);
      setLanded(null);
      setAirTemp(null);
      setWaterTemp(null);
      setWaterDepth(null);
      setWindSpeed(null);
      setWindDirection(null);
      setWindStrength(null);
      setPrecipitation(null);
      setGpsLocation(null);
      setCover(null);
      setNotes(null);
      clearAudioNotes();
   }

   private void updateControls(Fish f)
   {
      enableControls(true);
      updateTripLabel();
      setCaughtTime(f.getCaughtTime());
      setSpecies(f.getSpecies());
      setLength(f.getLength());
      setWeight(f.getWeight());
      setLure(f.getLure());
      setLanded(f.getCaughtState());
      setAirTemp(f.getAirTemp());
      setWaterTemp(f.getWaterTemp());
      setWaterDepth(f.getWaterDepth());
      setWindSpeed(f.getWindSpeed());
      setWindDirection(f.getWindDirection());
      setWindStrength(f.getWindStrength());
      setPrecipitation(f.getPrecipitation());
      setGpsLocation(f.getLocation());
      setCover(f.getCover());
      setNotes(f.getNotes());
      populateAudioNoteList(f);
   }

   private Fish getSelectedFish() { return getTripManager().getSelectedFish(); }

   private Trip getSelectedTrip()
   {
      return getTripManager().getSelectedTrip();
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }

   private void updateTripLabel()
   {
      Trip t = getSelectedTrip();
      if(null != t)
         setTitle(getTripManager().getDisplayableTripLabel(t));
   }

   private void setTitle(String t)
   {
      getTitleView().setText(t);
   }

   private void initEditors()
   {
      initCaughtDateEditor();
      initCaughtTimeEditor();
      initSpeciesEditor();
      initLureTypeEditor();
      initLureSizeEditor();
      initLureColorEditor();
      initLandedEditor();
      initWindDirectionEditor();
      initWindStrengthEditor();
      initPrecipEditor();
      initCoverEditor();
   }

   private void initCaughtDateEditor()
   {
      final TextView view = getCaughtDateControl();

      view.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
             final Calendar cal = Calendar.getInstance();
             Date dt = null;
             final Fish f = getSelectedFish();
             if(null == f)
                dt = cal.getTime();
             else
                dt = f.getCaughtTime();
             cal.setTime(dt);
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
                      Date newDt = cal.getTime();
                      if(null != f)
                         f.setCaughtTime(newDt);
                      updateUi();
                   }
                },
                year, month, day);
                picker.show();
            }
        });
   }

   private void initCaughtTimeEditor()
   {
      final TextView view = getCaughtTimeControl();

      view.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
             final Calendar cal = Calendar.getInstance();
             final Fish f = getSelectedFish();
             Date dt = null;
             if(null == f)
                dt = cal.getTime();
             else
                dt = f.getCaughtTime();
             cal.setTime(dt);
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
                      Date newDt = cal.getTime();
                      if(null != f)
                         f.setCaughtTime(newDt);
                      updateUi();
                   }
                },
                hours, minutes, DateFormat.is24HourFormat(getActivity()));
                picker.show();
            }
        });
   }

   private void initSpeciesEditor()
   {
      Spinner editor = getSpeciesControl();
      List<String> list = new ArrayList<>();
      list.add(Config.BLANK_LABEL);
      list.add(Config.OTHER_LABEL);
      for(String s : getTripManager().getAllSpecies())
         list.add(s);
      _speciesMap = new HashMap<String,Integer>();
      for(int i = 0; i < list.size(); ++i)
         _speciesMap.put(list.get(i), i);
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, list);
      editor.setAdapter(spinnerArrayAdapter);
      editor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}

         @Override
         public void onItemSelected(
            AdapterView<?> parent, View view, int pos, long id)
         {
            final String species = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(species))
            {
               final EditText input = new EditText(getContext());
               new AlertDialog.Builder(getContext())
                .setTitle("Enter New Species")
                .setView(input)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                  {
                     public void onClick(DialogInterface dialog, int whichButton)
                     {
                        String species = input.getText().toString();
                        Fish f = getSelectedFish();
                        if(null != f)
                           f.setSpecies(species);
                     }
                  })
               .setNegativeButton(android.R.string.no, null).show();
            }
            else
            {
               Fish f = getSelectedFish();
               if(null != f)
                  f.setSpecies(species);
            }
         }
     });
   }

   private void initLureTypeEditor()
   {
      Spinner editor = getLureTypeControl();
      List<String> list = new ArrayList<>();
      list.add(Config.BLANK_LABEL);
      list.add(Config.OTHER_LABEL);
      for(String s : getTripManager().getAllLureTypes())
         list.add(s);
      _lureTypeMap = new HashMap<String,Integer>();
      for(int i = 0; i < list.size(); ++i)
         _lureTypeMap.put(list.get(i), i);
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, list);
      editor.setAdapter(spinnerArrayAdapter);
      editor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}

         @Override
         public void onItemSelected(
            AdapterView<?> parent, View view, int pos, long id)
         {
            final String type = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(type))
            {
               final EditText input = new EditText(getContext());
               new AlertDialog.Builder(getContext())
                .setTitle("Enter New Lure Type")
                .setView(input)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                  {
                     public void onClick(DialogInterface dialog, int whichButton)
                     {
                        String type = input.getText().toString();
                        Fish f = getSelectedFish();
                        if(null != f)
                           f.setLureType(type);
                     }
                  })
               .setNegativeButton(android.R.string.no, null).show();
            }
            else
            {
               Fish f = getSelectedFish();
               if(null != f)
                  f.setLureType(type);
            }
         }
     });
   }

   private void initLureSizeEditor()
   {
      Spinner editor = getLureSizeControl();
      List<String> list = new ArrayList<>();
      list.add(Config.BLANK_LABEL);
      list.add(Config.OTHER_LABEL);
      for(String s : getTripManager().getAllLureSizes())
         list.add(s);
      _lureSizeMap = new HashMap<String,Integer>();
      for(int i = 0; i < list.size(); ++i)
         _lureSizeMap.put(list.get(i), i);
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, list);
      editor.setAdapter(spinnerArrayAdapter);
      editor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}

         @Override
         public void onItemSelected(
            AdapterView<?> parent, View view, int pos, long id)
         {
            final String size = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(size))
            {
               final EditText input = new EditText(getContext());
               new AlertDialog.Builder(getContext())
                .setTitle("Enter New Lure Size")
                .setView(input)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                  {
                     public void onClick(DialogInterface dialog, int whichButton)
                     {
                        String size = input.getText().toString();
                        Fish f = getSelectedFish();
                        if(null != f)
                           f.setLureSize(size);
                     }
                  })
               .setNegativeButton(android.R.string.no, null).show();
            }
            else
            {
               Fish f = getSelectedFish();
               if(null != f)
                  f.setLureSize(size);
            }
         }
     });
   }

   private void initLureColorEditor()
   {
      Spinner editor = getLureColorControl();
      List<String> list = new ArrayList<>();
      list.add(Config.BLANK_LABEL);
      list.add(Config.OTHER_LABEL);
      for(String s : getTripManager().getAllLureColors())
         list.add(s);
      _lureColorMap = new HashMap<String,Integer>();
      for(int i = 0; i < list.size(); ++i)
         _lureColorMap.put(list.get(i), i);
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, list);
      editor.setAdapter(spinnerArrayAdapter);
      editor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}

         @Override
         public void onItemSelected(
            AdapterView<?> parent, View view, int pos, long id)
         {
            final String color = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(color))
            {
               final EditText input = new EditText(getContext());
               new AlertDialog.Builder(getContext())
                .setTitle("Enter New Lure Color")
                .setView(input)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                  {
                     public void onClick(DialogInterface dialog, int whichButton)
                     {
                        String color = input.getText().toString();
                        Fish f = getSelectedFish();
                        if(null != f)
                           f.setLureColor(color);
                     }
                  })
               .setNegativeButton(android.R.string.no, null).show();
            }
            else
            {
               Fish f = getSelectedFish();
               if(null != f)
                  f.setLureColor(color);
            }
         }
     });
   }

   private void initLandedEditor()
   {
      Spinner editor = getLandedControl();
      List<Fish.CaughtState> itemList = new ArrayList<>();
      for(Fish.CaughtState s : getTripManager().getAllCaughtStates())
         itemList.add(s);
      ArrayAdapter<Fish.CaughtState> spinnerArrayAdapter = new ArrayAdapter<>(
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
            Fish.CaughtState s = (Fish.CaughtState) parent.getItemAtPosition(pos);
            Fish f = getSelectedFish();
            if(null != f)
               f.setCaughtState(s);
         }
      });
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
            Fish f = getSelectedFish();
            if(null != f)
               f.setWindDirection(dir);
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
            Fish f = getSelectedFish();
            if(null != f)
               f.setWindStrength(str);
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
            Fish f = getSelectedFish();
            if(null != f)
               f.setPrecipitation(precip);
         }
      });
   }

   private void initCoverEditor()
   {
      Spinner editor = getCoverControl();
      List<String> list = new ArrayList<>();
      list.add(Config.BLANK_LABEL);
      list.add(Config.OTHER_LABEL);
      for(String s : getTripManager().getAllCovers())
         list.add(s);
      _coverMap = new HashMap<String,Integer>();
      for(int i = 0; i < list.size(); ++i)
         _coverMap.put(list.get(i), i);
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, list);
      editor.setAdapter(spinnerArrayAdapter);
      editor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}

         @Override
         public void onItemSelected(
            AdapterView<?> parent, View view, int pos, long id)
         {
            final String cover = (String) parent.getItemAtPosition(pos);
            if(Config.OTHER_LABEL.equals(cover))
            {
               final EditText input = new EditText(getContext());
               new AlertDialog.Builder(getContext())
                .setTitle("Enter New Cover")
                .setView(input)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                  {
                     public void onClick(DialogInterface dialog, int whichButton)
                     {
                        String s = input.getText().toString();
                        Fish f = getSelectedFish();
                        if(null != f)
                           f.setCover(s);
                     }
                  })
               .setNegativeButton(android.R.string.no, null).show();
            }
            else
            {
               Fish f = getSelectedFish();
               if(null != f)
                  f.setCover(cover);
            }
         }
     });
   }


   private void initListeners()
   {
      getTripManager().addSelectedTripChangeListener(this);
      getTripManager().addTripListChangeListener(this);
      getTripManager().addSelectedFishChangeListener(this);
      getTripManager().addSpeciesChangeListener(this);
      getTripManager().addCoverChangeListener(this);
      getTripManager().addLureTypeChangeListener(this);
      getTripManager().addLureColorChangeListener(this);
      getTripManager().addLureSizeChangeListener(this);

      getLengthControl().addTextChangedListener(new TextWatcher()
      {
         @Override
         public void afterTextChanged(Editable e)
         {
            Fish f = getSelectedFish();
            if(null != f)
            {
               String s = e.toString();
               if(s.length() > 0)
                  f.setLength(new FishLength(Integer.parseInt(s)));
            }
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {}
      });

      getWeightControl().addTextChangedListener(new TextWatcher()
      {
         @Override
         public void afterTextChanged(Editable e)
         {
            Fish f = getSelectedFish();
            if(null != f)
            {
               String s = e.toString();
               if(s.length() > 0)
                  f.setWeight(new FishWeight(Integer.parseInt(s)));
            }
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
            Fish f = getSelectedFish();
            if(null != f)
            {
               String s = e.toString();
               if(s.length() > 0)
                  f.setAirTemp(new Temperature(Integer.parseInt(s)));
            }
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
            Fish f = getSelectedFish();
            if(null != f)
            {
               String s = e.toString();
               if(s.length() > 0)
                  f.setWaterTemp(new Temperature(Integer.parseInt(s)));
            }
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {}
      });

      getWaterDepthControl().addTextChangedListener(new TextWatcher()
      {
         @Override
         public void afterTextChanged(Editable e)
         {
            Fish f = getSelectedFish();
            if(null != f)
            {
               String s = e.toString();
               if(s.length() > 0)
                  f.setWaterDepth(new WaterDepth(Double.valueOf(s)));
            }
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
            Fish f = getSelectedFish();
            if(null != f)
            {
               String s = e.toString();
               if(s.length() > 0)
                  f.setWindSpeed(new Speed(Integer.decode(s)));
            }
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
            Fish f = getSelectedFish();
            if(null != f)
            {
               String s = e.toString();
               f.setNotes(s);
            }
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {}
      });

      getAudioNoteControl().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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
      getSelectedFish().addAudioNote(note);
      Spinner spinner = getAudioNoteControl();
      ArrayAdapter<AudioNote> adapter = (ArrayAdapter<AudioNote>) spinner.getAdapter();
      adapter.add(note);
      spinner.setSelection(adapter.getCount() - 1);
      updateAudioNoteButtonState();
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
      getAirTempUnitsLabel().setText(units);
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
      getWaterTempUnitsLabel().setText(units);
   }

   private void setCaughtTime(Date d)
   {
      if(null == d)
      {
         getCaughtTimeControl().setText("");
         getCaughtDateControl().setText("");
      }
      else
      {
         getCaughtDateControl().setText(Config.getDateFormat().format(d));
         getCaughtTimeControl().setText(Config.getTimeFormat().format(d));
      }
   }

   private void setWindSpeed(Speed speed)
   {
      String val = "";
      String units = Config.getDefaultWindSpeedUnits().toString();
      if(null != speed)
      {
         val =  "" + speed.getSpeed();
         units = speed.getUnits().toString();
      }
      getWindSpeedControl().setText(val);
      getWindSpeedUnitsLabel().setText(units);
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

   private void setLength(FishLength len)
   {
      String val = "";
      String units = Config.getDefaultFishLengthUnits().toString();
      if(null != len)
      {
         val =  "" + len.getLength();
         units = len.getUnits().toString();
      }
      getLengthControl().setText(val);
      getLengthUnitsLabel().setText(units);
   }

   private void setLure(Lure lure)
   {
      if(null == lure)
      {
         setLureType(null);
         setLureColor(null);
         setLureSize(null);
      }
      else
      {
         setLureType(lure.getType());
         setLureColor(lure.getColor());
         setLureSize(lure.getSize());
      }
   }

   private void setGpsLocation(LatLon loc)
   {
      // TODO - to be implemented
   }

   private void setLanded(Fish.CaughtState s)
   {
      // TODO - to be implemented
   }

   private void setWeight(FishWeight w)
   {
      String val = "";
      String units = Config.getDefaultFishWeightUnits().toString();
      if(null != w)
      {
         val =  "" + w.getWeight();
         units = w.getUnits().toString();
      }
      getWeightControl().setText(val);
      getWeightUnitsLabel().setText(units);
   }

   private void setWaterDepth(WaterDepth d)
   {
      String val = "";
      String units = Config.getDefaultWaterDepthUnits().toString();
      if(null != d)
      {
         val =  "" + d.getDepth();
         units = d.getUnits().toString();
      }
      getWaterDepthControl().setText(val);
      getWaterDepthUnitsLabel().setText(units);
   }

   private void setSpecies(String species)
   {
      if(null == species)
      {
         getSpeciesControl().setSelection(0);
            // NOTE:  0 is the blank element added first to the list
      }
      else
      {
         if(_speciesMap.containsKey(species))
         {
            int pos = _speciesMap.get(species);
            getSpeciesControl().setSelection(pos);
         }
         else
         {
            getTripManager().addSpecies(species);
               // event handling will trigger update of _speciesMap and
               // set the spinner selection
         }
      }
   }

   private void setLureType(String type)
   {
      if(null == type)
      {
         getLureTypeControl().setSelection(0);
            // NOTE:  0 is the "no-selection" element
      }
      else
      {
         if(_lureTypeMap.containsKey(type))
         {
            int pos = _lureTypeMap.get(type);
            getLureTypeControl().setSelection(pos);
         }
         else
         {
            getTripManager().addLureType(type);
               // event handling will trigger update of _lureTypeMap and
               // set the spinner selection
         }
      }
   }

   private void setLureColor(String color)
   {
      if(null == color)
      {
         getLureColorControl().setSelection(0);
            // NOTE: 0 is the "no-selection" element
      }
      else
      {
         if(_lureColorMap.containsKey(color))
         {
            int pos = _lureColorMap.get(color);
            getLureColorControl().setSelection(pos);
         }
         else
         {
            getTripManager().addLureColor(color);
               // event handling will trigger update of _lureColorMap and
               // set the spinner selection
         }
      }
   }

   private void setLureSize(String size)
   {
      if(null == size)
      {
         getLureSizeControl().setSelection(0);
            // NOTE:  0 is the "no-selection" element
      }
      else
      {
         if(_lureSizeMap.containsKey(size))
         {
            int pos = _lureSizeMap.get(size);
            getLureSizeControl().setSelection(pos);
         }
         else
         {
            getTripManager().addLureSize(size);
               // event handling will trigger update of _lureSizeMap and
               // set the spinner selection
         }
      }
   }

   private void setCover(String cover)
   {
      if(null == cover)
      {
         getCoverControl().setSelection(0);
            // NOTE:  0 is the "no-selection" element
      }
      else
      {
         if(_coverMap.containsKey(cover))
         {
            int pos = _coverMap.get(cover);
            getCoverControl().setSelection(pos);
         }
         else
         {
            getTripManager().addCover(cover);
               // event handling will trigger update of _coverMap and
               // set the spinner selection
         }
      }
   }

   private void populateAudioNoteList(Fish f)
   {
      Spinner spinner = getAudioNoteControl();
      List<AudioNote> noteList = new ArrayList<>();
      for(AudioNote note : f.getAudioNotes().values())
         noteList.add(note);
      ArrayAdapter<AudioNote> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, noteList);
      spinner.setAdapter(spinnerArrayAdapter);
     updateAudioNoteButtonState();
   }

   private void clearAudioNotes()
   {
      Spinner spinner = getAudioNoteControl();
      ArrayAdapter<AudioNote> adapter = (ArrayAdapter<AudioNote>) spinner.getAdapter();
      if(null != adapter)
         adapter.clear();
      spinner.setSelection(-1);
      updateAudioNoteButtonState();
   }

   private void updateAudioNoteButtonState()
   {
      Spinner spinner = getAudioNoteControl();
      getEditAudioNoteButton().setEnabled(spinner.getSelectedItem() != null);
      getPlayAudioNoteButton().setEnabled(spinner.getSelectedItem() != null);
   }


   private TextView getTitleView()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_title);
   }

   private TextView getAirTempUnitsLabel()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_air_temp_units);
   }

   private TextView getWaterTempUnitsLabel()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_water_temp_units);
   }

   private EditText getAirTempControl()
   {
      return (EditText) _rootView.findViewById(R.id.fish_detail_air_temp_entry);
   }

   private EditText getWaterTempControl()
   {
      return (EditText) _rootView.findViewById(R.id.fish_detail_water_temp_entry);
   }

   private EditText getNotesControl()
   {
      return (EditText) _rootView.findViewById(R.id.fish_detail_notes_field);
   }

   private Button getAddAudioNoteButton()
   {
      return (Button) _rootView.findViewById(R.id.fish_details_add_audio_note_button);
   }

   private Button getEditAudioNoteButton()
   {
      return (Button) _rootView.findViewById(R.id.fish_details_edit_audio_note_button);
   }

   private Button getPlayAudioNoteButton()
   {
      return (Button) _rootView.findViewById(R.id.fish_details_play_audio_note_button);
   }

   private EditText getWindSpeedControl()
   {
      return (EditText) _rootView.findViewById(R.id.fish_detail_wind_speed_entry);
   }

   private TextView getWindSpeedUnitsLabel()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_wind_speed_units);
   }

   private TextView getCaughtDateControl()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_caught_date);
   }

   private TextView getCaughtTimeControl()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_caught_time);
   }

   private Spinner getSpeciesControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_species_spinner);
   }

   private Spinner getLureTypeControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_lure_spinner);
   }

   private Spinner getLureSizeControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_lure_size_spinner);
   }

   private Spinner getLureColorControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_lure_color_spinner);
   }

   private Spinner getAudioNoteControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_audio_notes_list);
   }

   private Spinner getWindDirectionControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_wind_dir_spinner);
   }

   private Spinner getWindStrengthControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_wind_strength_spinner);
   }

   private Spinner getPrecipitationControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_precip_spinner);
   }

   private EditText getWaterDepthControl()
   {
      return (EditText)  _rootView.findViewById(R.id.fish_detail_water_depth_entry);
   }

   private TextView getWaterDepthUnitsLabel()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_water_depth_units);
   }

   private TextView getGpsControl()
   {
      return (TextView)  _rootView.findViewById(R.id.fish_detail_gps_loc_value);
   }

   private Spinner getCoverControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_cover_spinner);
   }

   private EditText getLengthControl()
   {
      return (EditText)  _rootView.findViewById(R.id.fish_detail_length_entry);
   }

   private TextView getLengthUnitsLabel()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_length_units);
   }

   private EditText getWeightControl()
   {
      return (EditText)  _rootView.findViewById(R.id.fish_detail_weight_entry);
   }

   private TextView getWeightUnitsLabel()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_weight_units);
   }

   private Spinner getLandedControl()
   {
      return (Spinner) _rootView.findViewById(R.id.fish_detail_landed_spinner);
   }

}
