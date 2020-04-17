package org.weshley.fishtracker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TripManager
{
   // TODO: Need to persist and load all trips, locations, covers, species,
   //       and other state

   // TODO: add more default covers
   private static final String[] DEFAULT_COVERS =
      { "Trees", "Weeds", "Rocks",  "Clear Bottom", "Open Water" };

   // TODO: add more default species
   private static final String[] DEFAULT_SPECIES =
      { "Largemouth Bass", "Spotted Bass", "Striped Bass", "Bluegill", "Crappie", "Blue Catfish",
        "Channel Catfish" };

   private static final boolean INIT_TEST_DATA = true;
   private static TripManager _instance = null;
   private Set<String> _allLocations = new TreeSet<>();
   private Set<String> _allSpecies = new TreeSet<>();
   private Set<String> _allCovers = new TreeSet<>();
   private Trip _activeTrip = null;
      // _activeTrip is the trip that is open that caught fish, tracks, and other events
      // are attached to
   private Trip _selectedTrip = null;
      // _selectedTrip is the trip current being displayed in the Trip Detail tab.  often
      // the _activeTrip, but not always
   private List<Trip> _trips = new ArrayList<>();
   private Set<TripListChangeListener> _tripListChangeListeners = new HashSet<>();
   private Set<SelectedTripChangeListener> _selectedTripChangeListeners = new HashSet<>();
   private Set<TripLocationsChangeListener> _tripLocationsChangeListeners = new HashSet<>();
   private Set<SpeciesChangeListener> _speciesChangeListeners = new HashSet<>();
   private Set<CoverChangeListener> _coverChangeListeners = new HashSet<>();
   private Fish _selectedFish = null;
      // _selectedFish is the fish currently displayed in the Fish Detail tab, maybe after
      // selecting a fish in the Fish list tab or clicking on Caught Fish button
   private Set<SelectedFishChangeListener> _selectedFishChangeListeners = new HashSet<SelectedFishChangeListener>();

   private static void initTestData()
   {
      Object[][] data =
      {
         // trip-start, trip-end, location, lake level, air-temp, water-temp, wind-speed, wind-dir, wind-strength, precip, notes
         { "2018/05/09 9:00 AM", "2018/05/09 10:00 AM", "Lake Hartwell", 660, 70, 60, 8, "SW", "Steady", "Clear", "line 1\nline 2" },
         { "2019/07/09 2:00 PM", "2019/07/09 4:00 PM", "Lake Hartwell", 661, 71, 61, 9, "NW", "Growing", "LightRain", "line 3\nline 4" },
         { "2019/06/02 12:00 AM", "2019/06/02 2:00 PM", "Lake Hartwell", 662, 72, 62, 10, "E", "Waning", "PartlyCloudy", "line 5\nline 6" }
      };

      DateFormat fmt = Config.getDateTimeFormat();
      Calendar cal = Calendar.getInstance();
      int batches = 1;
      Trip activeTrip = null;
      for(int b = 0; b < batches; ++b)
      {
         for(Object[] props : data)
         {
            try
            {
               Trip t = new Trip();
               Date startDt = fmt.parse((String) props[0]);
               cal.setTime(startDt);
               if(b > 0)
                  cal.add(Calendar.DAY_OF_MONTH, b);
               t.setStart(cal.getTime());
               Date endDt = fmt.parse((String) props[1]);
               cal.setTime(endDt);
               if(b > 0)
                  cal.add(Calendar.DAY_OF_MONTH, b);
               t.setEnd(cal.getTime());
               t.setLocation((String) props[2]);
               t.setLakeLevel((int) props[3]);
               t.setAirTemp(new Temperature((int) props[4]));
               t.setWaterTemp(new Temperature((int) props[5]));
               t.setWindSpeed(new Speed((int) props[6]));
               t.setWindDirection(Trip.Direction.valueOf((String) props[7]));
               t.setWindStrength(Trip.WindStrength.valueOf((String) props[8]));
               t.setPrecipitation(Trip.Precipitation.valueOf((String) props[9]));
               t.setNotes((String) props[10]);
               _instance._trips.add(t);
               if(null == activeTrip)
                  activeTrip = t;
            }
            catch(Exception ex)
            {
               Logger.error("Unexpeced error generating demo data", ex);
               ex.printStackTrace();
            }
         }
         _instance.setActiveTrip(activeTrip);
      }
   }

   public static TripManager instance()
   {
      if(null == _instance)
      {
         _instance = new TripManager();
         _instance.initAllLocations();
         if(INIT_TEST_DATA)
           initTestData();
      }
      return _instance;
   }

   public void addSpeciesChangeListener(SpeciesChangeListener lsnr)
   {
      _speciesChangeListeners.add(lsnr);
   }

   public void addCoverChangeListener(CoverChangeListener lsnr)
   {
      _coverChangeListeners.add(lsnr);
   }

   public void addTripListChangeListener(TripListChangeListener listener)
   {
      _tripListChangeListeners.add(listener);
   }

   public void addSelectedTripChangeListener(SelectedTripChangeListener listener)
   {
      _selectedTripChangeListeners.add(listener);
   }

   public void addSelectedFishChangeListener(SelectedFishChangeListener listener)
   {
      _selectedFishChangeListeners.add(listener);
   }

   public void addTripLocationsChangeListener(TripLocationsChangeListener listener)
   {
      _tripLocationsChangeListeners.add(listener);
   }

   public List<Trip> getTrips()
   {
      return _trips;
   }

   public String getDisplayableTripLabel(Trip t)
   {
      if(null == t)
         return "NO TRIP!!!!";

      // TODO - Find a better way to mark the active trip other than the “** ” tag??
      String tripLabel = t.getLabel();
      if(isActiveTrip(t))
         tripLabel = "**  " + tripLabel;
      return tripLabel;
   }

   public boolean hasActiveTrip()
   {
      return null != _activeTrip;
   }

   public Trip getActiveTrip()
   {
      return _activeTrip;
   }

   public boolean isActiveTrip(Trip t)
   {
      return _activeTrip == t;
   }

   public Trip getSelectedTrip()
   {
      return _selectedTrip;
   }

   public void setSelectedTrip(Trip t)
   {
      if(_selectedTrip == t)
         return;
      _selectedTrip = t;
      fireSelectedTripChanged(_selectedTrip);
   }

   public Trip getMostRecentTrip()
   {
      // TODO: this should really find the trip with the most recent start date and not assume
      //       the _trips list is in order
      if(_trips.isEmpty())
         return null;
      else
         return _trips.get(_trips.size() - 1);
   }

   public void setActiveTrip(Trip t)
   {
      if(_activeTrip == t)
         return;
      if(!_trips.contains(t))
         _trips.add(t);
      endTrip();
      _activeTrip = t;
      _selectedTrip = _activeTrip;
      fireTripListChanged(_activeTrip);
      fireSelectedTripChanged(_selectedTrip);
   }

   public Trip startTrip()
   {
      endTrip();
      _activeTrip = new Trip();
      _trips.add(_activeTrip);
      _selectedTrip = _activeTrip;
      fireTripListChanged(_activeTrip);
      fireSelectedTripChanged(_selectedTrip);
      return _activeTrip;
   }

   public void endTrip()
   {
      if(null != _activeTrip)
         _activeTrip.finalize();
      Trip oldTrip = _activeTrip;
      _activeTrip = null;
      fireTripListChanged(oldTrip);
   }

   public void resumeTrip(Trip oldTrip)
   {
      if(oldTrip == _activeTrip)
         return;

      if(null != _activeTrip)
         _activeTrip.finalize();
      fireTripListChanged(_activeTrip);

      // should never happen, but just in case....
      if(!_trips.contains(oldTrip))
         _trips.add(oldTrip);
      _activeTrip = oldTrip;
      fireTripListChanged(_activeTrip);

      _selectedTrip = _activeTrip;
      fireSelectedTripChanged(_selectedTrip);
   }

   public void deleteTrip(Trip t)
   {
      _trips.remove(t);
      if(t == _activeTrip)
         _activeTrip = null;
      fireTripListChanged(t);

      if(_selectedTrip == t)
      {
         _selectedTrip = _activeTrip;
         fireSelectedTripChanged(_selectedTrip);
      }
   }

   public void caughtNewFish()
   {
      _selectedFish =  getSelectedTrip().newFish();
      fireSelectedFishChanged(_selectedFish);
   }

   public Fish getSelectedFish()
   {
      return _selectedFish;
   }

   // TODO - add interface for deleting a fish

   public void addLocation(String location)
   {
      if((null != location) && !location.isEmpty() && !_allLocations.contains(location))
      {
         _allLocations.add(location);
         fireTripLocationsChanged(location);
      }
   }

   public Set<String> getAllLocations()
   {
      return _allLocations;
   }

   public void addSpecies(String species)
   {
      if((null != species) && !species.isEmpty() && !_allSpecies.contains(species))
      {
         _allSpecies.add(species);
         fireSpeciesChanged(species);
      }
   }

   public Set<String> getAllSpecies()
   {
      if((null == _allSpecies) || _allSpecies.isEmpty())
      {
         _allSpecies = new TreeSet<String>();
         // TODO:  read persisted list and add all values to _allSpecies
         for(String s : DEFAULT_SPECIES)
            _allSpecies.add(s);
      }
      return _allSpecies;
   }

   public void addCover(String cover)
   {
      if((null != cover) && !cover.isEmpty() && !_allCovers.contains(cover))
      {
         _allCovers.add(cover);
         fireCoversChanged(cover);
      }
   }

   public Set<String> getAllCovers()
   {
      if((null == _allCovers) || _allCovers.isEmpty())
      {
         _allCovers = new TreeSet<String>();
         // TODO:  read persisted list and add all values to _allCovers
         for(String s : DEFAULT_COVERS)
            _allCovers.add(s);
      }
      return _allCovers;
   }

   public Trip.Direction[] getAllWindDirections()
   {
      return Trip.Direction.values();
   }

   public Trip.WindStrength[] getAllWindStrengths()
   {
      return Trip.WindStrength.values();
   }

   public Trip.Precipitation[] getAllPrecipitations()
   {
      return Trip.Precipitation.values();
   }

   private TripManager()
   {
   }

   private void initAllLocations()
   {
      // TODO: this should be reading from some persistent storage, but for now....
      _allLocations.add("Lake Hartwell");
      _allLocations.add("Oliver Lake");
   }

   void fireTripListChanged(Trip t)
   {
      TripListChangeEvent ev = new TripListChangeEvent(t);
      for(TripListChangeListener lsnr : _tripListChangeListeners)
         lsnr.tripListChanged(ev);
   }

   void fireSelectedTripChanged(Trip t)
   {
      SelectedTripChangeEvent ev = new SelectedTripChangeEvent(t);
      for(SelectedTripChangeListener lsnr : _selectedTripChangeListeners)
         lsnr.selectedTripChanged(ev);
   }

   void fireSelectedFishChanged(Fish f)
   {
      SelectedFishChangeEvent ev = new SelectedFishChangeEvent(f);
      for(SelectedFishChangeListener lsnr : _selectedFishChangeListeners)
         lsnr.selectedFishChanged(ev);
   }

   void fireTripLocationsChanged(String location)
   {
      TripLocationsChangeEvent ev = new TripLocationsChangeEvent(location);
      for(TripLocationsChangeListener lsnr : _tripLocationsChangeListeners)
         lsnr.tripLocationsChanged(ev);
   }

   void fireSpeciesChanged(String species)
   {
      SpeciesChangeEvent ev = new SpeciesChangeEvent(species);
      for(SpeciesChangeListener lsnr : _speciesChangeListeners)
         lsnr.speciesChanged(ev);

   }

   void fireCoversChanged(String cover)
   {
      CoverChangeEvent ev = new CoverChangeEvent(cover);
      for(CoverChangeListener lsnr : _coverChangeListeners)
         lsnr.coverChanged(ev);

   }

}
