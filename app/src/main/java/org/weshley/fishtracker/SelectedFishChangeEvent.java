package org.weshley.fishtracker;

public class SelectedFishChangeEvent
{
   private Fish _fish = null;

   public SelectedFishChangeEvent(Fish f)
   {
      _fish = f;
   }

   public Fish getSelectedFish()
   {
      return _fish;
   }
}
