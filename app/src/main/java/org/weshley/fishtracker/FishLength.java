package org.weshley.fishtracker;

public class FishLength
{
   public enum Units { in, cm }

   private int _length;
   private Units _units;

   public FishLength(int length)
   {
      _length = length;
      _units = Config.getDefaultFishLengthUnits();
   }

   public FishLength(int length, Units units)
   {
      _length = length;
      _units = units;
   }

   public int getLength()
   {
      return _length;
   }

   public Units getUnits()
   {
      return _units;
   }
}
