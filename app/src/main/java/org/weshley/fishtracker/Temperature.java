package org.weshley.fishtracker;

public class Temperature
{
   public enum Units { F, C }

   private int _temp;
   private Units _units;

   public Temperature(int temp)
   {
      _temp = temp;
      _units = Config.getDefaultTempUnits();
   }

   public Temperature(int temp, Units units)
   {
      _temp = temp;
      _units = units;
   }

   public int getTemp()
   {
      return _temp;
   }

   public Units getUnits()
   {
      return _units;
   }
}
