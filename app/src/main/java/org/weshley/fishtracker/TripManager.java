package org.weshley.fishtracker;

import android.util.Log;

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
   private Trip _currentTrip = null;
   private List<Trip> _trips = new ArrayList<Trip>();
   private Set<TripListChangeListener> _tripListChangeListeners = new HashSet<TripListChangeListener>();


   private static void initTestData()
   {
      Object[][] data =
      {
         { "2018/05/09 9:00 AM", "2018/05/09 10:00 AM", "Lake Hartwell" },
         { "2019/07/09 2:00 PM", "2019/07/09 4:00 PM", "Lake Hartwell" },
         { "2019/06/02 12:00 AM", "2019/06/02 2:00 PM", "Lake Hartwell" }
      };

      DateFormat fmt = Config.getDateFormat();
      Calendar cal = Calendar.getInstance();
      int batches = 1;
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
               if(null == _instance._currentTrip)
                  _instance._currentTrip = t;
            }
            catch(Exception ex)
            {
               Logger.error("Unexpeced error generating demo data", ex);
               ex.printStackTrace();
            }
         }
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

   public List<Trip> getTrips()
   {
      return _trips;
   }

   public boolean hasCurrentTrip()
   {
      return null != _currentTrip;
   }

   public Trip getCurrentTrip()
   {
      return _currentTrip;
   }

   public boolean isCurrentTrip(Trip t)
   {
      return _currentTrip == t;
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

   public Trip startTrip()
   {
      endTrip();
      _currentTrip = new Trip();
      _trips.add(_currentTrip);
      fireTripListChanged(_currentTrip);
      return _currentTrip;
   }

   public void endTrip()
   {
      if(null != _currentTrip)
         _currentTrip.finalize();
      _currentTrip = null;
      fireTripListChanged(_currentTrip);
   }

   public void deleteTrip(Trip t)
   {
      _trips.remove(t);
      if(t == _currentTrip)
         _currentTrip = null;
      fireTripListChanged(t);
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

}
