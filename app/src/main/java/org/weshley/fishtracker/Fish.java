package org.weshley.fishtracker;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Fish
{
   public enum CaughtState { LANDED, HOOKED, HIT }

   // TODO: add more default covers
   private static final String[] DEFAULT_COVERS =
      { "Trees", "Weeds", "Rocks",  "Clear Bottom", "Open Water" };

   // TODO: add more default species
   private static final String[] DEFAULT_SPECIES =
      { "Largemouth Bass", "Spotted Bass", "Striped Bass", "Bluegill", "Crappie", "Blue Catfish",
        "Channel Catfish" };

   private static Set<String> _allSpecies = null;
   private static Set<String> _allCovers = null;

   private CaughtState _caughtState = null;
   private Date _time = null;
   private LatLon _location = null;
   private Lure _lure = null;
   private String _species = null;
   private int _length = -1;
   private int _weight = -1;
   private int _airTemp = -1;
   private int _waterTemp = -1;
   private int _waterDepth = -1;
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
      _notes = "";
      _audioNotes = new TreeMap<String,AudioNote>();
      initializeFromLastCaughtFish();
   }

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
      Fish.addToAllCovers(s);
   }

   public String getCover()
   {
      return _cover;
   }

   public void setPicture(Photo p)
   {
      _picture = p;
   }

   public Photo getPicture()
   {
      return _picture;
   }

   public void setWaterDepth(int depth)
   {
      _waterDepth = depth;
   }

   private int getWaterDepth()
   {
      return _waterDepth;
   }

   public void setAirTemp(int temp)
   {
      _airTemp = temp;
   }

   public int getAirTemp()
   {
      return _airTemp;
   }

   public void setWaterTemp(int temp)
   {
      _waterTemp = temp;
   }

   public int getWaterTemp()
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

   public int getWeight()
   {
      return _weight;
   }

   public void setWeight(int w)
   {
      _weight = w;
   }

   public int getLength()
   {
      return _length;
   }

   public void setLength(int l)
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
      Fish.addToAllSpecies(s);
   }

   public void setLure(Lure l)
   {
      _lure = l;
   }

   public Lure getLure()
   {
      return _lure;
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

   private int getAirTempFromSensor()
   {
      // TODO: figure out how to get temp from phone sensor

      // if no temp from sensor, try to use trip air temp
      Trip t = TripManager.instance().getCurrentTrip();
      if(null != t)
         return t.getAirTemp();
      return -1;
   }

   private int getCurrentTripWaterTemp()
   {
      Trip t = TripManager.instance().getCurrentTrip();
      if(null != t)
         return t.getWaterTemp();
      return -1;
   }

   private Fish getLastCaughtFish()
   {
      Trip t = TripManager.instance().getCurrentTrip();
      if(null == t)
         return null;
      return t.getMostRecentCaughtFish();
   }

   private static Set<String> getAllSpecies()
   {
      if(null == _allSpecies)
      {
         _allSpecies = new TreeSet<String>();
         // TODO:  read persisted list and add all values to _allSpecies
         for(String s : DEFAULT_SPECIES)
            _allSpecies.add(s);
      }
      return _allSpecies;
   }

   private static void addToAllSpecies(String s)
   {
      // TODO:  persist this list when modified
      if((null != s) && !s.isEmpty())
         getAllSpecies().add(s);
   }

   private static Set<String> getAllCovers()
   {
      if(null == _allCovers)
      {
         _allCovers = new TreeSet<String>();
         // TODO:  read persisted list and add all values to _allCovers
         for(String s : DEFAULT_COVERS)
            _allCovers.add(s);
      }
      return _allCovers;
   }

   private static void addToAllCovers(String s)
   {
      // TODO:  persist this list when modified
      if((null != s) && !s.isEmpty())
         getAllCovers().add(s);
   }
}
