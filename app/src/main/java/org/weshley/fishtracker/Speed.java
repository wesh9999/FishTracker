package org.weshley.fishtracker;

public class Speed
{
   public enum Units { mph, kph }

   private int _speed;
   private Units _units;

   public Speed(int speed)
   {
      _speed = speed;
      _units = Config.getDefaultWindSpeedUnits();
   }

   public Speed(int speed, Units units)
   {
      _speed = speed;
      _units = units;
   }

   public int getSpeed()
   {
      return _speed;
   }

   public Units getUnits()
   {
      return _units;
   }

}
