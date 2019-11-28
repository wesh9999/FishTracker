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
   // TODO: Need to persist and load all trips, locations, and other state

   private static final boolean INIT_TEST_DATA = true;
   private static TripManager _instance = null;
   private Set<String> _allLocations = new TreeSet<String>();
   private Trip _activeTrip = null;
      // _activeTrip is the trip that is open that caught fish, tracks, and other events
      // are attached to
   private Trip _selectedTrip = null;
      // _selectedTrip is the trip current being displayed in the Trip Detail tab.  often
      // the _activeTrip, but not always
   private List<Trip> _trips = new ArrayList<Trip>();
   private Set<TripListChangeListener> _tripListChangeListeners = new HashSet<TripListChangeListener>();
   private Set<SelectedTripChangeListener> _selectedTripChangeListeners = new HashSet<SelectedTripChangeListener>();


   private static void initTestData()
   {
      Object[][] data =
      {
         { "2018/05/09 9:00 AM", "2018/05/09 10:00 AM", "Lake Hartwell" },
         { "2019/07/09 2:00 PM", "2019/07/09 4:00 PM", "Lake Hartwell" },
         { "2019/06/02 12:00 AM", "2019/06/02 2:00 PM", "Lake Hartwell" }
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

   public void addTripListChangeListener(TripListChangeListener listener)
   {
      _tripListChangeListeners.add(listener);
   }

   public void addSelectedTripChangeListener(SelectedTripChangeListener listener)
   {
      _selectedTripChangeListeners.add(listener);
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

   public void addLocation(String location)
   {
      if((null != location) && !location.isEmpty())
         _allLocations.add(location);
   }

   public Set<String> getAllLocations()
   {
      return _allLocations;
   }

   private TripManager()
   {
   }

   private void initAllLocations()
   {
      // TODO: this should be reading from some persistent storage, but for now....
      _allLocations.add("-- Other --");
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

}
