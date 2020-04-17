package org.weshley.fishtracker;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class Fish
{
   public enum CaughtState { LANDED, HOOKED, HIT }

   private CaughtState _caughtState = null;
   private Date _time = null;
   private LatLon _location = null;
   private Lure _lure = null;
   private String _species = null;
   private FishLength _length = null;
   private FishWeight _weight = null;
   private Temperature _airTemp = null;
   private Temperature _waterTemp = null;
   private Speed _windSpeed = null;
   private Trip.Direction _windDirection = null;
   private Trip.WindStrength _windStrength = null;
   private Trip.Precipitation _precip = null;
   private WaterDepth _waterDepth = null;
   private String _cover = null;
   private Photo _picture = null;
   private String _notes = null;
   private Map<String, AudioNote> _audioNotes = null;

   public Fish()
   {
      _caughtState = CaughtState.LANDED;
      _time = new Date();
      _location = new LatLon();
      _airTemp = getAirTempFromSensor();
      _waterTemp = getCurrentTripWaterTemp();
      _windSpeed = null;
      _windDirection = null;
      _windStrength = null;
      _precip = null;
      _waterDepth = null;
      _notes = "";
      _audioNotes = new TreeMap<String,AudioNote>();
      initializeFromLastCaughtFish();
   }

   public Date getCaughtTime()
   {
      return _time;
   }

   public void setCaughtTime(Date dt)
   {
      _time = dt;
   }

   public LatLon getLocation() { return _location; }

   public void setLocation(LatLon location) { _location = location; }

   public void setNotes(String s)
   {
      _notes = s;
   }

   public String getNotes()
   {
      return _notes;
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

   public void setCover(String s)
   {
      _cover = s;
      getTripManager().addCover(s);
   }

   public String getCover()
   {
      return _cover;
   }

   public void setWindSpeed(Speed speed)
   {
      _windSpeed = speed;
   }

   public Speed getWindSpeed()
   {
      return _windSpeed;
   }

   public void setWindDirection(Trip.Direction dir)
   {
      _windDirection = dir;
   }

   public Trip.Direction getWindDirection()
   {
      return _windDirection;
   }

   public void setWindStrength(Trip.WindStrength str)
   {
      _windStrength = str;
   }

   public Trip.WindStrength getWindStrength()
   {
      return _windStrength;
   }

   public void setPrecipitation(Trip.Precipitation precip)
   {
      _precip = precip;
   }

   public Trip.Precipitation getPrecipitation()
   {
      return _precip;
   }

   public void setPicture(Photo p)
   {
      _picture = p;
   }

   public Photo getPicture()
   {
      return _picture;
   }

   public void setWaterDepth(WaterDepth depth)
   {
      _waterDepth = depth;
   }

   public WaterDepth getWaterDepth()
   {
      return _waterDepth;
   }

   public void setAirTemp(Temperature temp)
   {
      _airTemp = temp;
   }

   public Temperature getAirTemp()
   {
      return _airTemp;
   }

   public void setWaterTemp(Temperature temp)
   {
      _waterTemp = temp;
   }

   public Temperature getWaterTemp()
   {
      return _waterTemp;
   }

   public CaughtState getCaughtState()
   {
      return _caughtState;
   }

   public void setCaughtState(CaughtState s)
   {
      _caughtState = s;
   }

   public FishWeight getWeight()
   {
      return _weight;
   }

   public void setWeight(FishWeight w)
   {
      _weight = w;
   }

   public FishLength getLength()
   {
      return _length;
   }

   public void setLength(FishLength l)
   {
      _length = l;
   }

   public String getSpecies()
   {
      return _species;
   }

   public void setSpecies(String s)
   {
      _species = s;
      getTripManager().addSpecies(s);
   }

   public void setLure(Lure l)
   {
      _lure = l;
   }

   public Lure getLure()
   {
      return _lure;
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }

   private void initializeFromLastCaughtFish()
   {
      Fish f = getLastCaughtFish();
      if(null != f)
      {
         _lure = f.getLure();
         _species = f.getSpecies();
         _waterDepth = f.getWaterDepth();
         _cover = f.getCover();
      }
   }

   private Temperature getAirTempFromSensor()
   {
      // TODO: figure out how to get temp from phone sensor

      // if no temp from sensor, try to use trip air temp
      Trip t = TripManager.instance().getActiveTrip();
      if(null != t)
         return t.getAirTemp();
      return null;
   }

   private Temperature getCurrentTripWaterTemp()
   {
      Trip t = TripManager.instance().getActiveTrip();
      if(null != t)
         return t.getWaterTemp();
      return null;
   }

   private Fish getLastCaughtFish()
   {
      Trip t = TripManager.instance().getActiveTrip();
      if(null == t)
         return null;
      return t.getMostRecentCaughtFish();
   }

}
