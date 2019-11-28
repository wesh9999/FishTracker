package org.weshley.fishtracker;

public class SelectedTripChangeEvent
{
   private Trip _selectedTrip = null;

   public SelectedTripChangeEvent(Trip t)
   {
      _selectedTrip = t;
   }

   public Trip getSelectedTrip()
   {
      return _selectedTrip;
   }
}
