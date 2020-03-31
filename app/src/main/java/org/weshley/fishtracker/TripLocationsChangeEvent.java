package org.weshley.fishtracker;

public class TripLocationsChangeEvent
{
   private String _location = null;

   public TripLocationsChangeEvent(String loc)
   {
      _location = loc;
   }

   public String getLocation()
   {
      return _location;
   }
}
