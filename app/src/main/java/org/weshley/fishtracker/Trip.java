package org.weshley.fishtracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Trip
{
   public enum Direction
      {N, NE, E, SE, S, SW, W, NW}
   public enum Strength
      {Unknown, Intermittent, Steady, Growing, Waning}
   public enum Precipitation
      {Unknown, Clear, PartlyCloudy, MostlyCloudy, SolidClouds, LightRain, HeavyRain}

   private Date _start = null;
   private Date _end = null;
   private String _location = null;
   private boolean _trackingLocation = false;
   private List<LatLon> _path = null;
      // TODO: when _trackLocation is turned on, need to start a thread that periodically stores
      //       a new location in _path. stop when trip stops or when turned off
   private int _lakeLevel = -1;
   private int _airTemp = -1;
   private boolean _trackingTemp = false;
   private List<Integer> _tempProfile = null;
      // TODO: when _trackTemp is turned on, need to start a thread that periodically stores
      //       a new temp in _tempProfile.  stop when trip stops or when turned off.
   private int _waterTemp = -1;
   private int _windSpeed = -1;
   private Direction _windDirection = null;
   private Strength _windStrength = null;
   private Precipitation _precip = null;
   private String _notes = null;
   private Map<String, AudioNote> _audioNotes = null;
   private List<Fish> _fishCaught = null;
   private String _cachedLabel = null;


   public Trip()
   {
      _start = new Date();
      initPropertiesFromMostRecentTrip();
      initializeAirTempFromSensor();
      _windSpeed = 0;
      _windDirection = null;
      _windStrength = Strength.Unknown;
      _precip = Precipitation.Unknown;
      _notes = "";
      _audioNotes = new TreeMap<String,AudioNote>();
      _fishCaught = new ArrayList<Fish>();
   }

   public String getLabel()
   {
      if(null != _cachedLabel)
         return _cachedLabel;

      StringBuilder sb = new StringBuilder();
      if(null == _start)
         sb.append("<unknown time>");
      else
         sb.append(Config.getDateFormat().format(_start));
      sb.append(" @ ");
      if(StringUtils.isEmpty(_location))
         sb.append("<unknown location>");
      else
         sb.append(_location);

      _cachedLabel = sb.toString();
      return _cachedLabel;
   }

   public void finalize()
   {
      // TODO: what else do we need to do here when a trip ends?  persist the trip (or do that more frequently)?
      _end = new Date();
      stopTempTracking();
      stopLocationTracking();
   }

   public void addAudioNote(AudioNote note)
   {
      _audioNotes.put(note.getLabel(), note);
   }

   public void deleteAudioNote(String noteLabel)
   {
      _audioNotes.remove(noteLabel);
   }

   public Map<String, AudioNote> getAudioNotes()
   {
      return _audioNotes;
   }

   public List<Fish> getFishCaught()
   {
      return _fishCaught;
   }

   public void newFish()
   {
      _fishCaught.add(new Fish());
   }

   public void deleteFish(Fish f)
   {
      _fishCaught.remove(f);
   }

   public void setPrecipitation(Precipitation precip)
   {
      _precip = precip;
   }

   public Precipitation getPrecipitation()
   {
      return _precip;
   }

   public String getNotes()
   {
      return _notes;
   }

   public void setNotes(String s)
   {
      _notes = s;
   }

   public void setWindSpeed(int speed)
   {
      _windSpeed = speed;
   }

   public int getWindSpeed()
   {
      return _windSpeed;
   }

   public void setWindDirection(Direction dir)
   {
      _windDirection = dir;
   }

   public Direction getWindDirection()
   {
      return _windDirection;
   }

   public void setWindStrength(Strength str)
   {
      _windStrength = str;
   }

   public Strength getWindStrength()
   {
      return _windStrength;
   }

   public void setStart(Date dt)
   {
      _start = dt;
      _cachedLabel = null;
      getTripManager().fireTripListChanged(this);
   }

   public Date getStart()
   {
      return _start;
   }

   public void setEnd(Date dt)
   {
      _end = dt;
   }

   public Date getEnd()
   {
      return _end;
   }

   public void setLocation(String loc)
   {
      _location = loc;
      _cachedLabel = null;
      getTripManager().addLocation(loc);
      getTripManager().fireTripListChanged(this);
   }

   public String getLocation()
   {
      return _location;
   }

   public void setLakeLevel(int lev)
   {
      _lakeLevel = lev;
   }

   public int getLakeLevel()
   {
      return _lakeLevel;
   }

   public void setWaterTemp(int temp)
   {
      _waterTemp = temp;
   }

   public int getWaterTemp()
   {
      return _waterTemp;
   }

   public void setAirTemp(int temp)
   {
      _airTemp = temp;
   }

   public int getAirTemp()
   {
      return _airTemp;
   }

   public void setTrackingLocation(boolean b)
   {
      if(b)
         startLocationTracking();
      else
         stopLocationTracking();
   }

   public boolean isTrackingLocation()
   {
      return _trackingLocation;
   }

   public void setTrackingTemp(boolean b)
   {
      if(b)
         startTempTracking();
      else
         stopTempTracking();
   }

   public boolean isTrackingTemp()
   {
      return _trackingTemp;
   }

   public int getTotalFishCaught()
   {
      return _fishCaught.size();
   }

   public Map<String,Integer> getAllSpeciesCounts()
   {
      Map<String,Integer> counts = new TreeMap<String,Integer>();
      for(Fish f : _fishCaught)
      {
         String species = f.getSpecies();
         if(counts.containsKey(species))
            counts.put(species, counts.get(species) + 1);
         else
            counts.put(species, 1);
      }
      return counts;
   }

   public Fish getMostRecentCaughtFish()
   {
      // TODO: this should really look at catch time for all fish and not assume the list is in order
      if(_fishCaught.isEmpty())
         return null;
      else
         return _fishCaught.get(_fishCaught.size() - 1);
   }

   public String toString()
   {
      return "Trip(" + _start + " @ " + _location;
   }

   private void initializeAirTempFromSensor()
   {
      // TODO: figure out how to implement this
   }

   private void initPropertiesFromMostRecentTrip()
   {
      Trip lastTrip = TripManager.instance().getMostRecentTrip();
      if((null == lastTrip) || (lastTrip == this))
         return;
      setLocation(lastTrip.getLocation());
      setLakeLevel(lastTrip.getLakeLevel());
      setWaterTemp(lastTrip.getWaterTemp());
   }

   private void startTempTracking()
   {
      // TODO: start temp tracking thread()
      _trackingTemp = true;
      if(null == _tempProfile)
         _tempProfile = new ArrayList<Integer>();
   }

   private void stopTempTracking()
   {
      // TODO: stop temp tracking thread
      _trackingTemp = false;
   }

   private void startLocationTracking()
   {
      // TODO: start location tracking thread()
      _trackingLocation = true;
      if(null == _path)
         _path = new ArrayList<LatLon>();
   }

   private void stopLocationTracking()
   {
      // TODO: stop location tracking thread()
      _trackingLocation = false;
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }

}
