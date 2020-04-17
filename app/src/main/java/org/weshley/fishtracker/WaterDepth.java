package org.weshley.fishtracker;

public class WaterDepth
{
   public enum Units { ft, m }

   private double _depth;
   private Units _units;

   public WaterDepth(double depth)
   {
      _depth = depth;
      _units = Config.getDefaultWaterDepthUnits();
   }

   public WaterDepth(double depth, Units units)
   {
      _depth = depth;
      _units = units;
   }

   public double getDepth()
   {
      return _depth;
   }

   public Units getUnits()
   {
      return _units;
   }
}
