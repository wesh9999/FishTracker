package org.weshley.fishtracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

// TODO: need a UI to edit these values, maybe some way to switch between
//       named configs, persist & load them, etc
public class Config
{
   private static final String DATE_FORMAT = "yyyy/MM/dd";
   private static final String TIME_FORMAT = "h:mm a";

   public static final String OTHER_LABEL = "-- Other --";

   public static final String BLANK_LABEL = "";
     // used in spinners as a "no selection" entry

   public static DateFormat getDateTimeFormat()
   {
      return new SimpleDateFormat(DATE_FORMAT + " " + TIME_FORMAT);
   }

   public static DateFormat getDateFormat()
   {
      return new SimpleDateFormat(DATE_FORMAT);
   }

   public static DateFormat getTimeFormat()
   {
      return new SimpleDateFormat(TIME_FORMAT);
   }

   public static Temperature.Units getDefaultTempUnits() { return Temperature.Units.F; }

   public static Speed.Units getDefaultWindSpeedUnits() { return Speed.Units.mph; }

   public static WaterDepth.Units getDefaultWaterDepthUnits() { return WaterDepth.Units.ft; }

   public static FishLength.Units getDefaultFishLengthUnits() { return FishLength.Units.in; }

   public static FishWeight.Units getDefaultFishWeightUnits() { return FishWeight.Units.lbs; }

}
