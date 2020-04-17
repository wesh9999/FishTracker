package org.weshley.fishtracker;

public class SpeciesChangeEvent
{
   private String _species = null;

   public SpeciesChangeEvent(String s)
   {
      _species = s;
   }

   public String getSpecies()
   {
      return _species;
   }
}
