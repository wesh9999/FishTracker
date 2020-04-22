package org.weshley.fishtracker;

public class FishWeight
{
   public enum Units { lbs, kg }

   private double _weight;
   private Units _units;

   public FishWeight(double weight)
   {
      _weight = weight;
      _units = Config.getDefaultFishWeightUnits();
   }

   public FishWeight(double weight, Units units)
   {
      _weight = weight;
      _units = units;
   }

   public double getWeight()
   {
      return _weight;
   }

   public Units getUnits()
   {
      return _units;
   }
}
