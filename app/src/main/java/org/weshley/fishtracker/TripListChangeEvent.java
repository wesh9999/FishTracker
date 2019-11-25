package org.weshley.fishtracker;

public class TripListChangeEvent
{
   private Trip _changedTrip = null;

   public TripListChangeEvent(Trip t)
   {
      _changedTrip = t;
   }

   public Trip getChangedTrip()
   {
      return _changedTrip;
   }
}
