package org.weshley.fishtracker;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class Fish
{
   public enum CaughtState
   { UNDEFINED { public String toString() { return Config.BLANK_LABEL; }},
     Landed, Hooked, Hit
   }

   private CaughtState _caughtState = CaughtState.UNDEFINED;
   private Date _time = null;
   private LatLon _location = null;
   private Lure _lure = new Lure();
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
      _caughtState = CaughtState.Landed;
      _time = new Date();
      _location = getLatLonFromSensor();
      _airTemp = getMostRecentAirTemp();
      _waterTemp = getMostRecentWaterTemp();
      setMostRecentWindData();
      _precip = getMostRecentPrecipitation();
      _notes = "";
      _audioNotes = new TreeMap<String,AudioNote>();

      // initialize some data from last caught fish
      Fish f = getLastCaughtFish();
      if(null != f)
      {
         setLure(f.getLure());
         setSpecies(f.getSpecies());
         setWaterDepth(f.getWaterDepth());
         setCover(f.getCover());
      }
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
      if((null == s) || s.isEmpty())
      {
         _cover = null;
      }
      else
      {
         _cover = s;
         getTripManager().addCover(s);
      }
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
      if((s == null) || s.isEmpty())
      {
         _species = null;
      }
      else
      {
         _species = s;
         getTripManager().addSpecies(s);
      }
   }

   public void setLure(Lure l)
   {
      _lure = l;
      getTripManager().addLureType(l.getType());
      getTripManager().addLureColor(l.getColor());
      getTripManager().addLureSize(l.getSize());
   }

   public Lure getLure()
   {
      return _lure;
   }

   public void setLureType(String type)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setType(type);
   }

   public void setLureColor(String color)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setColor(color);
   }

   public void setLureSize(String size)
   {
      if(null == _lure)
         _lure = new Lure();
      _lure.setSize(size);
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }

   private void setMostRecentWindData()
   {
      // set wind speed, direction and strength from most recent
      // caught fish or trip data
      Fish f = getLastCaughtFish();
      if(null != f)
      {
         setWindSpeed(f.getWindSpeed());
         setWindDirection(f.getWindDirection());
         setWindStrength(f.getWindStrength());
         return;
      }

      Trip t = getActiveTrip();
      if(null != t)
      {
         setWindSpeed(t.getWindSpeed());
         setWindDirection(t.getWindDirection());
         setWindStrength(t.getWindStrength());
         return;
      }
   }

   private Temperature getMostRecentAirTemp()
   {
      // TODO: figure out how to get temp from phone sensor

      // if no temp from sensor, try to use last caught fish temp
      // or trip air temp
      Fish f = getLastCaughtFish();
      if((null != f) && (null != f.getAirTemp()))
         return f.getAirTemp();

      Trip t = getActiveTrip();
      if(null != t)
         return t.getAirTemp();
      return null;
   }

   private LatLon getLatLonFromSensor()
   {
      // TODO:  figure out how to use the GPS sensor
      return null;
   }

   private Trip getActiveTrip()
   {
      return getTripManager().getActiveTrip();
   }

   private Temperature getMostRecentWaterTemp()
   {
      // use either last caught fish temp or trip temp
      Fish f = getLastCaughtFish();
      if((null != f) && (null != f.getWaterTemp()))
         return f.getWaterTemp();

      Trip t = getActiveTrip();
      if(null != t)
         return t.getWaterTemp();
      return null;
   }

   private Trip.Precipitation getMostRecentPrecipitation()
   {
      // use either last caught fish or trip data
      Fish f = getLastCaughtFish();
      if((null != f) && (null != f.getPrecipitation())
           && (Trip.Precipitation.UNDEFINED != f.getPrecipitation()))
      {
         return f.getPrecipitation();
      }

      Trip t = getActiveTrip();
      if((null != t) && (null != t.getPrecipitation())
           && (Trip.Precipitation.UNDEFINED != t.getPrecipitation()))
      {
         return t.getPrecipitation();
      }
      return null;
   }

   private Fish getLastCaughtFish()
   {
      Trip t = getActiveTrip();
      if(null == t)
         return null;
      return t.getMostRecentCaughtFish();
   }

}
